package dtmovies.dtmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Objects;

public class MoviesProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int FAVORITES = 300;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private static final String ROW_INSERT_FAILED = "Failed to insert row into ";

    private MoviesDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITES, FAVORITES);

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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int match = URI_MATCHER.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = getMoviesFromTable(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
                break;
            case FAVORITES:
                if (selection != null) {
                    // case adding to/removing from favorites table

                    cursor = getMoviesFromTable(MoviesContract.Favorites.TABLE_NAME, MoviesContract.Favorites.TABLE_NAME,
                            projection, selection, selectionArgs, sortOrder);
                } else {
                    // case showing all favorites

                    cursor = getMoviesFromTable(MoviesContract.Favorites.TABLE_NAME,
                            null , projection, null, selectionArgs, sortOrder);
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
                Cursor cursor = getMoviesFromTable(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        MoviesContract.COLUMN_MOVIE_ID_KEY + " = \"" + String.valueOf(values.get(MoviesContract.MovieEntry.COLUMN_HEADLINE)) + "\"",
                        null,
                        null);

                if (cursor != null && cursor.getCount() == 0) {
                    id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);

                    if (id > 0) {
                        returnUri = MoviesContract.MovieEntry.CONTENT_URI;
                    } else {
                        returnUri = null;
                    }
                    cursor.close();
                } else {
                    returnUri = null;
                }

                break;
            case FAVORITES:
                id = db.insert(MoviesContract.Favorites.TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = MoviesContract.Favorites.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(ROW_INSERT_FAILED + uri);
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
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
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

    private Cursor getMoviesFromTable(String tableName, String tables, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        if (tables != null) {
            sqLiteQueryBuilder.setTables(tables);
        } else {
            sqLiteQueryBuilder.setTables(tableName + " INNER JOIN " + MoviesContract.MovieEntry.TABLE_NAME +
                    " ON " + tableName + "." + MoviesContract.COLUMN_MOVIE_ID_KEY +
                    " = " + MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_HEADLINE
            );
        }

        return sqLiteQueryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, sortOrder
        );
    }
}