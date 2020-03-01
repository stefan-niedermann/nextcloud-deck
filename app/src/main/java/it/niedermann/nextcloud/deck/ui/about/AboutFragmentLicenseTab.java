package it.niedermann.nextcloud.deck.ui.about;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutLicenseTabBinding;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentLicenseTab extends Fragment {

    FragmentAboutLicenseTabBinding binding;

    private String paragraphStart;
    private String paragraphEnd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutLicenseTabBinding.inflate(inflater, container, false);
        Resources resources = getResources();
        paragraphStart = getString(R.string.paragraph_start);
        paragraphEnd = getString(R.string.paragraph_end);
        binding.aboutAppLicenseButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_license)))));
        LinkUtil.setHtml(binding.aboutIconsDisclaimer, paragraphStart, getString(R.string.about_icons_disclaimer, getAppIconHint(resources), getMdiLink(resources)), paragraphEnd);
        return binding.getRoot();
    }

    private String getAppIconHint(Resources resources) {
        return LinkUtil.makeLink(resources, R.string.url_about_icon_author, R.string.about_app_icon_author_link_label) + paragraphEnd + paragraphStart;
    }

    private String getMdiLink(Resources resources) {
        return LinkUtil.makeLink(resources, R.string.url_about_icons_disclaimer_mdi, R.string.about_icons_disclaimer_mdi);
    }
}