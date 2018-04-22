package dtmovies.dtmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("PMD.GodClass")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(long id, String headline) {
        this.id = id;
        this.headline = headline;
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.displayTitle = in.readString();
        this.mpaaRating = in.readString();
        this.criticsPick = in.readInt();
        this.byline = in.readString();
        this.headline = in.readString();
        this.summaryShort = in.readString();
        this.publicationDate = in.readString();
        this.setLink(new Link(in.readString(), in.readString()));
        this.setMultimedia(new Multimedia(in.readString()));
    }

    @SerializedName("id")
    private long id;

    @SerializedName("display_title")
    @Expose
    private String displayTitle;

    @SerializedName("mpaa_rating")
    @Expose
    private String mpaaRating;

    @SerializedName("critics_pick")
    @Expose
    private Integer criticsPick;

    @SerializedName("byline")
    @Expose
    private String byline;

    @SerializedName("headline")
    @Expose
    private String headline;

    @SerializedName("summary_short")
    @Expose
    private String summaryShort;

    @SerializedName("publication_date")
    @Expose
    private String publicationDate;

    @SerializedName("opening_date")
    @Expose
    private String openingDate;

    @SerializedName("date_updated")
    @Expose
    private String dateUpdated;

    @SerializedName("link")
    @Expose
    private Link link;

    @SerializedName("multimedia")
    @Expose
    private Multimedia multimedia;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public Integer getCriticsPick() {
        return criticsPick;
    }

    public void setCriticsPick(Integer criticsPick) {
        this.criticsPick = criticsPick;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummaryShort() {
        return summaryShort;
    }

    public void setSummaryShort(String summaryShort) {
        this.summaryShort = summaryShort;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.id);
        parcel.writeString(this.displayTitle);
        parcel.writeString(this.mpaaRating);
        parcel.writeInt(this.criticsPick);
        parcel.writeString(this.byline);
        parcel.writeString(this.headline);
        parcel.writeString(this.summaryShort);
        parcel.writeString(this.publicationDate);
        parcel.writeString(this.getLink().getUrl());
        parcel.writeString(this.getLink().getSuggestedLinkText());
        if (this.getMultimedia() != null && this.getMultimedia().getSrc() != null) {
            parcel.writeString(this.getMultimedia().getSrc());
        } else {
            parcel.writeString("");
        }
    }

    public static Movie fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry._ID));
        String headline = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_HEADLINE));
        Movie movie = new Movie(id, headline);
        movie.setDisplayTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_DISPLAY_TITLE)));
        movie.setCriticsPick(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_CRITICS_PICK)));
        movie.setByline(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BYLINE)));
        movie.setHeadline(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_HEADLINE)));
        movie.setSummaryShort(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SUMMARY_SHORT)));
        movie.setPublicationDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_PUBLICATION_DATE)));
        movie.setLink(new Link(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ARTICLE_LINK)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_LINK_TEXT))));
        movie.setMultimedia(new Multimedia(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE_SRC))));
        return movie;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_DISPLAY_TITLE, displayTitle);
        values.put(MoviesContract.MovieEntry.COLUMN_CRITICS_PICK, criticsPick);
        values.put(MoviesContract.MovieEntry.COLUMN_BYLINE, byline);
        values.put(MoviesContract.MovieEntry.COLUMN_HEADLINE, headline);
        values.put(MoviesContract.MovieEntry.COLUMN_SUMMARY_SHORT, summaryShort);
        values.put(MoviesContract.MovieEntry.COLUMN_PUBLICATION_DATE, publicationDate);
        values.put(MoviesContract.MovieEntry.COLUMN_ARTICLE_LINK, getLink().getUrl());
        values.put(MoviesContract.MovieEntry.COLUMN_LINK_TEXT, getLink().getSuggestedLinkText());
        values.put(MoviesContract.MovieEntry.COLUMN_IMAGE_SRC, getMultimedia().getSrc());
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}