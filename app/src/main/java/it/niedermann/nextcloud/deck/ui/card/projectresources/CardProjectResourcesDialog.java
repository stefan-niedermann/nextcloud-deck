package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.DialogProjectResourcesBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

public class CardProjectResourcesDialog extends BrandedDialogFragment {

    private static final String KEY_RESOURCES = "resources";

    private CardProjectResourceAdapter adapter;
    private SyncManager syncManager;
    private DialogProjectResourcesBinding binding;
    private EditCardViewModel viewModel;
    @NonNull
    private List<OcsProjectResource> resources = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = requireArguments();
        if (!args.containsKey(KEY_RESOURCES)) {
            throw new IllegalArgumentException("Provide at least " + KEY_RESOURCES);
        }
        this.resources.addAll((ArrayList<OcsProjectResource>) args.getSerializable(KEY_RESOURCES));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogProjectResourcesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // TODO check if necessary
        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardProjectResourcesDialog.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        syncManager = new SyncManager(requireContext());

        // TODO parameter needed
        adapter = new CardProjectResourceAdapter(viewModel.getAccount(), resources);
        binding.getRoot().setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void applyBrand(int mainColor) {

    }

    public DialogFragment newInstance(@NonNull ArrayList<OcsProjectResource> resources) {
        final DialogFragment fragment = new CardProjectResourcesDialog();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_RESOURCES, resources);
        fragment.setArguments(args);
        return fragment;
    }
}
