package dtmovies.dtmovies.ui.grid;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.List;

import dtmovies.dtmovies.R;
import dtmovies.dtmovies.data.Movie;
import dtmovies.dtmovies.util.OnItemClickListener;

public class MoviesSearchAdapter extends ArrayRecyclerViewAdapter<Movie, MovieGridItemViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;

    public MoviesSearchAdapter(Context context, @Nullable List<Movie> items) {
        super(items);
        this.context = context;
    }

    @NonNull
    @Override
    public MovieGridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView, onItemClickListener);
    }

    @Override
    @SuppressLint("PrivateResource")
    public void onBindViewHolder(@NonNull MovieGridItemViewHolder holder, int position) {
        Movie movie = getItems().get(position);
        holder.movieTitle.setText(movie.getDisplayTitle());

        if (movie.getMultimedia() == null || movie.getMultimedia().getSrc() == null) {
            holder.moviePoster.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)));
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
                    .into(holder.moviePoster);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
