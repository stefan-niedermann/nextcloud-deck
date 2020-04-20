package it.niedermann.nextcloud.deck.ui.attachments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {

    private static final String MIMETYPE_IMAGE_PREFIX = "image";
    private final Account account;
    private final long cardRemoteId;
    @NonNull
    private List<Attachment> attachments;
    private Context context;

    @SuppressWarnings("WeakerAccess")
    public AttachmentAdapter(@NonNull Account account, long cardRemoteId, @NonNull List<Attachment> attachments) {
        super();
        this.attachments = attachments;
        this.account = account;
        this.cardRemoteId = cardRemoteId;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new AttachmentViewHolder(ItemAttachmentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        final Attachment attachment = attachments.get(position);
        final String uri = AttachmentUtil.getUrl(account.getUrl(), cardRemoteId, attachment.getId());
        if (attachment.getMimetype() != null && attachment.getMimetype().startsWith(MIMETYPE_IMAGE_PREFIX)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.binding.preview.setTransitionName(context.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId())));
            }
            holder.binding.preview.setImageResource(R.drawable.ic_image_grey600_24dp);
            Glide.with(context)
                    .load(uri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            if (context instanceof FragmentActivity) {
                                ((FragmentActivity) context).supportStartPostponedEnterTransition();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (context instanceof FragmentActivity) {
                                ((FragmentActivity) context).supportStartPostponedEnterTransition();
                            }
                            return false;
                        }
                    })
                    .error(R.drawable.ic_image_grey600_24dp)
                    .into(holder.binding.preview);
        }
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }
}
