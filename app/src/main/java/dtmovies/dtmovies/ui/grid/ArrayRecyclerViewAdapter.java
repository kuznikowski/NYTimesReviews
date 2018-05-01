package dtmovies.dtmovies.ui.grid;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class ArrayRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final List<T> items;

    ArrayRecyclerViewAdapter(@Nullable List<T> items) {
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = items;
        }
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(items).size();
    }

    List<T> getItems() {
        return items;
    }

    @Nullable
    public T getItem(int position) {
        if (items == null) {
            return null;
        }
        if (position < 0 || position > items.size()) {
            return null;
        }
        return items.get(position);
    }
}