package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAttachmentPickerBinding;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class CardAttachmentPicker extends BottomSheetDialogFragment implements Branded {

    private DialogAttachmentPickerBinding binding;
    private CardAttachmentPickerListener listener;

    public CardAttachmentPicker() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof CardAttachmentPickerListener) {
            this.listener = (CardAttachmentPickerListener) getParentFragment();
        } else {
            throw new IllegalArgumentException("Caller must implement " + CardAttachmentPickerListener.class.getSimpleName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (SDK_INT >= Build.VERSION_CODES.O_MR1) {
            if (!isDarkTheme(requireContext())) {
                setWhiteNavigationBar(dialog);
            }
        }

        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAttachmentPickerBinding.inflate(inflater, container, false);

        @Nullable Context context = getContext();
        if (context != null && isBrandingEnabled(context)) {
            applyBrand(readBrandMainColor(context));
        }

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.pickCamera.setOnClickListener((v) -> {
            if (SDK_INT >= LOLLIPOP) {
                listener.pickCamera();
                dismiss();
            } else {
                Toast.makeText(requireContext(), R.string.min_api_21, Toast.LENGTH_SHORT).show();
            }
        });
        binding.pickContact.setOnClickListener((v) -> {
            listener.pickContact();
            dismiss();
        });
        binding.pickFile.setOnClickListener((v) -> {
            listener.pickFile();
            dismiss();
        });
    }

    public static DialogFragment newInstance() {
        return new CardAttachmentPicker();
    }

    @Override
    public void applyBrand(int mainColor) {
        if (SDK_INT >= LOLLIPOP) {
            Stream.of(
                    binding.pickCameraIamge,
                    binding.pickContactIamge,
                    binding.pickFileIamge
            ).forEach(image -> {
                image.setBackgroundTintList(ColorStateList.valueOf(mainColor));
                image.setImageTintList(ColorStateList.valueOf(
                        DeckColorUtil.contrastRatioIsSufficient(mainColor, Color.WHITE)
                                ? Color.WHITE
                                : Color.BLACK
                ));
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

}
