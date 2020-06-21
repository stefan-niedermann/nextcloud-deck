package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.nextcloud.android.sso.exceptions.NextcloudApiNotRespondingException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotSupportedException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.TokenMismatchException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogExceptionBinding;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.tips.TipsAdapter;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class ExceptionDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_THROWABLE = "throwable";
    private static final String KEY_ACCOUNT = "account";
    public static final String INTENT_EXTRA_BUTTON_TEXT = "button_text";

    private Throwable throwable;

    @Nullable
    private Account account;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();
        if (args != null) {
            this.throwable = (Throwable) args.getSerializable(KEY_THROWABLE);
            if (this.throwable == null) {
                throwable = new IllegalArgumentException("Did not receive any exception in " + ExceptionDialogFragment.class.getSimpleName());
            }
            this.account = (Account) args.getSerializable(KEY_ACCOUNT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = View.inflate(getContext(), R.layout.dialog_exception, null);
        final DialogExceptionBinding binding = DialogExceptionBinding.bind(view);

        final TipsAdapter adapter = new TipsAdapter((actionIntent) -> requireActivity().startActivity(actionIntent));

        final String debugInfos = ExceptionUtil.getDebugInfos(requireContext(), throwable, account);

        binding.tips.setAdapter(adapter);
        binding.stacktrace.setText(debugInfos);

        DeckLog.logError(throwable);

        if (throwable instanceof TokenMismatchException) {
            adapter.add(R.string.error_dialog_tip_token_mismatch_retry);
            adapter.add(R.string.error_dialog_tip_token_mismatch_clear_storage);
            Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                    .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_deck_info);
            adapter.add(R.string.error_dialog_tip_clear_storage, intent);
        } else if (throwable instanceof NextcloudFilesAppNotSupportedException) {
            adapter.add(R.string.error_dialog_tip_files_outdated);
        } else if (throwable instanceof NextcloudApiNotRespondingException) {
            adapter.add(R.string.error_dialog_tip_files_force_stop);
            adapter.add(R.string.error_dialog_tip_files_delete_storage);
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectException) {
            adapter.add(R.string.error_dialog_timeout_instance);
            adapter.add(R.string.error_dialog_timeout_toggle, new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_network));
        } else if (throwable instanceof JSONException || throwable instanceof NullPointerException) {
            adapter.add(R.string.error_dialog_check_server);
        } else if (throwable instanceof NextcloudHttpRequestFailedException) {
            int statusCode = ((NextcloudHttpRequestFailedException) throwable).getStatusCode();
            switch (statusCode) {
                case 302:
                    adapter.add(R.string.error_dialog_redirect);
                    break;
                case 500:
                    if (account != null) {
                        adapter.add(R.string.error_dialog_check_server_logs, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_server_logs)
                                .setData(Uri.parse(account.getUrl() + getString(R.string.url_fragment_server_logs))));
                    } else {
                        adapter.add(R.string.error_dialog_check_server_logs);
                    }
                    break;
                case 503:
                    adapter.add(R.string.error_dialog_check_maintenance);
                    break;
                case 507:
                    adapter.add(R.string.error_dialog_insufficient_storage);
                    break;
            }
        } else if (throwable instanceof UploadAttachmentFailedException) {
            adapter.add(R.string.error_dialog_attachment_upload_failed);
        } else if (throwable instanceof DeckException) {
            switch (((DeckException) throwable).getHint()) {
                case CAPABILITIES_VERSION_NOT_PARSABLE:
                    if (account != null) {
                        adapter.add(R.string.error_dialog_version_not_parsable, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_install)
                                .setData(Uri.parse(account.getUrl() + getString(R.string.url_fragment_install_deck))));
                    } else {
                        adapter.add(R.string.error_dialog_version_not_parsable);
                    }
                    break;
                case CAPABILITIES_NOT_PARSABLE:
                default:
                    if (account != null) {
                        adapter.add(R.string.error_dialog_capabilities_not_parsable, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_server_logs)
                                .setData(Uri.parse(account.getUrl() + getString(R.string.url_fragment_server_logs))));
                    } else {
                        adapter.add(R.string.error_dialog_capabilities_not_parsable);
                    }
            }
        }

        return new AlertDialog.Builder(requireActivity())
                .setView(binding.getRoot())
                .setTitle(R.string.error_dialog_title)
                .setPositiveButton(android.R.string.copy, (a, b) -> {
                    copyToClipboard(requireContext(), getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                    a.dismiss();
                })
                .setNegativeButton(R.string.simple_close, null)
                .create();
    }

    public static DialogFragment newInstance(Throwable throwable, @Nullable Account account) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_THROWABLE, throwable);
        args.putSerializable(KEY_ACCOUNT, account);
        final DialogFragment fragment = new ExceptionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
