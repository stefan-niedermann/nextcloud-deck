package it.niedermann.nextcloud.deck.ui.attachments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class AttachmentsViewModel extends BaseViewModel {

    public AttachmentsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return baseRepository.getFullCardWithProjectsByLocalId(accountId, cardLocalId);
    }
}
