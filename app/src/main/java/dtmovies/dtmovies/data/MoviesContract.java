package dtmovies.dtmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */

public final class MoviesContract {

    public static final String CONTENT_AUTHORITY = "dtmovies.dtmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_LATEST_REVIEWS = "latest_reviews";
    public static final String PATH_FAVORITES = "favorites";

    public static final String COLUMN_MOVIE_ID_KEY = "headline";

    private MoviesContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_DISPLAY_TITLE = "display_title";
        public static final String COLUMN_CRITICS_PICK = "critics_pick";
        public static final String COLUMN_BYLINE = "byline";
        public static final String COLUMN_HEADLINE = "headline";
        public static final String COLUMN_SUMMARY_SHORT = "summary_short";
        public static final String COLUMN_PUBLICATION_DATE = "publication_date";
        public static final String COLUMN_ARTICLE_LINK = "article_link";
        public static final String COLUMN_LINK_TEXT = "link_text";
        public static final String COLUMN_IMAGE_SRC = "image_src";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DISPLAY_TITLE + " TEXT, " +
                        COLUMN_CRITICS_PICK + " INTEGER, " +
                        COLUMN_BYLINE + " TEXT, " +
                        COLUMN_HEADLINE + " VARCHAR(255) UNIQUE, " +
                        COLUMN_SUMMARY_SHORT + " TEXT, " +
                        COLUMN_PUBLICATION_DATE + " TEXT, " +
                        COLUMN_ARTICLE_LINK + " TEXT," +
                        COLUMN_LINK_TEXT + " TEXT, " +
                        COLUMN_IMAGE_SRC + " TEXT " +
                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_DISPLAY_TITLE, COLUMN_CRITICS_PICK,
                COLUMN_BYLINE, COLUMN_HEADLINE, COLUMN_SUMMARY_SHORT, COLUMN_PUBLICATION_DATE,
                COLUMN_ARTICLE_LINK, COLUMN_LINK_TEXT, COLUMN_IMAGE_SRC};

        private MovieEntry() {
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class LatestReviews implements BaseColumns {
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_LATEST_REVIEWS)
                .build();

        public static final String TABLE_NAME = "latest_reviews";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " TEXT NOT NULL" +
                        " );";

        private LatestReviews() {
        }
    }

    public static final class Favorites implements BaseColumns {
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorites";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " TEXT NOT NULL" +
                        " );";

        private Favorites() {
        }
    }
}