package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogProjectResourcesBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

public class CardProjectResourcesDialog extends DialogFragment {

    private static final String KEY_RESOURCES = "resources";
    private static final String KEY_PROJECT_NAME = "projectName";
    private DialogProjectResourcesBinding binding;
    private EditCardViewModel viewModel;

    private String projectName;
    @NonNull
    private final List<OcsProjectResource> resources = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final var args = requireArguments();
        if (!args.containsKey(KEY_RESOURCES)) {
            throw new IllegalArgumentException("Provide at least " + KEY_RESOURCES);
        }
        //noinspection unchecked
        this.resources.addAll((ArrayList<OcsProjectResource>) Objects.requireNonNull(args.getSerializable(KEY_RESOURCES)));
        this.projectName = args.getString(KEY_PROJECT_NAME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogProjectResourcesBinding.inflate(LayoutInflater.from(requireContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(projectName)
                .setView(binding.getRoot())
                .setNeutralButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final var adapter = new CardProjectResourceAdapter(viewModel, resources, requireActivity());
        binding.getRoot().setAdapter(adapter);
        super.onActivityCreated(savedInstanceState);
    }

    public static DialogFragment newInstance(@Nullable String projectName, @NonNull List<OcsProjectResource> resources) {
        final var fragment = new CardProjectResourcesDialog();
        final var args = new Bundle();
        args.putString(KEY_PROJECT_NAME, projectName);
        args.putSerializable(KEY_RESOURCES, new ArrayList<>(resources));
        fragment.setArguments(args);
        return fragment;
    }
}
