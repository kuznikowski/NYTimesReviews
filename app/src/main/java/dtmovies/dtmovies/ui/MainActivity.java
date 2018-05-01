package dtmovies.dtmovies.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dtmovies.dtmovies.NYTimesReviews;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.data.Movie;
import dtmovies.dtmovies.ui.detail.MovieDetailActivity;
import dtmovies.dtmovies.ui.detail.MovieDetailFragment;
import dtmovies.dtmovies.ui.grid.FavoritesGridFragment;
import dtmovies.dtmovies.ui.grid.GridFragment;
import dtmovies.dtmovies.util.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_MOVIE = "selectedMovie";
    private static final String SELECTED_NAVIGATION_ITEM = "selectedNavigationItem";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.movie_detail_container)
    ScrollView movieDetailContainer;

    private boolean twoPaneMode;
    private Movie selectedMovie = null;
    private int selectedNavigationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((NYTimesReviews) getApplication()).getNetworkComponent().inject(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_grid_container, GridFragment.create())
                    .commit();
        }


        twoPaneMode = movieDetailContainer != null;
        if (twoPaneMode && selectedMovie == null) {
            movieDetailContainer.setVisibility(View.GONE);
        }

        setupToolbar();
        setupNavigationDrawer();
        setupNavigationView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle();
    }

    // Sets the app title depending on the chosen navigation item
    private void updateTitle() {
        if (selectedNavigationItem == 0) {
            setTitle(getResources().getString(R.string.grid_title));
        } else if (selectedNavigationItem == 1) {
            setTitle(getResources().getString(R.string.navigation_item_favorites));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_MOVIE, selectedMovie);
        outState.putInt(SELECTED_NAVIGATION_ITEM, selectedNavigationItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectedMovie = savedInstanceState.getParcelable(SELECTED_MOVIE);
            selectedNavigationItem = savedInstanceState.getInt(SELECTED_NAVIGATION_ITEM);
            Menu menu = navigationView.getMenu();
            menu.getItem(selectedNavigationItem).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.VISIBLE);
            selectedMovie = movie;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.create(movie))
                    .commit();
        } else {
            MovieDetailActivity.start(this, movie);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.drawer_item_explore:
                if (selectedNavigationItem != 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, GridFragment.create())
                            .commit();
                    selectedNavigationItem = 0;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                updateTitle();
                return true;
            case R.id.drawer_item_favorites:
                if (selectedNavigationItem != 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, FavoritesGridFragment.create())
                            .commit();
                    selectedNavigationItem = 1;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                updateTitle();
                return true;
            default:
                return false;
        }
    }

    private void hideMovieDetailContainer() {
        selectedMovie = null;
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupNavigationDrawer() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }
}