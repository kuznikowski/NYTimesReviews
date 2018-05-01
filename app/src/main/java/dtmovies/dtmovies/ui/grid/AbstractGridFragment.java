package dtmovies.dtmovies.ui.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.data.MoviesContract;
import dtmovies.dtmovies.ui.ItemOffsetDecoration;
import dtmovies.dtmovies.util.OnItemClickListener;
import dtmovies.dtmovies.util.OnItemSelectedListener;

public abstract class AbstractGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOADER_ID = 0;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movies_grid)
    RecyclerView recyclerView;
    @BindView(R.id.view_no_movies)
    RelativeLayout noMoviesView;

    private MoviesAdapter adapter;

    private OnItemSelectedListener onItemSelectedListener;
    private GridLayoutManager gridLayoutManager;

    public AbstractGridFragment() {
        // Required empty public constructor
    }

    @NonNull
    protected abstract Uri getContentUri();

    protected abstract void onCursorLoaded(Cursor data);

    protected abstract void onRefreshAction();

    protected abstract void onMoviesGridInitialisationFinished();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            onItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new IllegalStateException("The activity must implement " +
                    OnItemSelectedListener.class.getSimpleName() + " interface.");
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        initSwipeRefreshLayout();
        recyclerView.addItemDecoration(new ItemOffsetDecoration(Objects.requireNonNull(getActivity()), R.dimen.movie_item_offset));
        initMoviesGrid();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGridLayout();
    }

    OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, AbstractGridFragment.this);
    }

    void updateGridLayout() {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noMoviesView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMoviesView.setVisibility(View.GONE);
        }
    }

    void initMoviesGrid() {
        adapter = new MoviesAdapter(getContext(), null);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(gridLayoutManager);
        onMoviesGridInitialisationFinished();
    }

    @SuppressLint("PrivateResource")
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_material_dark,
                R.color.accent_material_light);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(Objects.requireNonNull(getActivity()),
                getContentUri(),
                null,
                null,
                null,
                MoviesContract.MovieEntry.COLUMN_PUBLICATION_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        onCursorLoaded(data);
        updateGridLayout();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.changeCursor(null);
        updateGridLayout();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        onItemSelectedListener.onItemSelected(adapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        onRefreshAction();
    }

    MoviesAdapter getAdapter() {
        return adapter;
    }

    GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }
}
