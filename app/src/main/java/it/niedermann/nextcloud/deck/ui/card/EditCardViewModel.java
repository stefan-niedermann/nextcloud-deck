package it.niedermann.nextcloud.deck.ui.card;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.switchMap;

@SuppressWarnings("WeakerAccess")
public class EditCardViewModel extends AndroidViewModel {

    private SyncManager syncManager;
    private Account account;
    private long boardId;
    private FullCardWithProjects originalCard;
    private FullCardWithProjects fullCard;
    private boolean isSupportedVersion = false;
    private boolean hasCommentsAbility = false;
    private boolean pendingCreation = false;
    private boolean canEdit = false;
    private boolean createMode = false;
    private final MutableLiveData<Integer> brandingColor$ = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> descriptionIsPreview = new MutableLiveData<>(false);

    public EditCardViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
        this.brandingColor$.setValue(ContextCompat.getColor(application, R.color.primary));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * The result {@link LiveData} will emit <code>true</code> if the preview mode is enabled and <code>false</code> if the edit mode is enabled.
     * In {@link #createMode} it will not emit the last persisted state but only a temporary value.
     */
    public LiveData<Boolean> getDescriptionMode() {
        if (isCreateMode()) {
            return distinctUntilChanged(descriptionIsPreview);
        } else {
            return distinctUntilChanged(switchMap(distinctUntilChanged(new SharedPreferenceBooleanLiveData(sharedPreferences, getApplication().getString(R.string.shared_preference_description_preview), false)), (isPreview) -> {
                // When we are in preview mode but the description of the card is empty, we explicitly switch to the edit mode
                final FullCardWithProjects fullCard = getFullCard();
                if (fullCard == null) {
                    throw new IllegalStateException("Description mode must be queried after initializing " + EditCardViewModel.class.getSimpleName() + " with a card.");
                }
                if (isPreview && TextUtils.isEmpty(fullCard.getCard().getDescription())) {
                    descriptionIsPreview.setValue(false);
                } else {
                    descriptionIsPreview.setValue(isPreview);
                }
                return descriptionIsPreview;
            }));
        }
    }

    /**
     * Will toggle the edit / preview mode and persist the new state if not in {@link #createMode}.
     */
    public void toggleDescriptionPreviewMode() {
        final boolean newValue = Boolean.FALSE.equals(descriptionIsPreview.getValue());
        descriptionIsPreview.setValue(newValue);
        if (!isCreateMode()) {
            sharedPreferences
                    .edit()
                    .putBoolean(getApplication().getString(R.string.shared_preference_description_preview), newValue)
                    .apply();
        }
    }

    public LiveData<Integer> getBrandingColor() {
        return distinctUntilChanged(this.brandingColor$);
    }

    public void setBrandingColor(@ColorInt int brandingColor) {
        this.brandingColor$.setValue(brandingColor);
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param boardId  Local ID, expecting a positive long value
     * @param fullCard The card that is currently edited
     */
    public void initializeExistingCard(long boardId, @NonNull FullCardWithProjects fullCard, boolean isSupportedVersion) {
        this.boardId = boardId;
        this.fullCard = fullCard;
        this.originalCard = new FullCardWithProjects(this.fullCard);
        this.isSupportedVersion = isSupportedVersion;
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param boardId Local ID, expecting a positive long value
     * @param stackId Local ID, expecting a positive long value where the card should be created
     */
    public void initializeNewCard(long boardId, long stackId, boolean isSupportedVersion) {
        final FullCardWithProjects fullCard = new FullCardWithProjects();
        fullCard.setLabels(new ArrayList<>());
        fullCard.setAssignedUsers(new ArrayList<>());
        fullCard.setAttachments(new ArrayList<>());
        final Card card = new Card();
        card.setStackId(stackId);
        fullCard.setCard(card);
        initializeExistingCard(boardId, fullCard, isSupportedVersion);
    }

    public void setAccount(@NonNull Account account) {
        this.account = account;
        this.syncManager = new SyncManager(getApplication(), account.getName());
        hasCommentsAbility = account.getServerDeckVersionAsObject().supportsComments();
    }

    public boolean hasChanges() {
        if (fullCard == null) {
            DeckLog.info("Can not check for changes because fullCard is null → assuming no changes have been made yet.");
            return false;
        }
        return fullCard.equals(originalCard);
    }

    public boolean hasCommentsAbility() {
        return hasCommentsAbility;
    }

    public Account getAccount() {
        return account;
    }

    public FullCardWithProjects getFullCard() {
        return fullCard;
    }

    public boolean isPendingCreation() {
        return pendingCreation;
    }

    public void setPendingCreation(boolean pendingCreation) {
        this.pendingCreation = pendingCreation;
    }

    public boolean canEdit() {
        return canEdit && isSupportedVersion;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
        if (createMode) {
            this.descriptionIsPreview.setValue(false);
        }
    }

    public long getBoardId() {
        return boardId;
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return syncManager.getFullBoardById(accountId, localId);
    }

    public void createLabel(long accountId, Label label, long localBoardId, @NonNull IResponseCallback<Label> callback) {
        syncManager.createLabel(accountId, label, localBoardId, callback);
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return syncManager.getFullCardWithProjectsByLocalId(accountId, cardLocalId);
    }

    /**
     * Saves the current {@link #fullCard}. If it is a new card, it will be created, otherwise it will be updated.
     */
    public void saveCard(@NonNull IResponseCallback<FullCard> callback) {
        if (isCreateMode()) {
            syncManager.createFullCard(getAccount().getId(), getBoardId(), getFullCard().getCard().getStackId(), getFullCard(), callback);
        } else {
            syncManager.updateCard(getFullCard(), callback);
        }
    }

    public LiveData<List<Activity>> syncActivitiesForCard(@NonNull Card card) {
        return syncManager.syncActivitiesForCard(card);
    }

    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        syncManager.addAttachmentToCard(accountId, localCardId, mimeType, file, callback);
    }

    public void deleteAttachmentOfCard(long accountId, long localCardId, long localAttachmentId, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteAttachmentOfCard(accountId, localCardId, localAttachmentId, callback);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return syncManager.getCardByRemoteID(accountId, remoteId);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return syncManager.getBoardByRemoteId(accountId, remoteId);
    }
}
