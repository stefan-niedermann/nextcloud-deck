package it.niedermann.nextcloud.deck;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.List;

import io.reactivex.functions.Consumer;
import it.niedermann.nextcloud.deck.api.DeckAPI;
import it.niedermann.nextcloud.deck.api.DeckAPI_SSO;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.DataBaseAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NextcloudAPI mNextcloudAPI;
    private DataBaseAdapter dataBaseAdapter;
    private LoginDialogFragment loginDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.dataBaseAdapter = DataBaseAdapter.getInstance(this.getApplicationContext());
        if(this.dataBaseAdapter.hasAccounts()) {
            DeckAPI mApi;
            SingleSignOnAccount account = null;
            try {
                account = SingleAccountHelper.getCurrentSingleSignOnAccount(getApplicationContext());
                //SingleSignOnAccount ssoAccount =
                //        AccountImporter.getAuthTokenInSeparateThread(context, account);
                NextcloudAPI nextcloudAPI = new NextcloudAPI(getApplicationContext(), account, new Gson(), new NextcloudAPI.ApiConnectedListener() {

                    @Override
                    public void onConnected() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                mApi = new DeckAPI_SSO(nextcloudAPI);
                mApi.boards().subscribe(new Consumer<List<Board>>() {
                    @Override
                    public void accept(List<Board> boards) throws Exception {
                        Log.v("Deck", "=============================================================");
                        Log.v("Deck", "" + boards.size());
                    }
                });
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                e.printStackTrace();
            } catch (NoCurrentAccountSelectedException e) {
                e.printStackTrace();
            }



            /*
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    String accountName = dataBaseAdapter.readAccounts().get(0).getName();
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), accountName);
                    try {
                        Log.v("Deck", "===============================================================================");
                        Log.v("Deck", "Bestehenden Account erkannt: " + SingleAccountHelper.getCurrentSingleSignOnAccount(getApplicationContext()).name);

                        SingleSignOnAccount acc = SingleAccountHelper.getCurrentSingleSignOnAccount(getApplicationContext());

                        mNextcloudAPI= new NextcloudAPI(getApplicationContext(), acc, new Gson(),  new NextcloudAPI.ApiConnectedListener() {
                            @Override
                            public void onConnected() {

                                NextcloudRequest nextcloudRequest = new NextcloudRequest.Builder()
                                        .setMethod("GET")
                                        .setUrl("/index.php/apps/deck/boards")
                                        .build();

                                try {
                                    InputStream inputStream = mNextcloudAPI.performNetworkRequest(nextcloudRequest);
                                    while(inputStream.available() > 0) {
                                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                                        Log.v("Deck", br.readLine());
                                        // TODO do something useful with the data here..
                                        // like writing it to a file..?
                                    }
                                    inputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception ex) {
                                // TODO handle error here..
                                ex.printStackTrace();
                            }
                        });
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoCurrentAccountSelectedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.doInBackground();*/
        } else {
            loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(this.getSupportFragmentManager(), "NoticeDialogFragment");
        }
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();
        this.dataBaseAdapter.createAccount(account.name);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
