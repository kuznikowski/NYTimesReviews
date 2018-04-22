package dtmovies.dtmovies.ui.grid;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dtmovies.dtmovies.DTMovies;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.api.ResponseModel;
import dtmovies.dtmovies.api.ReviewsService;
import dtmovies.dtmovies.data.Movie;
import dtmovies.dtmovies.data.MoviesContract;
import dtmovies.dtmovies.data.MoviesService;
import dtmovies.dtmovies.ui.EndlessRecyclerViewOnScrollListener;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoviesGridFragment extends AbstractMoviesGridFragment {

    private static final String LOG_TAG = "MoviesGridFragment";
    private static final int SEARCH_QUERY_DELAY_MILLIS = 500;

    @Inject
    MoviesService moviesService;

    @Inject
    ReviewsService reviewsService;

    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;
    private SearchView searchView;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.requireNonNull(action).equals(MoviesService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(MoviesService.EXTRA_IS_SUCCESSFUL_UPDATED, true)) {
                    Snackbar.make(swipeRefreshLayout, R.string.error_failed_to_update_movies,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeRefreshLayout.setRefreshing(false);
                endlessRecyclerViewOnScrollListener.setLoading(false);
                updateGridLayout();
            }
        }
    };

    public static MoviesGridFragment create() {
        return new MoviesGridFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((DTMovies) Objects.requireNonNull(getActivity()).getApplication()).getNetworkComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.BROADCAST_UPDATE_FINISHED);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(broadcastReceiver, intentFilter);
        if (endlessRecyclerViewOnScrollListener != null) {
            endlessRecyclerViewOnScrollListener.setLoading(moviesService.isLoading());
        }
        swipeRefreshLayout.setRefreshing(moviesService.isLoading());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies_grid, menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        if (searchViewMenuItem != null) {
            searchView = (SearchView) searchViewMenuItem.getActionView();
            searchViewMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    recyclerView.setAdapter(null);
                    initMoviesGrid();
                    restartLoader();
                    swipeRefreshLayout.setEnabled(true);
                    return true;
                }
            });

            setupSearchView();
        }
    }

    @Override
    @NonNull
    protected Uri getContentUri() {
        return MoviesContract.LatestReviews.CONTENT_URI;
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        getAdapter().changeCursor(data);
        if (data == null || data.getCount() == 0) {
            refreshMovies();
        }
    }

    @Override
    protected void onRefreshAction() {
        refreshMovies();
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(getGridLayoutManager()) {
            @Override
            public void onLoadMore() {
                swipeRefreshLayout.setRefreshing(true);
                moviesService.loadMoreMovies();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
    }

    private void setupSearchView() {
        if (searchView == null) {
            Log.e(LOG_TAG, "SearchView is not initialized");
            return;
        }
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(getActivity().getComponentName()));

        RxSearchView.queryTextChanges(searchView)
                .debounce(SEARCH_QUERY_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .filter(query -> query.length() > 0)
                .doOnNext(query -> Log.d("search", query))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .switchMap(query -> reviewsService.searchMovies(query, null))
                .map(ResponseModel::getMovies)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Movie>>() {
                    @Override
                    public void onComplete() {
                        // do nothing
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        // do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "Error", e);
                    }

                    @Override
                    public void onNext(List<Movie> results) {
                        MoviesSearchAdapter adapter = new MoviesSearchAdapter(getContext(), results);
                        adapter.setOnItemClickListener((itemView, position) ->
                                getOnItemSelectedListener().onItemSelected(adapter.getItem(position))
                        );
                        recyclerView.setAdapter(adapter);
                        updateGridLayout();
                    }
                });

        searchView.setOnSearchClickListener(view -> {
            recyclerView.setAdapter(null);
            recyclerView.removeOnScrollListener(endlessRecyclerViewOnScrollListener);
            updateGridLayout();
            swipeRefreshLayout.setEnabled(false);
        });
    }

    private void refreshMovies() {
        swipeRefreshLayout.setRefreshing(true);
        moviesService.refreshMovies();
    }
}
