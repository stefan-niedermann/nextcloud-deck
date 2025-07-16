package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.shared.SharedExecutors;


public abstract class AbstractAccountRelatedRepository extends AbstractRepository {

    @NonNull
    protected final DataPropagationHelper dataPropagationHelper;
    @NonNull
    protected final ServerAdapter serverAdapter;

    @AnyThread
    protected AbstractAccountRelatedRepository(@NonNull Context context,
                                               @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        this(context, AccountImporter.getSingleSignOnAccount(context, account.getName()), new ConnectivityUtil(context));
    }

    private AbstractAccountRelatedRepository(@NonNull Context context,
                                             @NonNull SingleSignOnAccount ssoAccount,
                                             @NonNull ConnectivityUtil connectivityUtil) {
        this(context, new ServerAdapter(context.getApplicationContext(), ssoAccount, connectivityUtil), connectivityUtil, SyncHelper::new);
    }

    protected AbstractAccountRelatedRepository(@NonNull Context context,
                                               @NonNull ServerAdapter serverAdapter,
                                               @NonNull ConnectivityUtil connectivityUtil,
                                               @NonNull SyncHelper.Factory syncHelperFactory) {
        super(context, connectivityUtil);
        this.serverAdapter = serverAdapter;
        this.dataPropagationHelper = new DataPropagationHelper(
                serverAdapter,
                new DataBaseAdapter(context.getApplicationContext()),
                SharedExecutors.getIoDbWriteHighPriority());
    }

}
