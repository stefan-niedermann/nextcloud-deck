package it.niedermann.nextcloud.deck.ui.filter;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class FilterDialogFragment extends BrandedDialogFragment {

    private DialogFilterBinding binding;
    private MainViewModel viewModel;
    private LabelFilterAdapter labelAdapter;
    private UserFilterAdapter userAdapter;
    private OverdueFilterAdapter overdueAdapter;
    private FilterInformation filterInformation;

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
        final SyncManager syncManager = new SyncManager(requireActivity());

        binding = DialogFilterBinding.inflate(requireActivity().getLayoutInflater());

        overdueAdapter = new OverdueFilterAdapter(requireContext());
        binding.overdue.setAdapter(overdueAdapter);
        binding.overdue.setSelection(overdueAdapter.getPosition(this.filterInformation.getDueType()));
        binding.overdue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInformation.setDueType(overdueAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterInformation.setDueType(EDueType.NO_FILTER);
            }
        });

        observeOnce(syncManager.findProposalsForLabelsToAssign(viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId()), requireActivity(), (labels) -> {
            labelAdapter = new LabelFilterAdapter(labels, this.filterInformation.getLabels());
            binding.labels.setNestedScrollingEnabled(false);
            binding.labels.setAdapter(labelAdapter);
        });

        observeOnce(syncManager.findProposalsForUsersToAssign(viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId()), requireActivity(), (users) -> {
            userAdapter = new UserFilterAdapter(dpToPx(requireContext(), R.dimen.avatar_size), viewModel.getCurrentAccount(), users, this.filterInformation.getUsers());
            binding.users.setNestedScrollingEnabled(false);
            binding.users.setAdapter(userAdapter);
        });

        return dialogBuilder
                .setTitle(R.string.simple_filter)
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.simple_clear, (a, b) -> viewModel.postFilterInformation(null))
                .setPositiveButton(R.string.simple_filter, (a, b) -> {
                    filterInformation.clearLabels();
                    filterInformation.addAllLabels(labelAdapter.getSelected());

                    filterInformation.clearUsers();
                    filterInformation.addAllUsers(userAdapter.getSelected());

                    viewModel.postFilterInformation(hasActiveFilter(filterInformation) ? filterInformation : null);
                })
                .create();
    }

    public static DialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {

    }

    /**
     * @return whether or not the given filterInformation has any actual filters set
     */
    private static boolean hasActiveFilter(@Nullable FilterInformation filterInformation) {
        if (filterInformation == null) {
            return false;
        }
        return filterInformation.getDueType() != EDueType.NO_FILTER || filterInformation.getUsers().size() > 0 || filterInformation.getLabels().size() > 0;
    }
}
