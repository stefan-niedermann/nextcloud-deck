package it.niedermann.nextcloud.deck.ui.helper.colorchooser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
            colorPicker.addView(image);
        }
    }

    public void selectColor(String newColor) {
        selectedColor = newColor;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(newColor)) {
                colorPicker.getChildAt(i).performClick();
                return;
            }
        }
    }

    public String getSelectedColor() {
        return this.selectedColor;
    }
}
