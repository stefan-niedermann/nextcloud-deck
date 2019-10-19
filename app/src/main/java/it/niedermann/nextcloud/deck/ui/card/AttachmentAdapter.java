package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    private final Account account;
    private final long cardRemoteId;
    @NonNull
    private List<Attachment> attachments;
    private Context context;

    AttachmentAdapter(@NonNull Account account, long cardRemoteId, @NonNull List<Attachment> attachments) {
        super();
        this.attachments = attachments;
        this.account = account;
        this.cardRemoteId = cardRemoteId;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_attachment, parent, false);
        return new AttachmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        Attachment attachment = attachments.get(position);
        holder.filename.setText(attachment.getFilename());
        holder.filesize.setText(Formatter.formatFileSize(context, attachment.getFilesize()));
        holder.modified.setText(DateUtils.getRelativeTimeSpanString(context, attachment.getLastModified().getTime()));
        holder.filename.getRootView().setOnClickListener((event) -> {
            Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
            openURL.setData(Uri.parse(account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId()));
            context.startActivity(openURL);
        });
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.filename)
        TextView filename;
        @BindView(R.id.filesize)
        TextView filesize;
        @BindView(R.id.modified)
        TextView modified;

        private AttachmentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
