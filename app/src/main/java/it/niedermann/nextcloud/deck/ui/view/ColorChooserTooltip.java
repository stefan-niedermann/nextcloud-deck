package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.widget.TextView;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

import it.niedermann.nextcloud.deck.R;

public class ColorChooserTooltip extends FlagView {

    private final TextView textView;
    private final AlphaTileView alphaTileView;

    public ColorChooserTooltip(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        alphaTileView = findViewById(R.id.flag_color_layout);
    }


    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        textView.setText(String.format("#%s", colorEnvelope.getHexCode()));
        alphaTileView.setPaintColor(colorEnvelope.getColor());
    }

    @Override
    public void onFlipped(Boolean isFlipped) {

    }

    @Override
    public boolean isFlipAble() {
        return false;
    }
}
