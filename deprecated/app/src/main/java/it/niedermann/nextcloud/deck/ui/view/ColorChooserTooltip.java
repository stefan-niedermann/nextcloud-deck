package it.niedermann.nextcloud.deck.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

import java.util.Locale;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.R;

public class ColorChooserTooltip extends FlagView {

    private final TextView textView;
    private final AlphaTileView alphaTileView;

    public ColorChooserTooltip(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        alphaTileView = findViewById(R.id.flag_color_layout);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        textView.setText('#' + ColorUtil.intColorToHexString(colorEnvelope.getColor()).toUpperCase(Locale.ROOT));
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
