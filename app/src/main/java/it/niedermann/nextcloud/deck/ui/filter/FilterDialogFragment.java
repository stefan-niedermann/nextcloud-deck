package it.niedermann.nextcloud.deck.ui.filter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class FilterDialogFragment extends BrandedDialogFragment {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";
    private static final String KEY_FILTER_INFORMATION = "filterInformation";

    private FilterChangeListener filterChangeListener;
    private DialogFilterBinding binding;
    private SyncManager syncManager;

    private EDueTypeAdapter overdueAdapter;

    private Account account;
    private long boardId;
    private FilterInformation filterInformation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(requireActivity() instanceof FilterChangeListener)) {
            throw new IllegalArgumentException("Caller must implement" + FilterChangeListener.class.getSimpleName());
        }

        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_BOARD_ID) || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be provided as arguments");
        }

        this.filterChangeListener = (FilterChangeListener) requireActivity();
        this.boardId = args.getLong(KEY_BOARD_ID);
        this.account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (this.boardId == 0L || this.account == null) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be valid localIds and not be 0 or null");
        }

        Object filterInformation = args.getSerializable(KEY_FILTER_INFORMATION);
        if (filterInformation instanceof FilterInformation) {
            this.filterInformation = (FilterInformation) filterInformation;
        } else {
            this.filterInformation = new FilterInformation();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        binding = DialogFilterBinding.inflate(requireActivity().getLayoutInflater());
        overdueAdapter = new EDueTypeAdapter(requireContext());
        binding.overdue.setAdapter(overdueAdapter);
        binding.overdue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInformation.setDueType(overdueAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterInformation.setDueType(null);
            }
        });

        syncManager = new SyncManager(requireActivity());
        return dialogBuilder
                .setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.simple_filter, (a, b) -> filterChangeListener.onFilterChanged(filterInformation))
                .create();
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardId, @Nullable FilterInformation filterInformation) {
        final DialogFragment dialog = new FilterDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardId);
        if (filterInformation != null) {
            args.putSerializable(KEY_FILTER_INFORMATION, filterInformation);
        }
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {

    }
}
