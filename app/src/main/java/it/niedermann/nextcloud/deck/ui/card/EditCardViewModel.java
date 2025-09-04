package it.niedermann.nextcloud.deck.ui.card;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.BoardRepository;
import it.niedermann.nextcloud.deck.repository.CardRepository;
import it.niedermann.nextcloud.deck.repository.LabelRepository;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.card.details.CardDetailsFragment;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class EditCardViewModel extends BaseViewModel {

    private SyncRepository syncRepository;
    private final LabelRepository labelRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private Account account;
    private long boardId;
    private FullCardWithProjects originalCard;
    private FullCardWithProjects fullCard;
    private boolean isSupportedVersion = false;
    private boolean hasCommentsAbility = false;
    private boolean pendingSaveOperation = false;
    private boolean canEdit = false;
    private final MutableLiveData<String> descriptionChangedFromExternal$ = new MutableLiveData<>();
    private final MutableLiveData<Integer> boardColor$ = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> descriptionIsPreview = new MutableLiveData<>(false);
    private boolean attachmentsBackPressedCallbackStatus = false;

    public EditCardViewModel(@NonNull Application application) {
        super(application);
        this.labelRepository = new LabelRepository(application);
        this.boardRepository = new BoardRepository(application);
        this.cardRepository = new CardRepository(application);
        this.boardColor$.setValue(ContextCompat.getColor(application, R.color.primary));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * The result {@link LiveData} will emit <code>true</code> if the preview mode is enabled and <code>false</code> if the edit mode is enabled.
     */
    public LiveData<Boolean> getDescriptionMode() {
        return new ReactiveLiveData<>(new SharedPreferenceBooleanLiveData(sharedPreferences, getApplication().getString(R.string.shared_preference_description_preview), false))
                .distinctUntilChanged()
                .flatMap(isPreview -> {
                    // When we are in preview mode but the description of the card is empty, we explicitly switch to the edit mode
                    final var fullCard = getFullCard();
                    if (fullCard == null) {
                        throw new IllegalStateException("Description mode must be queried after initializing " + EditCardViewModel.class.getSimpleName() + " with a card.");
                    }
                    if (isPreview && TextUtils.isEmpty(fullCard.getCard().getDescription())) {
                        descriptionIsPreview.setValue(false);
                    } else {
                        descriptionIsPreview.setValue(isPreview);
                    }
                    return descriptionIsPreview;
                })
                .distinctUntilChanged();
    }

    /**
     * To be called when the description is mutated from <em>outside</em> of the {@link CardDetailsFragment}.
     */
    public void changeDescriptionFromExternal(@Nullable String description) {
        getFullCard().getCard().setDescription(description);
        this.descriptionChangedFromExternal$.postValue(description);
    }

    /**
     * @return a {@link LiveData} that gets triggered with the latest {@link Card#getDescription()} was changed from <em>outside</em> of the {@link CardDetailsFragment}.
     */
    public LiveData<String> descriptionChangedFromExternal() {
        return new ReactiveLiveData<>(this.descriptionChangedFromExternal$)
                .distinctUntilChanged();
    }

    /**
     * Will toggle the edit / preview mode and persist the new state
     */
    public void toggleDescriptionPreviewMode() {
        final boolean newValue = Boolean.FALSE.equals(descriptionIsPreview.getValue());
        descriptionIsPreview.setValue(newValue);
        sharedPreferences
                .edit()
                .putBoolean(getApplication().getString(R.string.shared_preference_description_preview), newValue)
                .apply();
    }

    public LiveData<Integer> getBoardColor() {
        return distinctUntilChanged(this.boardColor$);
    }

    public void setBoardColor(@ColorInt int color) {
        this.boardColor$.setValue(color);
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

    public void setAccount(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        this.account = account;
        this.syncRepository = new SyncRepository(getApplication(), account);
        hasCommentsAbility = account.getServerDeckVersionAsObject().supportsComments();
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return baseRepository.getCurrentBoardColor(accountId, boardId);
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

    public boolean isPendingSaveOperation() {
        return pendingSaveOperation;
    }

    public void setPendingSaveOperation(boolean pendingSaveOperation) {
        this.pendingSaveOperation = pendingSaveOperation;
    }

    public boolean canEdit() {
        return canEdit && isSupportedVersion;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public long getBoardId() {
        return boardId;
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return boardRepository.getFullBoardById(accountId, localId);
    }

    public CompletableFuture<Label> createLabel(long accountId, Label label, long localBoardId) {
        return labelRepository.createLabel(accountId, label, localBoardId);
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return baseRepository.getFullCardWithProjectsByLocalId(accountId, cardLocalId);
    }

    /**
     * Saves the current {@link #fullCard}. If it is a new card, it will be created, otherwise it will be updated.
     */
    public void saveCard(@NonNull IResponseCallback<FullCard> callback) {
        syncRepository.updateCard(getFullCard(), callback);
    }

    public LiveData<List<Activity>> syncActivitiesForCard(@NonNull Card card) {
        return syncRepository.syncActivitiesForCard(card);
    }

    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        syncRepository.addAttachmentToCard(accountId, localCardId, mimeType, file, callback);
    }

    public void deleteAttachmentOfCard(long accountId, long localCardId, long localAttachmentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        syncRepository.deleteAttachmentOfCard(accountId, localCardId, localAttachmentId, callback);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return cardRepository.getCardByRemoteID(accountId, remoteId);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return boardRepository.getBoardByRemoteId(accountId, remoteId);
    }

    public void setAttachmentsBackPressedCallbackStatus(boolean enabled) {
        this.attachmentsBackPressedCallbackStatus = enabled;
    }

    public boolean getAttachmentsBackPressedCallbackStatus() {
        return this.attachmentsBackPressedCallbackStatus;
    }
}
