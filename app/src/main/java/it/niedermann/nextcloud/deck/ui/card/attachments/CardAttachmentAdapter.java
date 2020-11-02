package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.ui.attachments.AttachmentsActivity;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.openAttachmentInBrowser;

@SuppressWarnings("WeakerAccess")
public class CardAttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> implements Branded {

    public static final int VIEW_TYPE_DEFAULT = 2;
    public static final int VIEW_TYPE_IMAGE = 1;

    @NonNull
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);
    @NonNull
    private final MenuInflater menuInflater;
    @ColorInt
    private int mainColor;
    private final Account account;
    @Nullable
    private Long cardRemoteId = null;
    private final long cardLocalId;
    @NonNull
    private final FragmentManager fragmentManager;
    @NonNull
    private final List<Attachment> attachments = new ArrayList<>();
    @NonNull
    private final AttachmentClickedListener attachmentClickedListener;

    CardAttachmentAdapter(
            @NonNull FragmentManager fragmentManager,
            @NonNull MenuInflater menuInflater,
            @NonNull AttachmentClickedListener attachmentClickedListener,
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
        final Attachment attachment = attachments.get(position);
        final Context context = holder.itemView.getContext();
        final View.OnClickListener onClickListener;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_IMAGE: {
                onClickListener = (event) -> {
                    attachmentClickedListener.onAttachmentClicked(position);
                    final Intent intent = AttachmentsActivity.createIntent(context, account, cardLocalId, attachment.getLocalId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && context instanceof Activity) {
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
                onClickListener = (event) -> openAttachmentInBrowser(context, account.getUrl(), cardRemoteId, attachment.getId());
                break;
            }
        }
        holder.bind(account, menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, mainColor);
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
    public void applyBrand(@ColorInt int mainColor) {
        this.mainColor = mainColor;
        notifyDataSetChanged();
    }
}
