package it.niedermann.nextcloud.deck.ui.card.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabProjectsBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.details.CardProjectsAdapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CardProjectsFragment extends BrandedFragment {

    private FragmentCardEditTabProjectsBinding binding;
    private EditCardViewModel mainViewModel;

    public static Fragment newInstance() {
        return new CardProjectsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabProjectsBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (mainViewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardProjectsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            if (requireActivity() instanceof EditActivity) {
                Toast.makeText(getContext(), R.string.error_edit_activity_killed_by_android, Toast.LENGTH_LONG).show();
                ((EditActivity) requireActivity()).directFinish();
            } else {
                requireActivity().finish();
            }
            return binding.getRoot();
        }

        if (mainViewModel.getFullCard().getProjects().size() > 0) {
            binding.projects.setNestedScrollingEnabled(false);
            final CardProjectsAdapter adapter = new CardProjectsAdapter(mainViewModel.getFullCard().getProjects(), getChildFragmentManager());
            binding.projects.setAdapter(adapter);
            binding.projects.setVisibility(VISIBLE);
            binding.emptyContentView.setVisibility(GONE);
        } else {
            binding.projects.setVisibility(GONE);
            binding.emptyContentView.setVisibility(VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void applyBrand(int mainColor) {
        BrandingUtil.applyBrandToFAB(mainColor, binding.fab);
    }
}
