package it.niedermann.nextcloud.deck.ui.about;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutLicenseTabBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;
import it.niedermann.nextcloud.deck.util.ColorUtil;

import static it.niedermann.nextcloud.deck.util.SpannableUtil.setTextWithURL;

public class AboutFragmentLicenseTab extends BrandedFragment {

    private FragmentAboutLicenseTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutLicenseTabBinding.inflate(inflater, container, false);
        setTextWithURL(binding.aboutIconsDisclaimerAppIcon, getResources(), R.string.about_icons_disclaimer_app_icon, R.string.about_app_icon_author_link_label, R.string.url_about_icon_author);
        setTextWithURL(binding.aboutIconsDisclaimerMdiIcons, getResources(), R.string.about_icons_disclaimer_mdi_icons, R.string.about_icons_disclaimer_mdi, R.string.url_about_icons_disclaimer_mdi);
        binding.aboutAppLicenseButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_license)))));
        return binding.getRoot();
    }

    @Override
    public void applyBrand(int mainColor) {
        @ColorInt final int finalMainColor = BrandingUtil.getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor);
        DrawableCompat.setTintList(binding.aboutAppLicenseButton.getBackground(), ColorStateList.valueOf(finalMainColor));
        binding.aboutAppLicenseButton.setTextColor(ColorUtil.getForegroundColorForBackgroundColor(finalMainColor));
    }
}