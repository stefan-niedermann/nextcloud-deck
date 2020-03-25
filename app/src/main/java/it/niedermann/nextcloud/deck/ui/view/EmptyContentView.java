package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = WidgetEmptyContentViewBinding.inflate(inflater, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EmptyContentView, 0, 0);

        @StringRes int descriptionRes = a.getResourceId(R.styleable.EmptyContentView_description, NO_DESCRIPTION);

        binding.title.setText(getResources().getString(a.getResourceId(R.styleable.EmptyContentView_title, R.string.no_content)));
        if (descriptionRes == NO_DESCRIPTION) {
            binding.description.setVisibility(View.GONE);
        } else {
            binding.description.setText(getResources().getString(descriptionRes));
        }
        binding.image.setImageResource(a.getResourceId(R.styleable.EmptyContentView_image, R.drawable.ic_app_logo));
        a.recycle();
    }

    public void hideDescription() {
        binding.description.setVisibility(View.GONE);
    }
}
