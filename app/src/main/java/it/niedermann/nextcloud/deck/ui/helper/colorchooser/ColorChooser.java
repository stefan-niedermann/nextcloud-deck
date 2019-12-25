package it.niedermann.nextcloud.deck.ui.helper.colorchooser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.flexbox.FlexboxLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class ColorChooser extends LinearLayout {

    private Context context;
    private String[] colors;

    @BindView(R.id.colorPicker)
    FlexboxLayout colorPicker;

    private String selectedColor;
    private String previouslySelectedColor;
    private ImageView previouslySelectedImageView;

    private Boolean isCustomColor = false;


    public ColorChooser(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorChooser, 0, 0);
        colors = getResources().getStringArray(a.getResourceId(R.styleable.ColorChooser_colors, 0));
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_color_chooser, this, true);
        ButterKnife.bind(this);
        initDefaultColors();

    }

    private void initDefaultColors() {
        // initializes the color picker with the default values from board_default_colors (in the colors array)
        // by looping through the array and creating an ImageView icon for each color
        for (final String color : colors) {
            ImageView image = new ImageView(getContext());
            image.setOnClickListener((View imageView) -> {
                if (previouslySelectedImageView != null) {
                    previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, previouslySelectedColor));
                }
                // when clicked -> sets this as the chosen color and adds the check icon to it
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_check_36dp, color));
                selectedColor = color;
                this.previouslySelectedColor = color;
                this.previouslySelectedImageView = image;
                //Log.d("watch", "color: " + color);
            });
            // finally, sets the image to circle_grey so that it can be tinted to "color". This image
            // is then added to the colorPicker
            image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, color));
            colorPicker.addView(image);
        }
    }

    private void initCustomColorChooser() {
        // initializes a final image icon for the custom color chooser and, if already set,
        // will set the icon color to the custom color
        ImageView customColorChooser = new ImageView(getContext());
        Log.d("watch", "custom color set: " + isCustomColor);
        if (isCustomColor == false) {
            customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, R.color.board_default_custom_color));
        } else {
            customColorChooser.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_colorize_36dp, this.selectedColor));
        }
        colorPicker.addView(customColorChooser);
        customColorChooser.setOnClickListener((View imageView) -> {
            Log.d("watch", "launch custom color chooser icon");
        });
    }

    public void selectColor(String newColor) {
        selectedColor = newColor;
        Log.d("watch", "color: " + this.selectedColor);
        // checks if the new color is one of the default colors in the colors array. If a match is
        // found then that color is selected from the color icons
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(newColor)) {
                initCustomColorChooser(); // adds custom color picker, with default color
                colorPicker.getChildAt(i).performClick();
                return;
            }
        }
        // if the board color is not found to be a default color, then a custom color is assumed and
        // the custom color chooser is given setup for this
        isCustomColor = true;
        initCustomColorChooser(); // adds custom color picker, with custom color
    }

    public String getSelectedColor() {
        return this.selectedColor;
    }
}
