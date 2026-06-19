package it.niedermann.nextcloud.deck.ui.main.search;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import org.jetbrains.annotations.Contract;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemSearchCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardOptionsItemSelectedListener;
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

    public void bind(@NonNull Account account, long localBoardId, @NonNull FullCard fullCard, @Nullable Long boardRemoteId, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener) {
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
            binding.coverImages.post(() -> {
                final var requestManager = Glide.with(binding.coverImages);
                AttachmentUtil.getThumbnailUrl(account, fullCard.getId(), coverImages.get(), binding.coverImages.getWidth())
                        .map(Uri::toString)
                        .map(uri -> requestManager.load(new SingleSignOnUrl(account.getName(), uri)))
                        .orElseGet(() -> requestManager.load(R.drawable.ic_image_24dp))
                        .apply(new RequestOptions().transform(
                                new CenterCrop(),
                                new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.spacer_1x))
                        ))
                        .placeholder(R.drawable.ic_image_24dp)
                        .error(R.drawable.ic_image_24dp)
                        .into(binding.coverImages);

            });
        } else {
            binding.coverImages.setVisibility(View.GONE);
        }

        binding.cardMenu.setOnClickListener(view -> {
            final var popup = new PopupMenu(context, view);
            popup.inflate(optionsMenu);
            final var menu = popup.getMenu();
            if (containsUser(fullCard.getAssignedUsers(), account.getUserName())) {
                menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
            } else {
                menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
            }
            if (boardRemoteId == null || fullCard.getCard().getId() == null) {
                menu.removeItem(R.id.share_link);
            }

            popup.setOnMenuItemClickListener(item -> optionsItemsSelectedListener.onCardOptionsItemSelected(item, fullCard));
            popup.show();
        });
    }

    @Contract("null, _ -> false")
    private static boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (final var user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void applyTheme(int color, String term) {
        final var utils = ThemeUtils.of(color, binding.getRoot().getContext());
        utils.platform.colorTextView(binding.title, ColorRole.ON_SURFACE);
        utils.platform.highlightText(binding.title, binding.title.getText().toString(), term);
        utils.platform.highlightText(binding.description, binding.description.getText().toString(), term);
        utils.platform.colorImageView(binding.cardMenu, ColorRole.ON_SURFACE_VARIANT);
    }
}
