package it.niedermann.nextcloud.deck.ui.filter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class FilterDialogFragment extends BrandedDialogFragment {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";

    private MainViewModel viewModel;
    private LabelFilterAdapter labelAdapter;
    private UserFilterAdapter userAdapter;
    private OverdueFilterAdapter overdueAdapter;
    private FilterInformation filterInformation;

    private Account account;
    private long boardId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(requireActivity() instanceof MainActivity)) {
            throw new IllegalArgumentException("Dialog must be called from " + MainActivity.class.getSimpleName());
        }

        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_BOARD_ID) || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be provided as arguments");
        }

        boardId = args.getLong(KEY_BOARD_ID);
        account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (boardId <= 0L || account == null) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be valid localIds and not be 0 or null");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        this.filterInformation = viewModel.getFilterInformation().getValue();
        if (this.filterInformation == null) {
            this.filterInformation = new FilterInformation();
        }

        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        final DialogFilterBinding binding = DialogFilterBinding.inflate(requireActivity().getLayoutInflater());

        overdueAdapter = new OverdueFilterAdapter(requireContext());
        userAdapter = new UserFilterAdapter(requireContext());

        binding.overdue.setAdapter(overdueAdapter);
        binding.people.setAdapter(userAdapter);

        binding.overdue.setSelection(overdueAdapter.getPosition(this.filterInformation.getDueType()));
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

        SyncManager syncManager = new SyncManager(requireActivity());

        observeOnce(syncManager.findProposalsForLabelsToAssign(account.getId(), boardId), requireActivity(), (labels) -> {
            labelAdapter = new LabelFilterAdapter(requireContext(), labels, this.filterInformation.getLabelIDs());
            binding.labels.setNestedScrollingEnabled(false);
            binding.labels.setAdapter(labelAdapter);
        });

        observeOnce(syncManager.findProposalsForUsersToAssign(account.getId(), boardId), requireActivity(), (users) -> {
            userAdapter.addAll(users);
            userAdapter.notifyDataSetChanged();
            for (long userId : this.filterInformation.getUserIDs()) {
                binding.people.setSelection(userAdapter.getPosition(userId));
            }
            binding.people.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DeckLog.info("clicked position: " + position);
                    filterInformation.addUserId(userAdapter.getItemId(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Nothing to do
                }
            });
        });

        return dialogBuilder
                .setTitle(R.string.simple_filter)
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.simple_clear, (a, b) -> viewModel.postFilterInformation(null))
                .setPositiveButton(R.string.simple_filter, (a, b) -> {
                    filterInformation.clearLabelIds();
                    filterInformation.addAllLabelIds(labelAdapter.getSelected());
                    viewModel.postFilterInformation(filterInformation);
                })
                .create();
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardId) {
        final DialogFragment dialog = new FilterDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {

    }
}
