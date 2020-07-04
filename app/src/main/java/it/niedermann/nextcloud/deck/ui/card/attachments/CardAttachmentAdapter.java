package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.attachments.AttachmentsActivity;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

@SuppressWarnings("WeakerAccess")
public class CardAttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {

    public static final int VIEW_TYPE_DEFAULT = 2;
    public static final int VIEW_TYPE_IMAGE = 1;

    private final MenuInflater menuInflater;
    @ColorInt
    private int mainColor;
    private final Account account;
    @Nullable
    private Long cardRemoteId = null;
    private final long cardLocalId;
    @NonNull
    FragmentManager fragmentManager;
    @NonNull
    private List<Attachment> attachments = new ArrayList<>();
    @Nullable
    private final AttachmentClickedListener attachmentClickedListener;

    CardAttachmentAdapter(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull MenuInflater menuInflater,
            @Nullable AttachmentClickedListener attachmentClickedListener,
            @NonNull Account account,
            @Nullable Long cardLocalId
    ) {
        super();
        this.fragmentManager = fragmentManager;
        this.menuInflater = menuInflater;
        this.attachmentClickedListener = attachmentClickedListener;
        this.account = account;
        this.cardLocalId = cardLocalId == null ? NO_ID : cardLocalId;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        Long id = attachments.get(position).getLocalId();
        return id == null ? NO_ID : id;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                return new ImageAttachmentViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(context), parent, false));
            case VIEW_TYPE_DEFAULT:
            default:
                return new DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding.inflate(LayoutInflater.from(context), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final Attachment attachment = attachments.get(position);
        final int viewType = getItemViewType(position);

        @Nullable final String uri = (attachment.getId() == null || cardRemoteId == null)
                ? attachment.getLocalPath() :
                AttachmentUtil.getRemoteUrl(account.getUrl(), cardRemoteId, attachment.getId());
        holder.setNotSyncedYetStatus(!DBStatus.LOCAL_EDITED.equals(attachment.getStatusEnum()), mainColor);
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.attachment_menu, menu);
            menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                DeleteAttachmentDialogFragment.newInstance(attachment).show(fragmentManager, DeleteAttachmentDialogFragment.class.getCanonicalName());
                return false;
            });
            if (uri == null) {
                menu.findItem(android.R.id.copyUrl).setVisible(false);
            } else {
                menu.findItem(android.R.id.copyUrl).setOnMenuItemClickListener(item -> copyToClipboard(context, attachment.getFilename(), uri));
            }
        });

        switch (viewType) {
            case VIEW_TYPE_IMAGE: {
                holder.getPreview().setImageResource(R.drawable.ic_image_grey600_24dp);
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_image_grey600_24dp)
                        .error(R.drawable.ic_image_grey600_24dp)
                        .into(holder.getPreview());
                holder.itemView.setOnClickListener((v) -> {
                    if (attachmentClickedListener != null) {
                        attachmentClickedListener.onAttachmentClicked(position);
                    }
                    final Intent intent = AttachmentsActivity.createIntent(context, account, cardLocalId, attachment.getLocalId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && context instanceof Activity) {
                        String transitionName = context.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId()));
                        holder.getPreview().setTransitionName(transitionName);
                        context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.getPreview(), transitionName).toBundle());
                    } else {
                        context.startActivity(intent);
                    }
                });
                break;
            }
            case VIEW_TYPE_DEFAULT:
            default: {
                DefaultAttachmentViewHolder defaultHolder = (DefaultAttachmentViewHolder) holder;

                if (MimeTypeUtil.isAudio(attachment.getMimetype())) {
                    holder.getPreview().setImageResource(R.drawable.ic_music_note_grey600_24dp);
                } else if (MimeTypeUtil.isVideo(attachment.getMimetype())) {
                    holder.getPreview().setImageResource(R.drawable.ic_local_movies_grey600_24dp);
                } else if (MimeTypeUtil.isPdf(attachment.getMimetype())) {
                    holder.getPreview().setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                } else if (MimeTypeUtil.isContact(attachment.getMimetype())) {
                    holder.getPreview().setImageResource(R.drawable.ic_baseline_contact_mail_24);
                } else {
                    holder.getPreview().setImageResource(R.drawable.ic_attach_file_grey600_24dp);
                }

                if (cardRemoteId != null) {
                    defaultHolder.itemView.setOnClickListener((event) -> {
                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                        openURL.setData(Uri.parse(AttachmentUtil.getRemoteUrl(account.getUrl(), cardRemoteId, attachment.getId())));
                        context.startActivity(openURL);
                    });
                }
                defaultHolder.binding.filename.setText(attachment.getBasename());
                defaultHolder.binding.filesize.setText(Formatter.formatFileSize(context, attachment.getFilesize()));
                if (attachment.getLastModifiedLocal() != null) {
                    defaultHolder.binding.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModifiedLocal().getTime()));
                    defaultHolder.binding.modified.setVisibility(View.VISIBLE);
                } else if (attachment.getLastModified() != null) {
                    defaultHolder.binding.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModified().getTime()));
                    defaultHolder.binding.modified.setVisibility(View.VISIBLE);
                } else {
                    defaultHolder.binding.modified.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return MimeTypeUtil.isImage(attachments.get(position).getMimetype()) ? VIEW_TYPE_IMAGE : VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public void setAttachments(@NonNull List<Attachment> attachments, @Nullable Long cardRemoteId) {
        this.cardRemoteId = cardRemoteId;
        this.attachments.clear();
        this.attachments.addAll(attachments);
        notifyDataSetChanged();
    }

    public void addAttachment(Attachment a) {
        this.attachments.add(a);
        notifyItemInserted(this.attachments.size());
    }

    public void removeAttachment(Attachment a) {
        final int index = this.attachments.indexOf(a);
        this.attachments.remove(a);
        notifyItemRemoved(index);
    }
}
