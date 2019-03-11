package it.niedermann.nextcloud.deck.model.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public class FullCardViewModel extends ViewModel {

    public LiveData<FullCard> fullCard;

}
