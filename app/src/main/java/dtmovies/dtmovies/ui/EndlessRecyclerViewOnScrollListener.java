package dtmovies.dtmovies.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 7; // set number of remaining items to swipe when loading more items

    private final GridLayoutManager gridLayoutManager;
    private boolean loading = false;

    protected EndlessRecyclerViewOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = gridLayoutManager.getItemCount();
        int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();

        // decide whether load more items
        boolean endHasBeenReached = lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount;
        if (!loading && totalItemCount > 0 && endHasBeenReached) {
            loading = true;
            onLoadMore();
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    protected abstract void onLoadMore();
}
