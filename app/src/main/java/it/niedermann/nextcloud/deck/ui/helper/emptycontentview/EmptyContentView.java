package it.niedermann.nextcloud.deck.ui.helper.emptycontentview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;

public class EmptyContentView extends RelativeLayout {

    private static final int NO_DESCRIPTION = -1;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.image)
    AppCompatImageView image;

    public EmptyContentView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_empty_content_view, this, true);
        ButterKnife.bind(this);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EmptyContentView, 0, 0);

        @StringRes int descriptionRes = a.getResourceId(R.styleable.EmptyContentView_description, NO_DESCRIPTION);

        title.setText(getResources().getString(a.getResourceId(R.styleable.EmptyContentView_title, R.string.no_content)));
        if (descriptionRes == NO_DESCRIPTION) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(getResources().getString(descriptionRes));
        }
        image.setImageResource(a.getResourceId(R.styleable.EmptyContentView_image, R.drawable.ic_app_logo));
        a.recycle();
    }

    public void hideDescription() {
        description.setVisibility(View.GONE);
    }
}
