package it.niedermann.nextcloud.deck.model.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class FullCardViewModel extends ViewModel {

    public LiveData<FullCard> fullCard;

}
