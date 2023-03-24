package it.niedermann.nextcloud.deck.ui.card;

import static androidx.core.content.ContextCompat.getMainExecutor;
import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogNewCardBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.preparecreate.PrepareCreateViewModel;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.ThemedDialogFragment;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;
import it.niedermann.nextcloud.deck.util.KeyboardUtils;
import it.niedermann.nextcloud.deck.util.OnTextChangedWatcher;

public class NewCardDialog extends ThemedDialogFragment implements DialogInterface.OnClickListener {

    private NewCardViewModel newCardViewModel;
    private PrepareCreateViewModel viewModel;
    private CreateCardListener createCardListener;
    private DialogNewCardBinding binding;
    private final MutableLiveData<Boolean> isPending = new MutableLiveData<>(false);

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";
    private static final String KEY_STACK_ID = "stack_id";

    private Account account;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CreateCardListener) {
            this.createCardListener = (CreateCardListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + CreateCardListener.class.getCanonicalName());
        }

        final var args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }
        this.account = (Account) getArguments().getSerializable(KEY_ACCOUNT);

        newCardViewModel = new SyncViewModel.Provider(requireActivity(), requireActivity().getApplication(), account).get(NewCardViewModel.class);
        DeckLog.log(newCardViewModel);
        viewModel = new ViewModelProvider(requireActivity()).get(PrepareCreateViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogNewCardBinding.inflate(requireActivity().getLayoutInflater());

        final var dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.add_card)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_save, null)
                .setNegativeButton(R.string.edit, null)
                .create();

        dialog.setOnShowListener(d -> {
            final boolean inputIsValid = inputIsValid(binding.input.getText());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(inputIsValid);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> onClick(dialog, DialogInterface.BUTTON_POSITIVE));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(inputIsValid);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
        });

        binding.input.addTextChangedListener(new OnTextChangedWatcher(s -> {
            final boolean inputIsValid = inputIsValid(binding.input.getText());
            if (inputIsValid) {
                binding.inputWrapper.setError(null);
            }
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(inputIsValid);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(inputIsValid);
        }));

        distinctUntilChanged(isPending).observe(this, isPending -> {
            if (isPending) {
                binding.inputWrapper.setVisibility(View.INVISIBLE);
                binding.progressCircular.setVisibility(View.VISIBLE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            } else {
                binding.inputWrapper.setVisibility(View.VISIBLE);
                binding.progressCircular.setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
            }
        });

        binding.input.setOnEditorActionListener((textView, actionId, event) -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    return true;
            }
            return false;
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KeyboardUtils.showKeyboardForEditText(binding.input);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final boolean openOnSuccess;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                openOnSuccess = false;
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                openOnSuccess = true;
                break;
            default:
                return;
        }
        if (Boolean.FALSE.equals(isPending.getValue())) {
            isPending.setValue(true);
            final var currentUserInput = binding.input.getText();
            if (inputIsValid(currentUserInput)) {
                // TODO Check args in onAttach
                final var args = getArguments();
                assert args != null;
                newCardViewModel.createFullCard(account.getId(), args.getLong(KEY_BOARD_ID), args.getLong(KEY_STACK_ID), currentUserInput.toString()).whenCompleteAsync((fullCard, throwable) -> {
                    if (throwable != null) {
                        isPending.setValue(false);
                        if (throwable instanceof OfflineException) {
                            Toast.makeText(requireContext(), ((OfflineException) throwable).getReason().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            newCardViewModel.getCurrentAccount()
                                    .thenAcceptAsync(account -> ExceptionDialogFragment
                                            .newInstance(throwable, account)
                                            .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()), getMainExecutor(requireContext()));
                        }
                    } else {
                        createCardListener.onCardCreated(fullCard);
                        if (openOnSuccess) {
                            newCardViewModel
                                    .createEditIntent(requireContext(), fullCard.getAccountId(), args.getLong(KEY_BOARD_ID), fullCard.getLocalId())
                                    .thenAcceptAsync(this::startActivity, getMainExecutor(requireContext()));
                        }
                        dismiss();
                    }
                }, getMainExecutor(requireContext()));
            } else {
                binding.inputWrapper.setError(getString(R.string.title_is_mandatory));
                binding.input.requestFocus();
                isPending.setValue(false);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        this.createCardListener.onDismiss(dialog);
    }

    private static boolean inputIsValid(@Nullable CharSequence input) {
        return input != null && !input.toString().trim().isEmpty();
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardId, long stackId) {
        final NewCardDialog dialog = new NewCardDialog();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardId);
        args.putLong(KEY_STACK_ID, stackId);
        dialog.setArguments(args);

        return dialog;

    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.colorTextInputLayout(binding.inputWrapper);
        utils.platform.colorCircularProgressBar(binding.progressCircular, ColorRole.PRIMARY);
    }
}
