package it.niedermann.nextcloud.deck.ui.attachments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {

    private final Account account;
    private final long cardRemoteId;
    @NonNull
    private final List<Attachment> attachments = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    public AttachmentAdapter(@NonNull Account account, long cardRemoteId, @NonNull List<Attachment> attachments) {
        super();
        this.attachments.clear();
        this.attachments.addAll(attachments);
        this.account = account;
        this.cardRemoteId = cardRemoteId;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        return new AttachmentViewHolder(context, ItemAttachmentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        holder.bind(account, attachments.get(position), cardRemoteId);
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }
}
