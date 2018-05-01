package dtmovies.dtmovies.ui.detail;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import dtmovies.dtmovies.NYTimesReviews;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.api.ReviewsService;
import dtmovies.dtmovies.data.Movie;

public class MovieDetailFragment extends RxFragment {

    private static final String ARG_MOVIE = "ArgMovie";

    @BindView(R.id.text_byline)
    TextView reviewByline;
    @BindView(R.id.text_movie_display_title)
    TextView movieDisplayTitle;
    @BindView(R.id.text_movie_critics_pick)
    TextView movieCriticsPick;
    @BindView(R.id.text_movie_publication_date)
    TextView moviePublicationDate;
    @BindView(R.id.text_movie_summary)
    TextView movieSummary;
    @BindView(R.id.text_article_link)
    TextView reviewArticle;
    @BindView(R.id.card_movie_detail)
    CardView cardMovieDetail;
    @BindView(R.id.card_movie_summary)
    CardView cardMovieSummary;

    @Inject
    ReviewsService reviewsService;

    private Movie movie;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment create(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }

        ((NYTimesReviews) Objects.requireNonNull(getActivity()).getApplication()).getNetworkComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        setupCardsElevation();
        return rootView;
    }

    private void setupCardsElevation() {
        setupCardElevation(cardMovieDetail);
        setupCardElevation(cardMovieSummary);
    }

    private void setupCardElevation(View view) {
        ViewCompat.setElevation(view,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
    }

    private void initViews() {
        movieDisplayTitle.setText(movie.getHeadline());
        if (movie.getCriticsPick() == 1) movieCriticsPick.setText(getString(R.string.yes));
        String publicationDate = String.format(getString(R.string.movie_detail_publication_date),
                movie.getPublicationDate());
        moviePublicationDate.setText(publicationDate);
        movieSummary.setText(movie.getSummaryShort());
        reviewByline.setText(movie.getByline());
        reviewArticle.setText(movie.getLink().getSuggestedLinkText());
        reviewArticle.setPaintFlags(reviewArticle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        reviewArticle.setOnClickListener(v -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getLink().getUrl()));
            startActivity(browser);
        });
    }

    private float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
