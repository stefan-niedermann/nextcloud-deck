package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.WidgetColorChooserBinding;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class ColorChooser extends LinearLayout {

    private WidgetColorChooserBinding binding;

    private Context context;
    private String[] colors;

    private String selectedColor;
    private String previouslySelectedColor;
    private ImageView previouslySelectedImageView;

    private Boolean hasCustomColor = false;
    private ColorPickerView customColorPicker;
    private BrightnessSlideBar brightnessSlideBar;
    private ImageView customColorChooser;

    public ColorChooser(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorChooser, 0, 0);
        colors = getResources().getStringArray(a.getResourceId(R.styleable.ColorChooser_colors, 0));
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        binding = WidgetColorChooserBinding.inflate(inflater, this, true);
        initDefaultColors();

        customColorPicker = (ColorPickerView) findViewById(R.id.customColorPicker);
        brightnessSlideBar = findViewById(R.id.brightnessSlide);

        customColorPicker.attachBrightnessSlider(brightnessSlideBar);

        customColorPicker.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                String customColor = "#" + envelope.getHexCode().substring(2);
                selectedColor = customColor;
                previouslySelectedColor = customColor;
                //previouslySelectedColor = customColor;
                customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.circle_alpha_colorize_36dp, selectedColor));
            }
        });
    }

    private void initDefaultColors() {
        for (final String color : colors) {
            ImageView image = new ImageView(getContext());
            image.setOnClickListener((imageView) -> {
                if (previouslySelectedImageView != null) { // null when first selection
                    previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, previouslySelectedColor));
                }
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_check_36dp, color));
                selectedColor = color;
                this.previouslySelectedColor = color;
                this.previouslySelectedImageView = image;
            });
            image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, color));
            binding.colorPicker.addView(image);
        }
    }

    private void initCustomColorChooser() {
        // initializes a final image icon for the custom color chooser and, if already set,
        // will set the icon color to the custom color
        customColorChooser = new ImageView(getContext());
        if (!hasCustomColor) {
            customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, R.color.board_default_custom_color));
        } else {
            customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, this.selectedColor));
        }
        binding.colorPicker.addView(customColorChooser);
        customColorChooser.setOnClickListener((View imageView) -> {
            // when clicked sets the custom color wheel to be visible
            customColorPicker.setVisibility(View.VISIBLE);
            brightnessSlideBar.setVisibility(View.VISIBLE);
        });
    }

    public void selectColor(String newColor) {
        selectedColor = newColor;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(newColor)) {
                initCustomColorChooser(); // adds custom color picker, with default color
                binding.colorPicker.getChildAt(i).performClick();
                return;
            }
        }
        // if the board color is not found to be a default color, then a custom color is assumed and
        // the custom color chooser is setup for this
        hasCustomColor = true;
        initCustomColorChooser(); // adds custom color picker, with custom color
    }

    public String getSelectedColor() {
        return this.selectedColor;
    }
}
