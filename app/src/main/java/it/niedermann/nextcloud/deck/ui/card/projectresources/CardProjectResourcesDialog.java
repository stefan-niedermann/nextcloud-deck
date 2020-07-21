package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.DialogProjectResourcesBinding;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

public class CardProjectResourcesDialog extends BrandedDialogFragment {

    private static final String KEY_PROJECT_LOCAL_ID = "projectLocalId";
    private SyncManager syncManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = requireArguments();
        final long projectLocalId = args.getLong(KEY_PROJECT_LOCAL_ID, -1L);
        if (projectLocalId < 0L) {
            throw new IllegalArgumentException("Provide at least " + KEY_PROJECT_LOCAL_ID + " which must be a value greater than 0.");
        }
        syncManager = new SyncManager(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final DialogProjectResourcesBinding binding = DialogProjectResourcesBinding.inflate(inflater, container, false);
        final EditCardViewModel viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // TODO check if necessary
        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardProjectResourcesDialog.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        final CardProjectResourceAdapter adapter = new CardProjectResourceAdapter(viewModel.getAccount());
        binding.getRoot().setAdapter(adapter);

//        syncManager.getResourcesForProjectLocalId().observe((resources) -> adapter.setResources(resources));
        return binding.getRoot();
    }

    @Override
    public void applyBrand(int mainColor) {

    }

    public static DialogFragment newInstance(@NonNull Long projectLocalId) {
        final DialogFragment fragment = new CardProjectResourcesDialog();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_PROJECT_LOCAL_ID, projectLocalId);
        fragment.setArguments(args);
        return fragment;
    }
}
