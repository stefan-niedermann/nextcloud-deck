package it.niedermann.nextcloud.deck.ui.helper.emptycontentview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;

public class EmptyContentView extends RelativeLayout {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.image)
    ImageView image;

    public EmptyContentView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_empty_content_view, this, true);
        ButterKnife.bind(this);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EmptyContentView, 0, 0);
        title.setText(getResources().getString(a.getResourceId(R.styleable.EmptyContentView_title, R.string.app_name_short)));
        description.setText(getResources().getString(a.getResourceId(R.styleable.EmptyContentView_description, R.string.app_name)));
        image.setImageDrawable(getResources().getDrawable(a.getResourceId(R.styleable.EmptyContentView_image, R.drawable.ic_launcher_foreground)));
        a.recycle();
    }
}
