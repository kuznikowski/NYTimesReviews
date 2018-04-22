package dtmovies.dtmovies.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel<T> {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("copyright")
    @Expose
    private String copyright;

    @SerializedName("num_results")
    @Expose
    private Integer numResults;

    @SerializedName("has_more")
    @Expose
    private Boolean hasMore;

    @SerializedName("results")
    @Expose
    private List<T> movies;

    public ResponseModel(String status, String copyright, Integer numResults, Boolean hasMore, List<T> movies) {
        this.status = status;
        this.copyright = copyright;
        this.numResults = numResults;
        this.hasMore = hasMore;
        this.movies = movies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public List<T> getMovies() {
        return movies;
    }

    public void setMovies(List<T> movies) {
        this.movies = movies;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}