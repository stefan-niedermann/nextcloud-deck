package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryItemViewHolder> {

    @NonNull
    List<Long> imageIds = new ArrayList<>();

    public GalleryAdapter() {
        setHasStableIds(true);
    }

    public void setImageIds(@NonNull Collection<Long> imageIds) {
        this.imageIds.clear();
        this.imageIds.addAll(imageIds);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryItemViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        holder.bind(imageIds.get(position));
    }

    @Override
    public int getItemCount() {
        return this.imageIds.size();
    }
}
