package it.niedermann.nextcloud.deck.ui.filter;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterBinding;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class FilterDialogFragment extends BrandedDialogFragment {

    private DialogFilterBinding binding;
    private MainViewModel mainViewModel;

    private final static int[] tabTitles = new int[]{
            R.string.filter_tags_title,
            R.string.filter_assignees_title,
            R.string.filter_duedate_title
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        binding = DialogFilterBinding.inflate(requireActivity().getLayoutInflater());
        binding.viewPager.setAdapter(new TabsPagerAdapter(getChildFragmentManager(), getLifecycle()));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();

        return dialogBuilder
                .setTitle(R.string.simple_filter)
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.simple_clear, (a, b) -> mainViewModel.postFilterInformation(null))
                .setPositiveButton(R.string.simple_filter, null)
                .create();
    }

    public static DialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        binding.tabLayout.setSelectedTabIndicatorColor(BrandedActivity.getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor));
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        TabsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FilterLabelsFragment();
                case 1:
                    return new FilterAssigneesFragment();
                case 2:
                    return new FilterDuedateFragment();
                default:
                    throw new IllegalArgumentException("position must be between 0 and 2");
            }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}
