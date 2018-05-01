package dtmovies.dtmovies.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dtmovies.dtmovies.NYTimesReviews;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.data.FavoritesService;
import dtmovies.dtmovies.data.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String MOVIE_TO_SHOW = "movieToShow";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.backdrop_image)
    ImageView movieBackdropImage;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @Inject
    FavoritesService favoritesService;

    private Movie movie;

    public static void start(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(MOVIE_TO_SHOW, movie);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        movie = getIntent().getParcelableExtra(MOVIE_TO_SHOW);

        ((NYTimesReviews) getApplication()).getNetworkComponent().inject(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_grid_container, MovieDetailFragment.create(movie))
                    .commit();
        }
        initToolbar();
        ViewCompat.setElevation(nestedScrollView,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(fab,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_fab_elevation_in_dp)));
        updateFab();
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        if (favoritesService.isFavorite(movie)) {
            favoritesService.removeFromFavorites(movie);
            showSnackbar(R.string.message_removed_from_favorites);
        } else {
            favoritesService.addToFavorites(movie);
            showSnackbar(R.string.message_added_to_favorites);
        }
        updateFab();
    }


    private void updateFab() {
        if (favoritesService.isFavorite(movie)) {
            fab.setImageResource(R.drawable.ic_favorite_white);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_white_border);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
        collapsingToolbarLayout.setTitle(movie.getDisplayTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        setTitle("");

        if (movie.getMultimedia() == null || movie.getMultimedia().getSrc() == null) {
            movieBackdropImage.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.accent_material_light)));
        } else {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(movie.getMultimedia().getSrc())
                    .transition(new DrawableTransitionOptions()
                            .crossFade())
                    .apply(requestOptions)
                    .into(movieBackdropImage);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(@StringRes int messageResourceId) {
        showSnackbar(getString(messageResourceId));
    }

    private float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}