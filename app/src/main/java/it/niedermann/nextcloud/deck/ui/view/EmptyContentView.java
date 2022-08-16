package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.WidgetEmptyContentViewBinding;

public class EmptyContentView extends RelativeLayout {

    private static final int NO_DESCRIPTION = -1;

    private WidgetEmptyContentViewBinding binding;

    public EmptyContentView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        binding = WidgetEmptyContentViewBinding.inflate(LayoutInflater.from(context), this, true);

        final var styles = context.obtainStyledAttributes(attrs,
                R.styleable.EmptyContentView, 0, 0);

        @StringRes int descriptionRes = styles.getResourceId(R.styleable.EmptyContentView_description, NO_DESCRIPTION);

        binding.title.setText(getResources().getString(styles.getResourceId(R.styleable.EmptyContentView_title, R.string.no_content)));
        if (descriptionRes == NO_DESCRIPTION) {
            binding.description.setVisibility(View.GONE);
        } else {
            binding.description.setText(getResources().getString(descriptionRes));
        }
        binding.image.setImageResource(styles.getResourceId(R.styleable.EmptyContentView_image, R.drawable.ic_app_logo));
        styles.recycle();
    }

    public void hideDescription() {
        binding.description.setVisibility(View.GONE);
    }
}
