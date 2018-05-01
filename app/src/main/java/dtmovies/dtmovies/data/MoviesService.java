package dtmovies.dtmovies.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dtmovies.dtmovies.api.ResponseModel;
import dtmovies.dtmovies.api.ReviewsService;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoviesService {

    public static final String UPDATE_FINISHED = "updateFinished";
    public static final String IS_SUCCESSFUL_UPDATED = "isSuccessfulUpdated";

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
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        if (uri == null) {
            return;
        }

        callDiscoverMovies(getOffset(uri));
    }

    private void callDiscoverMovies(@Nullable Integer offset) {
        reviewsService.latestReviews(offset)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseModel::getMovies)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Movie>>() {
                    @Override
                    public void onComplete() {
                        loading = false;
                        sendUpdateFinishedBroadcast(true);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        // do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        loading = false;
                        sendUpdateFinishedBroadcast(false);
                    }

                    @Override
                    public void onNext(List<Movie> results) {
                        saveMovies(results);
                    }
                });
    }

    public void saveMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            context.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movie.toContentValues());
        }
    }

    public List<Movie> findMoviesOffline(String title) {
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.COLUMN_DISPLAY_TITLE + " LIKE \"%" + title + "%\"",
                null,
                MoviesContract.MovieEntry.COLUMN_PUBLICATION_DATE + " DESC"
        );

        if (cursor == null) return null;

        List<Movie> results = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            results.add(Movie.fromCursor(cursor));
        }

        cursor.close();
        return results;
    }

    private void sendUpdateFinishedBroadcast(boolean successfulUpdated) {
        Intent intent = new Intent(UPDATE_FINISHED);
        intent.putExtra(IS_SUCCESSFUL_UPDATED, successfulUpdated);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private int getOffset(Uri uri) {
        Cursor movies = context.getContentResolver().query(uri, null, null, null, null);
        int offset = 0;

        if (movies != null) {
            offset = movies.getCount();
            movies.close();
        }

        return offset;
    }
}