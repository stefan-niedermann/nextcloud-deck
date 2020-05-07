package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudApiNotRespondingException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotSupportedException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.TokenMismatchException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogExceptionBinding;
import it.niedermann.nextcloud.deck.databinding.ItemTipBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class ExceptionDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_THROWABLES = "throwables";
    private static final String KEY_ACCOUNT = "account";
    private static final String INTENT_EXTRA_BUTTON_TEXT = "button_text";

    @NonNull
    private ArrayList<Throwable> throwables = new ArrayList<>();

    @Nullable
    private Account account;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();
        if (args != null) {
            final Object throwablesArgument = args.getSerializable(KEY_THROWABLES);
            if (throwablesArgument != null) {
                throwables.addAll((ArrayList<Throwable>) throwablesArgument);
            }
            final Object accountArgument = args.getSerializable(KEY_ACCOUNT);
            if (accountArgument != null) {
                this.account = (Account) accountArgument;
            }
        }
        if (throwables.size() == 0) {
            throwables.add(new IllegalArgumentException("Did not receive any exception in " + ExceptionDialogFragment.class.getSimpleName()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = View.inflate(getContext(), R.layout.dialog_exception, null);
        final DialogExceptionBinding binding = DialogExceptionBinding.bind(view);

        final TipsAdapter adapter = new TipsAdapter();

        final String debugInfos = ExceptionUtil.getDebugInfos(requireContext(), throwables, account);

        binding.tips.setAdapter(adapter);
        binding.stacktrace.setText(debugInfos);

        for (Throwable t : throwables) {
            DeckLog.logError(t);
            if (t instanceof TokenMismatchException) {
                adapter.add(R.string.error_dialog_tip_token_mismatch_retry);
                adapter.add(R.string.error_dialog_tip_token_mismatch_clear_storage);
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                        .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_deck_info);
                adapter.add(R.string.error_dialog_tip_clear_storage, intent);
            } else if (t instanceof NextcloudFilesAppNotSupportedException) {
                adapter.add(R.string.error_dialog_tip_files_outdated);
            } else if (t instanceof NextcloudApiNotRespondingException) {
                adapter.add(R.string.error_dialog_tip_files_force_stop);
                adapter.add(R.string.error_dialog_tip_files_delete_storage);
            } else if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
                adapter.add(R.string.error_dialog_timeout_instance);
                adapter.add(R.string.error_dialog_timeout_toggle, new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_network));
            } else if (t instanceof JSONException || t instanceof NullPointerException) {
                adapter.add(R.string.error_dialog_check_server);
            } else if (t instanceof NextcloudHttpRequestFailedException) {
                int statusCode = ((NextcloudHttpRequestFailedException) t).getStatusCode();
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

    public static DialogFragment newInstance(Throwable exception, @Nullable Account account) {
        final Bundle args = new Bundle();
        final ArrayList<Throwable> list = new ArrayList<>(1);
        list.add(exception);
        args.putSerializable(KEY_THROWABLES, list);
        args.putSerializable(KEY_ACCOUNT, account);
        final DialogFragment fragment = new ExceptionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {

        @NonNull
        private List<TipsModel> tips = new LinkedList<>();

        @NonNull
        @Override
        public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);
            return new TipsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
            holder.bind(tips.get(position));
        }

        @Override
        public int getItemCount() {
            return tips.size();
        }

        private void add(@StringRes int tip) {
            add(tip, null, null);
        }

        private void add(@StringRes int tip, @Nullable Intent primaryAction) {
            add(tip, primaryAction, null);
        }

        private void add(@StringRes int text, @Nullable Intent primaryAction, @Nullable Intent secondaryAction) {
            tips.add(new TipsModel(text, primaryAction, secondaryAction));
            notifyItemInserted(tips.size());
        }
    }

    private static class TipsViewHolder extends RecyclerView.ViewHolder {
        private final ItemTipBinding binding;

        private TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTipBinding.bind(itemView);
        }

        public void bind(TipsModel tip) {
            binding.tip.setText(tip.text);
            if (tip.primaryAction != null && tip.primaryAction.hasExtra(INTENT_EXTRA_BUTTON_TEXT)) {
                binding.actionPrimary.setVisibility(View.VISIBLE);
                binding.actionPrimary.setText(tip.primaryAction.getIntExtra(INTENT_EXTRA_BUTTON_TEXT, 0));
                binding.actionPrimary.setOnClickListener((v) -> binding.getRoot().getRootView().getContext().startActivity(tip.primaryAction));
            } else {
                binding.actionPrimary.setVisibility(View.GONE);
            }
            if (tip.secondaryAction != null && tip.secondaryAction.hasExtra(INTENT_EXTRA_BUTTON_TEXT)) {
                binding.actionSecondary.setVisibility(View.VISIBLE);
                binding.actionSecondary.setText(tip.secondaryAction.getIntExtra(INTENT_EXTRA_BUTTON_TEXT, 0));
                binding.actionSecondary.setOnClickListener((v) -> binding.getRoot().getRootView().getContext().startActivity(tip.secondaryAction));
            } else {
                binding.actionSecondary.setVisibility(View.GONE);
            }
        }
    }

    private static class TipsModel {
        @StringRes
        int text;
        @Nullable
        Intent primaryAction;
        @Nullable
        Intent secondaryAction;

        TipsModel(@StringRes int text, @Nullable Intent primaryAction, @Nullable Intent secondaryAction) {
            this.text = text;
            this.primaryAction = primaryAction;
            this.secondaryAction = secondaryAction;
        }
    }
}
