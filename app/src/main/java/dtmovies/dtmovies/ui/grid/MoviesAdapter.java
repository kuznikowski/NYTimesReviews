package dtmovies.dtmovies.ui.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import dtmovies.dtmovies.R;
import dtmovies.dtmovies.data.Movie;
import dtmovies.dtmovies.util.CursorRecyclerViewAdapter;
import dtmovies.dtmovies.util.OnItemClickListener;

public class MoviesAdapter extends CursorRecyclerViewAdapter<GridItemViewHolder> {
    private final Context context;
    private OnItemClickListener onItemClickListener;

    MoviesAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    @SuppressLint("PrivateResource")
    public void onBindViewHolder(GridItemViewHolder viewHolder, Cursor cursor) {
        if (cursor != null) {
            Movie movie = Movie.fromCursor(cursor);
            viewHolder.movieTitle.setText(movie.getDisplayTitle());

            if (movie.getMultimedia() == null || movie.getMultimedia().getSrc() == null) {
                viewHolder.moviePoster.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)));
            } else {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.placeholder(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)));
                requestOptions.fitCenter();

                Glide.with(context)
                        .load(movie.getMultimedia().getSrc())
                        .transition(new DrawableTransitionOptions()
                                .crossFade())
                        .apply(requestOptions)
                        .into(viewHolder.moviePoster);
            }
        }

    }

    @NonNull
    @Override
    public GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, parent, false);
        return new GridItemViewHolder(itemView, onItemClickListener);
    }

    @Nullable
    public Movie getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return null;
        }
        if (position < 0 || position > cursor.getCount()) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < position; i++) {
            cursor.moveToNext();
        }
        return Movie.fromCursor(cursor);
    }
}
