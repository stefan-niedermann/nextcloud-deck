package it.niedermann.nextcloud.deck.ui.attachments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class AttachmentsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public AttachmentsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return syncManager.getFullCardWithProjectsByLocalId(accountId, cardLocalId);
    }
}
