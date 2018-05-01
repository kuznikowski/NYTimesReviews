package dtmovies.dtmovies.ui.grid;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dtmovies.dtmovies.R;
import dtmovies.dtmovies.util.OnItemClickListener;

public class GridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.image_movie_poster)
    ImageView moviePoster;

    @BindView(R.id.movie_title)
    TextView movieTitle;

    private final OnItemClickListener onItemClickListener;

    GridItemViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
