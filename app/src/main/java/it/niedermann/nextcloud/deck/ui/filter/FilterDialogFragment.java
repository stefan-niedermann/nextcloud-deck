package it.niedermann.nextcloud.deck.ui.filter;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class FilterDialogFragment extends BrandedDialogFragment {

    private DialogFilterBinding binding;
    private FilterViewModel filterViewModel;
    private Drawable indicator;

    private final static int[] tabTitles = new int[]{
            R.string.filter_tags_title,
            R.string.filter_user_title,
            R.string.filter_duedate_title
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final var context = requireContext();

        indicator = ContextCompat.getDrawable(context, R.drawable.circle_grey600_8dp);
        assert indicator != null;
        indicator.setColorFilter(ContextCompat.getColor(context, R.color.defaultBrand), PorterDuff.Mode.SRC_ATOP);

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        final var dialogBuilder = new MaterialAlertDialogBuilder(context);

        binding = DialogFilterBinding.inflate(requireActivity().getLayoutInflater());
        binding.viewPager.setAdapter(new TabsPagerAdapter(this));
        binding.viewPager.setOffscreenPageLimit(tabTitles.length);

        final var filterInformationDraft = filterViewModel.getFilterInformationDraft();
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            filterInformationDraft.observe(this, (draft) -> {
                switch (position) {
                    case 0:
                        tab.setIcon(draft.getLabels().size() > 0 || draft.isNoAssignedLabel() ? indicator : null);
                        break;
                    case 1:
                        tab.setIcon(draft.getUsers().size() > 0 || draft.isNoAssignedUser() ? indicator : null);
                        break;
                    case 2:
                        tab.setIcon(draft.getDueType() != EDueType.NO_FILTER ? indicator : null);
                        break;
                    default:
                        throw new IllegalStateException("position must be between 0 and 2");
                }
            });
            tab.setText(tabTitles[position]);
        }).attach();

        binding.viewPager.post(() -> {
            binding.viewPager.setCurrentItem(filterViewModel.getCurrentFilterTab(), false);
            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    filterViewModel.setCurrentFilterTab(position);
                }
            });
        });
        filterViewModel.createFilterInformationDraft();

        return dialogBuilder
                .setTitle(R.string.simple_filter)
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.simple_clear, (a, b) -> filterViewModel.clearFilterInformation(false))
                .setPositiveButton(R.string.simple_filter, (a, b) -> filterViewModel.publishFilterInformationDraft())
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static DialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void applyBrand(int mainColor) {
        @ColorInt final int finalMainColor = getSecondaryForegroundColorDependingOnTheme(binding.tabLayout.getContext(), mainColor);
        final boolean contrastRatioIsSufficient = ColorUtil.INSTANCE.getContrastRatio(mainColor, ContextCompat.getColor(binding.tabLayout.getContext(), R.color.primary)) > 1.7d;
        binding.tabLayout.setSelectedTabIndicatorColor(contrastRatioIsSufficient ? mainColor : finalMainColor);
        indicator.setColorFilter(contrastRatioIsSufficient ? mainColor : finalMainColor, PorterDuff.Mode.SRC_ATOP);
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        TabsPagerAdapter(final Fragment f) {
            super(f);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FilterLabelsFragment();
                case 1:
                    return new FilterUserFragment();
                case 2:
                    return new FilterDueTypeFragment();
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
