package dtmovies.dtmovies.api;

import dtmovies.dtmovies.data.Movie;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface ReviewsService {

    // Get the latest reviews
    @GET("search.json")
    Observable<ResponseModel<Movie>> latestReviews(@Query("offset") Integer offset);

    // Search reviews by query
    @GET("search.json")
    Observable<ResponseModel<Movie>> searchReviews(@Query("query") String query);
}