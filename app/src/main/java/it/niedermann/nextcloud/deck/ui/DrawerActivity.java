package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.MediatorLiveData;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.deck.databinding.NavHeaderMainBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT;

public abstract class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final int MENU_ID_ABOUT = -1;
    protected static final int MENU_ID_ADD_BOARD = -2;
    protected static final int MENU_ID_SETTINGS = -3;
    protected static final int MENU_ID_ADD_ACCOUNT = -2;
    protected static final int ACTIVITY_ABOUT = 1;
    protected static final int ACTIVITY_SETTINGS = 2;
    protected static final long NO_ACCOUNTS = -1;
    protected static final long NO_BOARDS = -1;
    protected static final long NO_STACKS = -1;

    protected ActivityMainBinding binding;

    private String accountAlreadyAdded;
    private String sharedPreferenceLastAccount;
    private String urlFragmentUpdateDeck;
    private String addBoard;
    private String noAccount;
    private String addAccount;
    private int minimumServerAppMajor;
    private int minimumServerAppMinor;
    private int minimumServerAppPatch;

    protected List<Account> accountsList = new ArrayList<>();
    protected Account account;
    protected boolean accountChooserActive = false;
    protected SyncManager syncManager;
    protected SharedPreferences sharedPreferences;
    private Snackbar deckVersionTooLowSnackbar = null;
    private Snackbar accountIsGettingImportedSnackbar;

    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_SETTINGS) {
            if (resultCode == RESULT_OK) {
                recreate();
            }
        } else if (requestCode == ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT) {
            if (resultCode != RESULT_OK) {
                finish();
            } else {
                accountChooserActive = false;
            }
        } else {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, this, this::importNewAccount);
            } catch (AccountImportCancelledException e) {
                DeckLog.info("Account import has been canceled.");
            }
        }
    }

    private void importNewAccount(SingleSignOnAccount account) {

        final WrappedLiveData<Account> accountLiveData = this.syncManager.createAccount(new Account(account.name, account.userId, account.url));
        accountLiveData.observe(this, (Account createdAccount) -> {
            if (accountLiveData.hasError()) {
                try {
                    accountLiveData.throwError();
                } catch (SQLiteConstraintException ex) {
                    Snackbar.make(binding.coordinatorLayout, accountAlreadyAdded, Snackbar.LENGTH_LONG).show();
                }
            } else {
                // Remember last account - THIS HAS TO BE DONE SYNCHRONOUSLY
                SharedPreferences.Editor editor = sharedPreferences.edit();
                DeckLog.log("--- Write: shared_preference_last_account" + " | " + createdAccount.getId());
                editor.putLong(sharedPreferenceLastAccount, createdAccount.getId());
                editor.commit();
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), createdAccount.getName());
                syncManager = new SyncManager(DrawerActivity.this);

                try {
                    syncManager.getServerVersion(new IResponseCallback<Capabilities>(createdAccount) {
                        @Override
                        public void onResponse(Capabilities response) {
                            if (response.getDeckVersion().compareTo(new Version("", minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch)) < 0) {
                                deckVersionTooLowSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.your_deck_version_is_too_old, Snackbar.LENGTH_INDEFINITE).setAction("Learn more", v -> {
                                    new AlertDialog.Builder(DrawerActivity.this, Application.getAppTheme(getApplicationContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                                            .setTitle(R.string.update_deck)
                                            .setMessage(R.string.deck_outdated_please_update)
                                            .setPositiveButton(R.string.simple_update, (dialog, whichButton) -> {
                                                Intent openURL = new Intent(Intent.ACTION_VIEW);
                                                openURL.setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck));
                                                startActivity(openURL);
                                            })
                                            .setNegativeButton(R.string.simple_discard, null).show();
                                });
                                deckVersionTooLowSnackbar.show();
                                syncManager.deleteAccount(createdAccount.getId());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                DeckLog.log("--- Remove: shared_preference_last_account" + " | " + createdAccount.getId());
                                editor.remove(sharedPreferenceLastAccount);
                                editor.commit(); // Has to be done synchronously
                            } else {
                                SyncWorker.update(getApplicationContext());
                                accountIsGettingImportedSnackbar.show();
                            }
                        }
                    });
                } catch (OfflineException e) {
                    new AlertDialog.Builder(DrawerActivity.this)
                            .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                            .setPositiveButton(R.string.simple_close, null)
                            .show();
                    syncManager.deleteAccount(createdAccount.getId());
                    DeckLog.log("--- Remove: shared_preference_last_account" + " | " + createdAccount.getId());
                    editor.remove(sharedPreferenceLastAccount);
                    editor.commit(); // Has to be done synchronously
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountAlreadyAdded = getString(R.string.account_already_added);
        sharedPreferenceLastAccount = getString(R.string.shared_preference_last_account);
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);
        addBoard = getString(R.string.add_board);
        noAccount = getString(R.string.no_account);
        addAccount = getString(R.string.add_account);
        minimumServerAppMajor = getResources().getInteger(R.integer.minimum_server_app_major);
        minimumServerAppMinor = getResources().getInteger(R.integer.minimum_server_app_minor);
        minimumServerAppPatch = getResources().getInteger(R.integer.minimum_server_app_patch);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setSupportActionBar(binding.toolbar);

        accountIsGettingImportedSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.account_is_getting_imported, Snackbar.LENGTH_INDEFINITE);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        NavHeaderMainBinding headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0));

        switchMap(syncManager.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return syncManager.readAccounts();
            } else {
                accountSet(null);
                Intent intent = new Intent(this, ImportAccountActivity.class);
                startActivityForResult(intent, ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return new MediatorLiveData<>();
            }
        }).observe(this, (List<Account> accounts) -> {
            DeckLog.log("+++ readAccounts()");
            accountsList = accounts;
            long lastAccountId = sharedPreferences.getLong(sharedPreferenceLastAccount, NO_ACCOUNTS);
            DeckLog.log("--- Read: shared_preference_last_account" + " | " + lastAccountId);

            for (Account account : accounts) {
                if (lastAccountId == account.getId() || lastAccountId == NO_ACCOUNTS) {
                    this.account = account;
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setHeaderView();
                    ViewUtil.addAvatar(this, headerBinding.drawerCurrentAccount, this.account.getUrl(), this.account.getUserName(), R.mipmap.ic_launcher_round);
                    // TODO show spinner
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        registerAutoSyncOnNetworkAvailable();
                    } else {
                        syncManager.synchronize(new IResponseCallback<Boolean>(this.account) {
                            @Override
                            public void onResponse(Boolean response) {
                                accountIsGettingImportedSnackbar.dismiss();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable instanceof NextcloudHttpRequestFailedException) {
                                    ExceptionUtil.handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, DrawerActivity.this);
                                }
                            }
                        });
                    }
                    accountSet(this.account);
                    break;
                }
            }
        });

        binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_header_view).setOnClickListener(v -> {
            this.accountChooserActive = !this.accountChooserActive;
            if (accountChooserActive) {
                buildSidenavAccountChooser();
            } else {
                buildSidenavMenu();
            }
        });
    }

    protected void showAccountPicker() {
        if (deckVersionTooLowSnackbar != null) {
            deckVersionTooLowSnackbar.dismiss();
        }

        try {
            AccountImporter.pickNewAccount(this);
        } catch (NextcloudFilesAppNotInstalledException e) {
            UiExceptionManager.showDialogForException(this, e);
            Log.w("Deck", "=============================================================");
            Log.w("Deck", "Nextcloud app is not installed. Cannot choose account");
            e.printStackTrace();
        } catch (AndroidGetAccountsPermissionNotGranted e) {
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected abstract void accountSet(@Nullable Account account);

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (accountChooserActive) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case MENU_ID_ADD_ACCOUNT:
                    showAccountPicker();
                    break;
                default:
                    this.account = accountsList.get(item.getItemId());
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setHeaderView();
                    accountChooserActive = false;
                    accountSet(this.account);

                    // Remember last account
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    DeckLog.log("--- Write: shared_preference_last_account" + " | " + this.account.getId());
                    editor.putLong(sharedPreferenceLastAccount, this.account.getId());
                    editor.apply();
            }
        } else {
            switch (item.getItemId()) {
                case MENU_ID_ABOUT:
                    Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class)
                            .putExtra(BUNDLE_KEY_ACCOUNT, account);
                    startActivityForResult(aboutIntent, ACTIVITY_ABOUT);
                    break;
                case MENU_ID_SETTINGS:
                    Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(settingsIntent, ACTIVITY_SETTINGS);
                    break;
                case MENU_ID_ADD_BOARD:
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                    break;
                default:
                    boardSelected(item.getItemId(), account);
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    protected abstract void boardSelected(int itemId, Account account);

    protected void setHeaderView() {
        ViewUtil.addAvatar(this, binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_current_account), account.getUrl(), account.getUserName(), R.mipmap.ic_launcher_round);
        ((TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_username_full)).setText(account.getName());
    }

    protected void setNoAccountHeaderView() {
        ViewUtil.addAvatar(this, binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_current_account), null, "", R.mipmap.ic_launcher_round);
        ((TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_username_full)).setText(noAccount);
    }

    private void buildSidenavAccountChooser() {
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        if (accountsList == null || accountsList.size() == 0) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name_short);
            setNoAccountHeaderView();
        } else {
            int index = 0;
            for (Account account : this.accountsList) {
                final int currentIndex = index;
                MenuItem m = menu.add(Menu.NONE, index++, Menu.NONE, account.getName()).setIcon(R.drawable.ic_person_grey600_24dp);
                AppCompatImageButton contextMenu = new AppCompatImageButton(this);
                contextMenu.setBackgroundDrawable(null);

                String uri = account.getUrl() + "/index.php/avatar/" + Uri.encode(account.getUserName()) + "/56";
                Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                m.setIcon(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

                contextMenu.setImageDrawable(ViewUtil.getTintedImageView(this, R.drawable.ic_delete_black_24dp, R.color.grey600));
                contextMenu.setOnClickListener((v) -> {
                    if (currentIndex != 0) { // Select first account after deletion
                        this.account = accountsList.get(0);
                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                        syncManager = new SyncManager(this);
                        accountSet(this.account);
                        setHeaderView();
                        accountChooserActive = false;

                        // Remember last account
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        DeckLog.log("--- Write: shared_preference_last_account" + " | " + this.account.getId());
                        editor.putLong(sharedPreferenceLastAccount, this.account.getId());
                        editor.apply();
                    } else if (accountsList.size() > 1) { // Select second account after deletion
                        this.account = accountsList.get(1);
                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                        syncManager = new SyncManager(this);
                        accountSet(this.account);
                        setHeaderView();
                        accountChooserActive = false;

                        // Remember last account
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        DeckLog.log("--- Write: shared_preference_last_account" + " | " + this.account.getId());
                        editor.putLong(sharedPreferenceLastAccount, this.account.getId());
                        editor.apply();
                    } else {
                        accountsList.clear();
                    }

                    syncManager.deleteAccount(account.getId());
                    buildSidenavAccountChooser();
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                });
                m.setActionView(contextMenu);
            }
        }
        menu.add(Menu.NONE, MENU_ID_ADD_ACCOUNT, Menu.NONE, addAccount).setIcon(R.drawable.ic_person_add_black_24dp);
    }

    abstract void buildSidenavMenu();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerAutoSyncOnNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        if (connectivityManager != null) {
            if (networkCallback == null) {
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        DeckLog.log("Got Network connection");
                        syncManager.synchronize(new IResponseCallback<Boolean>(account) {
                            @Override
                            public void onResponse(Boolean response) {
                                accountIsGettingImportedSnackbar.dismiss();
                                DeckLog.log("Auto-Sync after connection available successful");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable instanceof NextcloudHttpRequestFailedException) {
                                    ExceptionUtil.handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, DrawerActivity.this);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        DeckLog.log("Network lost");
                    }
                };
            }
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (IllegalArgumentException ignored) {
            }
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }
}
