package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogExceptionBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.tips.TipsAdapter;
import it.niedermann.nextcloud.exception.ExceptionUtil;

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

        final String debugInfos = ExceptionUtil.INSTANCE.getDebugInfos(requireContext(), throwable, BuildConfig.FLAVOR, account == null ? null : account.getServerDeckVersion());

        binding.tips.setAdapter(adapter);
        binding.stacktrace.setText(debugInfos);

        DeckLog.logError(throwable);

        adapter.setThrowable(requireContext(), account, throwable);

        return new AlertDialog.Builder(requireActivity())
                .setView(binding.getRoot())
                .setTitle(R.string.error_dialog_title)
                .setPositiveButton(android.R.string.copy, (a, b) -> {
                    ClipboardUtil.INSTANCE.copyToClipboard(requireContext(), getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
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
