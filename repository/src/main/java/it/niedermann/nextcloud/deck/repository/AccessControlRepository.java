package it.niedermann.nextcloud.deck.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AccessControlDataProvider;
import okhttp3.Headers;

public class AccessControlRepository extends AbstractRepository {

    public AccessControlRepository(@NonNull Context context) {
        super(context);
    }

    public void createAccessControl(long accountId, @NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, entity.getBoardId());
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).createEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, ResponseCallback.from(account, callback), ((entity1, response) -> {
                        response.setBoardId(entity.getBoardId());
                        response.setUserId(entity.getUser().getLocalId());
                    })
            );
        });
    }

    public void updateAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).updateEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, ResponseCallback.from(account, callback));
        });
    }

    public void deleteAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<EmptyResponse> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).deleteEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, new ResponseCallback<>(account) {
                        @Override
                        public void onResponse(EmptyResponse response, Headers headers) {
                            // revoked own board-access?
                            if (entity.getAccountId() == entity.getAccountId() && entity.getUser().getUid().equals(account.getUserName())) {
                                dataBaseAdapter.saveNeighbourOfBoard(board.getAccountId(), board.getLocalId());
                                dataBaseAdapter.removeCurrentStackId(board.getAccountId(), board.getLocalId());
                                dataBaseAdapter.deleteBoardPhysically(board.getBoard());
                            }
                            callback.onResponse(response, headers);
                        }

                        @SuppressLint("MissingSuperCall")
                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });
        });
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long localBoardId) {
        return dataBaseAdapter.getAccessControlByLocalBoardId(accountId, localBoardId);
    }
}
