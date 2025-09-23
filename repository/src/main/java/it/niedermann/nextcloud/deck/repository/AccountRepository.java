package it.niedermann.nextcloud.deck.repository;

import static io.reactivex.rxjava3.core.Flowable.just;

import android.content.Context;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Flowable;

public class AccountRepository extends AbstractRepository {

    public AccountRepository(@NonNull Context context) {
        super(context);
    }

    public Flowable<ImportState> importAccount(@NonNull String accountName,
                                               @NonNull String url,
                                               @NonNull String userName,
                                               @NonNull String token) {

        // TODO Implement account import
        //  1. Check whether accountName already exists.
        //  2. Use repository-sync to fetch everything related to this account
        //  3. In case of an error rollback by deleting the local account and cascading this deletion.

        return just(new ImportState(accountName, url, userName, 0, 0, 0));

//        final var accountToCreate = new Account(account.name, account.userId, account.url);
//        vm.createAccount(accountToCreate, new IResponseCallback<>() {
//            @Override
//            public void onResponse(Account createdAccount, Headers headers) {
//                try {
//                    final var syncManager = new SyncRepository(ImportAccountFragment.this, createdAccount);
//
//                    syncManager.refreshCapabilities(new ResponseCallback<>(createdAccount) {
//                        @Override
//                        public void onResponse(Capabilities response, Headers headers) {
//                            if (!response.isMaintenanceEnabled()) {
//                                if (response.getDeckVersion().isSupported()) {
//                                    final var callback = new IResponseCallback<>() {
//                                        @Override
//                                        public void onResponse(Object response, Headers headers) {
//                                            var progress$ = syncManager.synchronize(new ResponseCallback<>(account) {
//                                                @Override
//                                                public void onResponse(Boolean response, Headers headers) {
//                                                    restoreWifiPref();
//                                                    SyncWorker.update(getApplicationContext());
//                                                    vm.saveCurrentAccount(account);
//                                                    setResult(RESULT_OK);
//                                                    finish();
//                                                }
//
//                                                @Override
//                                                public void onError(Throwable throwable) {
//                                                    super.onError(throwable);
//                                                    setStatusText(throwable.getMessage());
//                                                    runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
//                                                    rollbackAccountCreation(createdAccount.getId());
//                                                }
//                                            });
//
//                                            runOnUiThread(() -> progress$.observe(ImportAccountFragment.this, (progress) -> {
//                                                DeckLog.log("New progress value", progress.first, progress.second);
//                                                if (progress.first > 0) {
//                                                    dataBinding.progressCircular.setIndeterminate(false);
//                                                }
//                                                if (progress.first < progress.second) {
//                                                    dataBinding.progressText.setText(getString(R.string.progress_import, progress.first + 1, progress.second));
//                                                }
//                                                dataBinding.progressCircular.setProgress(progress.first);
//                                                dataBinding.progressCircular.setMax(progress.second);
//                                            }));
//                                        }
//                                    };
//
//                                    if (response.getDeckVersion().firstCallHasDifferentResponseStructure()) {
//                                        syncManager.fetchBoardsFromServer(new ResponseCallback<>(account) {
//                                            @Override
//                                            public void onResponse(List<FullBoard> response, Headers headers) {
//                                                callback.onResponse(createdAccount, headers);
//                                            }
//
//                                            @SuppressLint("MissingSuperCall")
//                                            @Override
//                                            public void onError(Throwable throwable) {
//                                                // We proceed with the import anyway. It's just important that one request has been done.
//                                                callback.onResponse(createdAccount, headers);
//                                            }
//                                        });
//                                    } else {
//                                        callback.onResponse(createdAccount, headers);
//                                    }
//                                } else {
//                                    setStatusText(getString(R.string.deck_outdated_please_update, response.getDeckVersion().getOriginalVersion()));
//                                    runOnUiThread(() -> {
//                                        dataBinding.updateDeckButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW)
//                                                .setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck))));
//                                        dataBinding.updateDeckButton.setVisibility(View.VISIBLE);
//                                    });
//                                    rollbackAccountCreation(createdAccount.getId());
//                                }
//                            } else {
//                                setStatusText(R.string.maintenance_mode);
//                                rollbackAccountCreation(createdAccount.getId());
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable throwable) {
//                            super.onError(throwable);
//                            if (throwable instanceof OfflineException o && OfflineException.Reason.OFFLINE.equals(o.getReason())) {
//                                setStatusText(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account);
//                            } else {
//                                setStatusText(throwable.getMessage());
//                                runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
//                            }
//                            rollbackAccountCreation(createdAccount.getId());
//                        }
//                    });
//                } catch (NextcloudFilesAppAccountNotFoundException e) {
//                    setStatusText(e.getMessage());
//                    runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(e, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
//                    rollbackAccountCreation(createdAccount.getId());
//                }
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                IResponseCallback.super.onError(error);
//                if (error instanceof SQLiteConstraintException) {
//                    DeckLog.warn("Account already added");
//                    runOnUiThread(() -> setStatusText(getString(R.string.account_already_added, accountToCreate.getName())));
//                } else {
//                    runOnUiThread(() -> {
//                        setStatusText(error.getMessage());
//                        it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(error, accountToCreate).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName());
//                    });
//                }
//                runOnUiThread(() -> dataBinding.addButton.setEnabled(true));
//                restoreWifiPref();
//                resetAvatar();
//            }
//        });
    }

    public Flowable<Boolean> hasAccounts() {
        return dataBaseAdapter.hasAnyAccounts();
    }

    public record ImportState(
            @NonNull String accountName,
            @NonNull String url,
            @NonNull String userName,
            int boardsTotal,
            int boardsImported,
            int boardsCurrentlyImporting
    ) {
    }
}
