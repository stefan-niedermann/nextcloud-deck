package it.niedermann.nextcloud.deck.ui.card;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.util.Objects;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAttachmentBinding;

public class AttachmentDialogFragment extends DialogFragment {

    private static final String BUNDLE_KEY_URL = "url";
    private static final String BUNDLE_KEY_ALT = "alt";

    /**
     * Use newInstance()-Method
     */
    public AttachmentDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        it.niedermann.nextcloud.deck.databinding.DialogAttachmentBinding binding = DialogAttachmentBinding.inflate(getLayoutInflater());

        Glide.with(this)
//                .addDefaultRequestListener(new RequestListener<Object>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Object> target, boolean isFirstResource) {
//                        image.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Object resource, Object model, Target<Object> target, DataSource dataSource, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        image.setVisibility(View.VISIBLE);
//                        return false;
//                    }
//                })
                .load(requireArguments().getString(BUNDLE_KEY_URL))
                .into(binding.image);
        binding.image.setContentDescription(requireArguments().getString(BUNDLE_KEY_ALT));
        binding.image.getRootView().setOnClickListener((v) -> dismiss());

        return new AlertDialog.Builder(requireActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(binding.getRoot())
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(requireDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static AttachmentDialogFragment newInstance(@NonNull String url, @NonNull String alt) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_URL, url);
        bundle.putString(BUNDLE_KEY_ALT, alt);

        AttachmentDialogFragment fragment = new AttachmentDialogFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
