package it.niedermann.nextcloud.deck.ui.card.attachments;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.generateOpenAttachmentIntent;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.ui.attachments.AttachmentsActivity;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

@SuppressWarnings("WeakerAccess")
public class CardAttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> implements Themed {

    public static final int VIEW_TYPE_DEFAULT = 2;
    public static final int VIEW_TYPE_IMAGE = 1;

    @NonNull
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);
    @NonNull
    private final MenuInflater menuInflater;
    @ColorInt
    private int color;
    private final Account account;
    @Nullable
    private Long cardRemoteId = null;
    private final long cardLocalId;
    @NonNull
    private final FragmentManager fragmentManager;
    @NonNull
    private final List<Attachment> attachments = new ArrayList<>();
    @NonNull
    private final AttachmentInteractionListener attachmentInteractionListener;

    CardAttachmentAdapter(
            @NonNull FragmentManager fragmentManager,
            @NonNull MenuInflater menuInflater,
            @NonNull AttachmentInteractionListener attachmentInteractionListener,
            @NonNull Account account,
            @Nullable Long cardLocalId
    ) {
        super();
        this.fragmentManager = fragmentManager;
        this.menuInflater = menuInflater;
        this.attachmentInteractionListener = attachmentInteractionListener;
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
        final var context = parent.getContext();
        return switch (viewType) {
            case VIEW_TYPE_IMAGE ->
                    new ImageAttachmentViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(context), parent, false));
            default ->
                    new DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding.inflate(LayoutInflater.from(context), parent, false));
        };
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        final var attachment = attachments.get(position);
        final var context = holder.itemView.getContext();
        final View.OnClickListener onClickListener;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_IMAGE: {
                onClickListener = (event) -> {
                    attachmentInteractionListener.onAttachmentClicked(position);
                    final var intent = AttachmentsActivity.createIntent(context, account, cardLocalId, attachment.getLocalId());
                    if (context instanceof Activity) {
                        String transitionName = context.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId()));
                        holder.getPreview().setTransitionName(transitionName);
                        context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.getPreview(), transitionName).toBundle());
                    } else {
                        context.startActivity(intent);
                    }
                };
                break;
            }
            case VIEW_TYPE_DEFAULT:
            default: {
                onClickListener = (event) -> {
                    final var intent = generateOpenAttachmentIntent(account, context, cardRemoteId, attachment);
                    if (intent.isPresent()) {
                        context.startActivity(intent.get());
                    } else {
                        Toast.makeText(context, R.string.attachment_does_not_yet_exist, Toast.LENGTH_LONG).show();
                        DeckLog.logError(new IllegalArgumentException("attachmentRemoteId must not be null."));
                    }
                };
                break;
            }
        }
        // FIXME only onAppendToDescription if write permission!!
        // FIXME Trigger new description displayed!
        holder.bind(account, menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, attachmentInteractionListener::onAppendToDescription, color);
    }

    @Override
    public int getItemViewType(int position) {
        return MimeTypeUtil.isImage(attachments.get(position).getMimetype()) ? VIEW_TYPE_IMAGE : VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    private void updateIsEmpty() {
        this.isEmpty.postValue(getItemCount() <= 0);
    }

    @NonNull
    public LiveData<Boolean> isEmpty() {
        return distinctUntilChanged(this.isEmpty);
    }

    public void setAttachments(@NonNull List<Attachment> attachments, @Nullable Long cardRemoteId) {
        this.cardRemoteId = cardRemoteId;
        this.attachments.clear();
        this.attachments.addAll(attachments);
        notifyDataSetChanged();
        this.updateIsEmpty();
    }

    public void addAttachment(Attachment a) {
        this.attachments.add(0, a);
        notifyItemInserted(this.attachments.size());
        this.updateIsEmpty();
    }

    public void removeAttachment(Attachment a) {
        final int index = this.attachments.indexOf(a);
        this.attachments.remove(a);
        notifyItemRemoved(index);
        this.updateIsEmpty();
    }

    public void replaceAttachment(Attachment toReplace, Attachment with) {
        final int index = this.attachments.indexOf(toReplace);
        this.attachments.remove(toReplace);
        this.attachments.add(index, with);
        notifyItemChanged(index);
    }

    @Override
    public void applyTheme(@ColorInt int color) {
        this.color = color;
        notifyDataSetChanged();
    }
}
