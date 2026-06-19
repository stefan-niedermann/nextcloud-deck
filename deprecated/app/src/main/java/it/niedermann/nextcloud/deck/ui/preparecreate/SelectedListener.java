package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.widget.AdapterView;

/**
 * Default interface implementation
 */
public interface SelectedListener extends AdapterView.OnItemSelectedListener {
    @Override
    default void onNothingSelected(AdapterView<?> parent) {
        // Nothing to do here...
    }
}