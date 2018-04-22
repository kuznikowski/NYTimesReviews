package dtmovies.dtmovies.api;

import dtmovies.dtmovies.data.Movie;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface ReviewsService {

    @GET("search.json")
    Observable<ResponseModel<Movie>> latestReviews(@Query("query") String query,
                                                   @Query("offset") Integer offset);

    @GET("search.json")
    Observable<ResponseModel<Movie>> searchMovies(@Query("query") String query,
                                                  @Query("offset") Integer offset);
}