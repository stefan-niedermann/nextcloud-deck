package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAttachmentPickerBinding;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.View.GONE;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class CardAttachmentPicker extends BottomSheetDialogFragment implements Branded {

    private DialogAttachmentPickerBinding binding;
    private CardAttachmentPickerListener listener;

    private ImageView[] brandedViews;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAttachmentPickerBinding.inflate(inflater, container, false);
        brandedViews = new ImageView[]{binding.pickCameraIamge, binding.pickContactIamge, binding.pickFileIamge};

        if (SDK_INT < LOLLIPOP) {
            binding.pickCamera.setVisibility(GONE);
        }

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
            } else {
                Toast.makeText(requireContext(), R.string.min_api_21, Toast.LENGTH_SHORT).show();
            }
        });
        binding.pickContact.setOnClickListener((v) -> listener.pickContact());
        binding.pickFile.setOnClickListener((v) -> listener.pickFile());
    }

    public static DialogFragment newInstance() {
        return new CardAttachmentPicker();
    }

    @Override
    public void applyBrand(int mainColor) {
        if (SDK_INT >= LOLLIPOP) {
            final ColorStateList backgroundColorStateList = ColorStateList.valueOf(mainColor);
            final ColorStateList foregroundColorStateList = ColorStateList.valueOf(
                    DeckColorUtil.contrastRatioIsSufficient(mainColor, Color.WHITE)
                            ? Color.WHITE
                            : Color.BLACK
            );
            for (ImageView v : brandedViews) {
                v.setBackgroundTintList(backgroundColorStateList);
                v.setImageTintList(foregroundColorStateList);
            }
        }
    }
}
