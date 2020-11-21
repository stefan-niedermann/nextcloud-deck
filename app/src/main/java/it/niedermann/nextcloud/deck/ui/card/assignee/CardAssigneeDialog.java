package it.niedermann.nextcloud.deck.ui.card.assignee;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogPreviewBinding;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDeleteAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;

@Deprecated
public class CardAssigneeDialog extends BrandedDialogFragment {

    private static final String KEY_USER = "user";
    private DialogPreviewBinding binding;
    private EditCardViewModel viewModel;

    @Nullable
    private CardAssigneeListener cardAssigneeListener = null;
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private User user;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof CardAssigneeListener) {
            this.cardAssigneeListener = (CardAssigneeListener) getParentFragment();
        } else if (context instanceof CardAssigneeListener) {
            this.cardAssigneeListener = (CardAssigneeListener) context;
        }

        final Bundle args = requireArguments();
        if (!args.containsKey(KEY_USER)) {
            throw new IllegalArgumentException("Provide at least " + KEY_USER);
        }
        final Serializable user = args.getSerializable(KEY_USER);
        if (user == null) {
            throw new IllegalArgumentException(KEY_USER + " must not be null.");
        }
        this.user = (User) user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogPreviewBinding.inflate(LayoutInflater.from(requireContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        AlertDialog.Builder dialogBuilder = new BrandedDeleteAlertDialogBuilder(requireContext());

        if (viewModel.canEdit() && cardAssigneeListener != null) {
            dialogBuilder.setPositiveButton(R.string.simple_unassign, (d, w) -> cardAssigneeListener.onUnassignUser(user));
        }

        return dialogBuilder
                .setView(binding.getRoot())
                .setNeutralButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = requireContext();

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.setColorSchemeColors(isDarkTheme(context) ? Color.LTGRAY : Color.DKGRAY);
        circularProgressDrawable.start();

        binding.avatar.post(() -> Glide.with(binding.avatar.getContext())
                .load(viewModel.getAccount().getUrl() + "/index.php/avatar/" + Uri.encode(user.getUid()) + "/" + binding.avatar.getWidth())
                .placeholder(circularProgressDrawable)
                .error(R.drawable.ic_person_grey600_24dp)
                .into(binding.avatar));
        binding.title.setText(user.getDisplayname());
    }

    @Override
    public void applyBrand(int mainColor) {
    }

    public static DialogFragment newInstance(@NonNull User user) {
        final DialogFragment fragment = new CardAssigneeDialog();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }
}
