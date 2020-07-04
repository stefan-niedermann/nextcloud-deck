package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.flexbox.FlexboxLayout;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.WidgetColorChooserBinding;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class ColorChooser extends LinearLayout {

    private WidgetColorChooserBinding binding;

    private final FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

    private Context context;
    private String[] colors;

    private String selectedColor;
    private String previouslySelectedColor;
    @Nullable
    private ImageView previouslySelectedImageView;

    public ColorChooser(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        params.setMargins(0, dpToPx(context, R.dimen.spacer_1x), 0, 0);
        params.setFlexBasisPercent(.15f);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorChooser, 0, 0);
        colors = getResources().getStringArray(a.getResourceId(R.styleable.ColorChooser_colors, 0));
        a.recycle();

        binding = WidgetColorChooserBinding.inflate(LayoutInflater.from(context), this, true);
        for (final String color : colors) {
            ImageView image = new ImageView(getContext());
            image.setLayoutParams(params);
            image.setOnClickListener((imageView) -> {
                if (previouslySelectedImageView != null) { // null when first selection
                    previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, previouslySelectedColor));
                }
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_check_36dp, color));
                selectedColor = color;
                this.previouslySelectedColor = color;
                this.previouslySelectedImageView = image;
                binding.customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, R.color.board_default_custom_color));
                binding.customColorPicker.setVisibility(View.GONE);
                binding.brightnessSlide.setVisibility(View.GONE);
            });
            image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, color));
            binding.colorPicker.addView(image, binding.colorPicker.getChildCount() - 1);
        }

        binding.customColorPicker.attachBrightnessSlider(binding.brightnessSlide);
        binding.customColorChooser.setOnClickListener((v) -> {
            binding.customColorPicker.setVisibility(View.VISIBLE);
            binding.brightnessSlide.setVisibility(View.VISIBLE);
            if (previouslySelectedImageView != null) {
                previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.circle_grey600_36dp, selectedColor));
                previouslySelectedImageView = null;
            }
        });

        binding.customColorPicker.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            if (previouslySelectedImageView != null) {
                previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, previouslySelectedColor));
                previouslySelectedImageView = null;
            }
            String customColor = "#" + envelope.getHexCode().substring(2);
            selectedColor = customColor;
            previouslySelectedColor = customColor;
            binding.customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.circle_alpha_colorize_36dp, selectedColor));
        });
    }

    public void selectColor(String newColor) {
        boolean newColorIsCustomColor = true;
        selectedColor = newColor;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(newColor)) {
                binding.customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, R.color.board_default_custom_color));
                binding.colorPicker.getChildAt(i).performClick();
                newColorIsCustomColor = false;
                break;
            }
        }
        if (newColorIsCustomColor) {
            binding.customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, this.selectedColor));
        }
    }

    public String getSelectedColor() {
        return this.selectedColor;
    }
}
