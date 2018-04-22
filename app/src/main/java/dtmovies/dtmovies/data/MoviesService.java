package dtmovies.dtmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import javax.inject.Inject;

import dtmovies.dtmovies.api.ResponseModel;
import dtmovies.dtmovies.api.ReviewsService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoviesService {

    public static final String BROADCAST_UPDATE_FINISHED = "UpdateFinished";
    public static final String EXTRA_IS_SUCCESSFUL_UPDATED = "isSuccessfulUpdated";

    private static final String LOG_TAG = "MoviesService";

    private final Context context;
    private volatile boolean loading = false;

    private final ReviewsService reviewsService;

    @Inject
    public MoviesService(Context context, ReviewsService reviewsService) {
        this.context = context.getApplicationContext();
        this.reviewsService = reviewsService;
    }

    public void refreshMovies() {
        if (loading) {
            return;
        }
        loading = true;

        callDiscoverMovies(null);
    }

    public boolean isLoading() {
        return loading;
    }

    public void loadMoreMovies() {
        if (loading) {
            return;
        }
        loading = true;
        Uri uri = MoviesContract.LatestReviews.CONTENT_URI;
        if (uri == null) {
            return;
        }
        callDiscoverMovies(getOffset(uri));
    }

    private void callDiscoverMovies(@Nullable Integer offset) {
        reviewsService.latestReviews("", offset)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(this::clearMoviesSortTableIfNeeded)
                .map(ResponseModel::getMovies)
                .flatMap(Observable::fromIterable)
                .map(this::saveMovie)
                .map(MoviesContract.MovieEntry::getIdFromUri)
                .doOnNext(this::saveMovieReference)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onComplete() {
                        loading = false;
                        sendUpdateFinishedBroadcast(true);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loading = false;
                        sendUpdateFinishedBroadcast(false);
                        Log.e(LOG_TAG, "Error", e);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        // do nothing
                    }
                });
    }

    private void saveMovieReference(Long movieId) {
    }

    private Uri saveMovie(Movie movie) {
        ContentValues entry = new ContentValues();
        entry.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movie.getHeadline());
        context.getContentResolver().insert(MoviesContract.LatestReviews.CONTENT_URI, entry);
        return context.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movie.toContentValues());
    }

    private void clearMoviesSortTableIfNeeded(ResponseModel<Movie> movie) {
        if (!movie.getHasMore()) {
            context.getContentResolver().delete(
                    MoviesContract.LatestReviews.CONTENT_URI,
                    null,
                    null
            );
        }
    }

    private void sendUpdateFinishedBroadcast(boolean successfulUpdated) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(EXTRA_IS_SUCCESSFUL_UPDATED, successfulUpdated);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private int getOffset(Uri uri) {
        Cursor movies = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        int offset = 0;
        if (movies != null) {
            offset = movies.getCount();
            movies.close();
        }
        return offset;
    }
}
