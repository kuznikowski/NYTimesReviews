package dtmovies.dtmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class MoviesProvider extends ContentProvider {

    static final int MOVIES = 100;
    static final int MOVIE_BY_ID = 101;
    static final int LATEST_REVIEWS = 201;
    static final int FAVORITES = 300;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private static final String FAILED_TO_INSERT_ROW_INTO = "Failed to insert row into ";

    private MoviesDbHelper dbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_BY_ID);

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" +
                MoviesContract.PATH_LATEST_REVIEWS, LATEST_REVIEWS);

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" +
                MoviesContract.PATH_FAVORITES, FAVORITES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        Cursor cursor;
        checkColumns(projection);
        switch (match) {
            case LATEST_REVIEWS:
                cursor = getMoviesFromReferenceTable(MoviesContract.LatestReviews.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case FAVORITES:
                if (selection != null) {
                    cursor = findFavorites(MoviesContract.Favorites.TABLE_NAME,
                            projection, selection, selectionArgs, sortOrder);
                } else {
                    cursor = getMoviesFromReferenceTable(MoviesContract.Favorites.TABLE_NAME,
                            projection, null, selectionArgs, sortOrder);
                }
                break;
            default:
                return null;
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;
        long id;
        switch (match) {
            case MOVIES:
                id = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case LATEST_REVIEWS:
                id = db.insert(MoviesContract.LatestReviews.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.LatestReviews.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case FAVORITES:
                id = db.insert(MoviesContract.Favorites.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.Favorites.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;
        switch (match) {
            case LATEST_REVIEWS:
                rowsDeleted = db.delete(MoviesContract.LatestReviews.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(MoviesContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

    private Cursor getMoviesFromReferenceTable(String tableName, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        sqLiteQueryBuilder.setTables(
                tableName + " INNER JOIN " + MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + tableName + "." + MoviesContract.COLUMN_MOVIE_ID_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_HEADLINE
        );

        return sqLiteQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor findFavorites(String tableName, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        sqLiteQueryBuilder.setTables(tableName);

        return sqLiteQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(
                    MoviesContract.MovieEntry.getColumns()));
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection.");
            }
        }
    }
}