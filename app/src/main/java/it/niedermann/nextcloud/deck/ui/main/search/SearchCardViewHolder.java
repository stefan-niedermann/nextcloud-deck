package it.niedermann.nextcloud.deck.ui.main.search;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemSearchCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

public class SearchCardViewHolder extends SearchViewHolder {

    private final ItemSearchCardBinding binding;

    public SearchCardViewHolder(@NonNull ItemSearchCardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account, long localBoardId, @NonNull FullCard fullCard) {
        final var context = binding.getRoot().getContext();
        binding.getRoot().setOnClickListener(v -> context.startActivity(EditActivity.createEditCardIntent(context, account, localBoardId, fullCard.getLocalId())));

        binding.title.setText(fullCard.getCard().getTitle());
        if (TextUtils.isEmpty(fullCard.getCard().getDescription())) {
            binding.description.setVisibility(View.GONE);
        } else {
            binding.description.setVisibility(View.VISIBLE);
            binding.description.setText(fullCard.getCard().getDescription());
        }


        final var coverImages = fullCard.getAttachments()
                .stream()
                .filter(attachment -> MimeTypeUtil.isImage(attachment.getMimetype()))
                .findFirst();

        if (coverImages.isPresent()) {
            binding.coverImages.setVisibility(View.VISIBLE);
            binding.coverImages.post(() -> Glide.with(binding.coverImages)
                    .load(new SingleSignOnUrl(account.getName(), AttachmentUtil.getThumbnailUrl(account, fullCard.getId(), coverImages.get(), binding.coverImages.getWidth())))
                    .apply(new RequestOptions().transform(
                            new CenterCrop(),
                            new RoundedCorners(DimensionUtil.INSTANCE.dpToPx(context, R.dimen.spacer_1x))
                    ))
                    .placeholder(R.drawable.ic_image_grey600_24dp)
                    .error(R.drawable.ic_image_grey600_24dp)
                    .into(binding.coverImages));
        } else {
            binding.coverImages.setVisibility(View.GONE);
        }
    }

    public void applyTheme(int color, String term) {
        final var utils = ThemeUtils.of(color, binding.getRoot().getContext());
        utils.platform.colorTextView(binding.title, ColorRole.ON_SURFACE);
        utils.platform.highlightText(binding.title, binding.title.getText().toString(), term);
        utils.platform.highlightText(binding.description, binding.description.getText().toString(), term);
    }
}
