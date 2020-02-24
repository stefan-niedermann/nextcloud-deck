package it.niedermann.nextcloud.deck.ui.card;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;

public class AttachmentDialogFragment extends DialogFragment {

    private static final String BUNDLE_KEY_URL = "url";
    private static final String BUNDLE_KEY_ALT = "alt";

    @BindView(R.id.image)
    ImageView image;

//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    /**
     * Use newInstance()-Method
     */
    public AttachmentDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_attachment, null);
        ButterKnife.bind(this, view);

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
                .into(image);
        image.setContentDescription(requireArguments().getString(BUNDLE_KEY_ALT));
        image.getRootView().setOnClickListener((v) -> dismiss());

        return new AlertDialog.Builder(requireActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(view)
                .create();
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
