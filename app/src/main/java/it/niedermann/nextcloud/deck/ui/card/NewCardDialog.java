package it.niedermann.nextcloud.deck.ui.card;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.DialogNewCardBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.preparecreate.PrepareCreateViewModel;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

public class NewCardDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private PrepareCreateViewModel viewModel;

    private CreateCardListener createCardListener;

    private static final String ARG_ACCOUNT = "account";
    private static final String ARG_BOARD_LOCAL_ID = "board_id";
    private static final String ARG_STACK_LOCAL_ID = "stack_id";
    private static final String ARG_BRAND = "brand";

    private Account account;
    private long boardLocalId;
    private long stackLocalId;
    @ColorInt
    private int color;

    private DialogNewCardBinding binding;
    private final MutableLiveData<Boolean> isPending = new MutableLiveData<>(false);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CreateCardListener) {
            this.createCardListener = (CreateCardListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + CreateCardListener.class.getCanonicalName());
        }

        final var args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("Provide " + ARG_ACCOUNT + ", " + ARG_BOARD_LOCAL_ID + " and " + ARG_STACK_LOCAL_ID);
        }
        account = (Account) args.getSerializable(ARG_ACCOUNT);
        boardLocalId = args.getLong(ARG_BOARD_LOCAL_ID);
        stackLocalId = args.getLong(ARG_STACK_LOCAL_ID);
        color = args.getInt(ARG_BRAND);
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

        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.colorTextInputLayout(binding.inputWrapper);
        utils.platform.colorCircularProgressBar(binding.progressCircular);

        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final boolean inputIsValid = inputIsValid(binding.input.getText());
                if (inputIsValid) {
                    binding.inputWrapper.setError(null);
                }
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(inputIsValid);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(inputIsValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        distinctUntilChanged(isPending).observe(this, (isPending) -> {
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
        binding.input.requestFocus();
        requireDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                final var fullCard = viewModel.createFullCard(account.getServerDeckVersionAsObject(), currentUserInput.toString());
                viewModel.saveCard(account, boardLocalId, stackLocalId, fullCard, new IResponseCallback<>() {
                    @Override
                    public void onResponse(FullCard createdCard) {
                        requireActivity().runOnUiThread(() -> {
                            createCardListener.onCardCreated(createdCard);

                            if (openOnSuccess) {
                                startActivity(EditActivity.createEditCardIntent(requireContext(), account, boardLocalId, createdCard.getLocalId()));
                            }
                            dismiss();
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        IResponseCallback.super.onError(throwable);
                        requireActivity().runOnUiThread(() -> {
                            isPending.setValue(false);
                            if (throwable instanceof OfflineException) {
                                Toast.makeText(requireContext(), ((OfflineException) throwable).getReason().getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                ExceptionDialogFragment
                                        .newInstance(throwable, account)
                                        .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                            }
                        });
                    }
                });
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

    public static DialogFragment newInstance(@NonNull Account account, long boardLocalId, long stackLocalId, @ColorInt int brand) {
        final var fragment = new NewCardDialog();
        final var args = new Bundle();
        args.putSerializable(ARG_ACCOUNT, account);
        args.putLong(ARG_BOARD_LOCAL_ID, boardLocalId);
        args.putLong(ARG_STACK_LOCAL_ID, stackLocalId);
        args.putInt(ARG_BRAND, brand);
        fragment.setArguments(args);
        return fragment;
    }
}
