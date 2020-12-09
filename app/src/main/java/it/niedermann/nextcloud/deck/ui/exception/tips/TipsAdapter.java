package it.niedermann.nextcloud.deck.ui.exception.tips;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudApiNotRespondingException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotSupportedException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.TokenMismatchException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Account;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment.INTENT_EXTRA_BUTTON_TEXT;

public class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {

    private static final Intent INTENT_APP_INFO = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
            .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_deck_info);

    @NonNull
    private Consumer<Intent> actionButtonClickedListener;
    @NonNull
    private List<TipsModel> tips = new LinkedList<>();

    public TipsAdapter(@NonNull Consumer<Intent> actionButtonClickedListener) {
        this.actionButtonClickedListener = actionButtonClickedListener;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);
        return new TipsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        holder.bind(tips.get(position), actionButtonClickedListener);
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    public void setThrowable(@NonNull Context context, @Nullable Account account, @NonNull Throwable throwable) {
        if (throwable instanceof TokenMismatchException) {
            add(R.string.error_dialog_tip_token_mismatch_retry);
            add(R.string.error_dialog_tip_clear_storage_might_help);
            add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
        } else if (throwable instanceof NextcloudFilesAppNotSupportedException) {
            add(R.string.error_dialog_tip_files_outdated);
        } else if (throwable instanceof NextcloudApiNotRespondingException) {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                add(R.string.error_dialog_tip_disable_battery_optimizations, new Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_battery_settings));
            } else {
                add(R.string.error_dialog_tip_disable_battery_optimizations);
            }
            add(R.string.error_dialog_tip_files_force_stop);
            add(R.string.error_dialog_tip_files_delete_storage);
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectException) {
            add(R.string.error_dialog_timeout_instance);
            add(R.string.error_dialog_timeout_toggle, new Intent(Settings.ACTION_WIFI_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_network));
        } else if (throwable instanceof JSONException || throwable instanceof NullPointerException) {
            add(R.string.error_dialog_check_server);
        } else if (throwable instanceof NextcloudHttpRequestFailedException) {
            int statusCode = ((NextcloudHttpRequestFailedException) throwable).getStatusCode();
            switch (statusCode) {
                case 302:
                    add(R.string.error_dialog_redirect);
                    break;
                case 500:
                    if (account != null) {
                        add(R.string.error_dialog_check_server_logs, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_server_logs)
                                .setData(Uri.parse(account.getUrl() + context.getString(R.string.url_fragment_server_logs))));
                    } else {
                        add(R.string.error_dialog_check_server_logs);
                    }
                    break;
                case 503:
                    add(R.string.error_dialog_check_maintenance);
                    break;
                case 507:
                    add(R.string.error_dialog_insufficient_storage);
                    break;
            }
        } else if (throwable instanceof UploadAttachmentFailedException) {
            add(R.string.error_dialog_attachment_upload_failed);
        } else if (throwable instanceof DeckException) {
            switch (((DeckException) throwable).getHint()) {
                case CAPABILITIES_VERSION_NOT_PARSABLE:
                    if (account != null) {
                        add(R.string.error_dialog_version_not_parsable, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_install)
                                .setData(Uri.parse(account.getUrl() + context.getString(R.string.url_fragment_install_deck))));
                    } else {
                        add(R.string.error_dialog_version_not_parsable);
                    }
                    add(R.string.error_dialog_account_might_not_be_authorized);
                    break;
                case UNKNOWN_ACCOUNT_USER_ID:
                    add(R.string.error_dialog_user_not_found_in_database);
                    break;
                case CAPABILITIES_NOT_PARSABLE:
                default:
                    if (account != null) {
                        add(R.string.error_dialog_capabilities_not_parsable, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_server_logs)
                                .setData(Uri.parse(account.getUrl() + context.getString(R.string.url_fragment_server_logs))));
                    } else {
                        add(R.string.error_dialog_capabilities_not_parsable);
                    }
            }
            // Files app might no longer be authenticated: https://github.com/stefan-niedermann/nextcloud-deck/issues/621#issuecomment-665533567
            add(R.string.error_dialog_tip_clear_storage_might_help);
            add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
        } else if (throwable instanceof RuntimeException) {
            if (throwable.getMessage() != null && throwable.getMessage().contains("database")) {
                Intent reportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_report_bug)))
                        .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_report_issue);
                add(R.string.error_dialog_tip_database_upgrade_failed, reportIntent);
                add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
            }
        }
    }

    public void add(@StringRes int text) {
        add(text, null);
    }

    public void add(@StringRes int text, @Nullable Intent primaryAction) {
        tips.add(new TipsModel(text, primaryAction));
        notifyItemInserted(tips.size());
    }
}