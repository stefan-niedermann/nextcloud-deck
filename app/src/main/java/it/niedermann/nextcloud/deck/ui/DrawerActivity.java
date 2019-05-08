package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.login.LoginDialogFragment;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public abstract class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final int MENU_ID_ABOUT = -1;
    protected static final int MENU_ID_ADD_BOARD = -2;
    protected static final int MENU_ID_ADD_ACCOUNT = -2;
    protected static final int ACTIVITY_ABOUT = 1;
    protected static final long NO_BOARDS = -1;
    protected static final long NO_STACKS = -1;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected List<Account> accountsList = new ArrayList<>();
    protected Account account;
    protected boolean accountChooserActive = false;
    private LoginDialogFragment loginDialogFragment;
    protected SyncManager syncManager;
    protected SharedPreferences sharedPreferences;
    private HeaderViewHolder headerViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this.coordinatorLayout, this));
        setSupportActionBar(toolbar);

        View header = navigationView.getHeaderView(0);
        headerViewHolder = new HeaderViewHolder(header);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(getApplicationContext(), this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        syncManager.hasAccounts().observe(this, (Boolean hasAccounts) -> {
            if (hasAccounts != null && hasAccounts) {
                syncManager.readAccounts().observe(this, (List<Account> accounts) -> {
                    if (accounts != null) {
                        accountsList = accounts;
                        int lastAccount = sharedPreferences.getInt(getString(R.string.shared_preference_last_account), 0);
                        if (accounts.size() > lastAccount) {
                            this.account = accounts.get(lastAccount);
                            SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                            setHeaderView();
                            syncManager = new SyncManager(getApplicationContext(), this);
                            ViewUtil.addAvatar(this, headerViewHolder.currentAccountAvatar, this.account.getUrl(), this.account.getUserName());
                            // TODO show spinner
                            syncManager.synchronize(new IResponseCallback<Boolean>(this.account) {
                                @Override
                                public void onResponse(Boolean response) {
                                    //nothing
                                }
                            });
                            accountSet(this.account);
                        }
                    }
                });
            } else {
                loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.show(this.getSupportFragmentManager(), "NoticeDialogFragment");
            }
        });

        navigationView.getHeaderView(0).findViewById(R.id.drawer_header_view).setOnClickListener(v -> {
            this.accountChooserActive = !this.accountChooserActive;
            if (accountChooserActive) {
                buildSidenavAccountChooser();
            } else {
                buildSidenavMenu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected abstract void accountSet(Account account);

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (accountChooserActive) {
            switch (item.getItemId()) {
                case MENU_ID_ADD_ACCOUNT:
                    loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(this.getSupportFragmentManager(), "NoticeDialogFragment");
                    break;
                default:
                    this.account = accountsList.get(item.getItemId());
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    setHeaderView();
                    accountChooserActive = false;
                    accountSet(this.account);

                    // Remember last account
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(getString(R.string.shared_preference_last_account), item.getItemId());
                    editor.apply();
            }
        } else {
            switch (item.getItemId()) {
                case MENU_ID_ABOUT:
                    Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivityForResult(aboutIntent, ACTIVITY_ABOUT);
                    break;
                case MENU_ID_ADD_BOARD:
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), getString(R.string.create_board));
                    break;
                default:
                    boardSelected(item.getItemId(), account);
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected abstract void boardSelected(int itemId, Account account);

    protected void setHeaderView() {
        ViewUtil.addAvatar(this, navigationView.getHeaderView(0).findViewById(R.id.drawer_current_account), account.getUrl(), account.getUserName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username_full)).setText(account.getName());
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();
        Account acc = new Account();
        acc.setName(account.name);
        acc.setUserName(account.username);
        acc.setUrl(account.url);
        final WrappedLiveData<Account> accountLiveData = this.syncManager.createAccount(acc);
        accountLiveData.observe(this, (Account ac) -> {
            if (accountLiveData.hasError()) {
                try {
                    accountLiveData.throwError();
                } catch (SQLiteConstraintException ex) {
                    Snackbar.make(coordinatorLayout, "Account bereits hinzugefügt", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                syncManager.synchronize(new IResponseCallback<Boolean>(ac) {
                    @Override
                    public void onResponse(Boolean response) {

                    }
                });
                Snackbar.make(coordinatorLayout, "Account hinzugefügt", Snackbar.LENGTH_SHORT).show();
            }
        });

        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
    }

    private void buildSidenavAccountChooser() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        int index = 0;
        for (Account account : this.accountsList) {
            MenuItem m = menu.add(Menu.NONE, index++, Menu.NONE, account.getName()).setIcon(R.drawable.ic_person_grey600_24dp);
            AppCompatImageButton contextMenu = new AppCompatImageButton(this);
            contextMenu.setBackgroundDrawable(null);
            contextMenu.setImageDrawable(ViewUtil.getTintedImageView(this, R.drawable.ic_delete_black_24dp, R.color.grey600));
            contextMenu.setOnClickListener((v) -> {
                syncManager.deleteAccount(account.getId());
            });
            m.setActionView(contextMenu);
        }
        menu.add(Menu.NONE, MENU_ID_ADD_ACCOUNT, Menu.NONE, getString(R.string.add_account)).setIcon(R.drawable.ic_person_add_black_24dp);
    }

    abstract void buildSidenavMenu();

    protected static class HeaderViewHolder {
        @BindView(R.id.drawer_current_account)
        ImageView currentAccountAvatar;
        @BindView(R.id.drawer_account_middle)
        ImageView accountMiddleAvatar;
        @BindView(R.id.drawer_account_end)
        ImageView accountEndAvatar;

        @BindView(R.id.drawer_menu_toggle)
        LinearLayout drawerMenuToggle;
        @BindView(R.id.drawer_account_chooser_toggle)
        ImageView drawerMenuToggleIcon;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
