package it.niedermann.nextcloud.deck.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutLicenseTabBinding;

import static it.niedermann.nextcloud.deck.util.SpannableUtil.url;

public class AboutFragmentLicenseTab extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutLicenseTabBinding binding = FragmentAboutLicenseTabBinding.inflate(inflater, container, false);
        binding.aboutIconsDisclaimer.setText(
                getString(R.string.about_icons_disclaimer,
                        url(getString(R.string.about_app_icon_author_link_label), getString(R.string.url_about_icon_author)),
                        url(getString(R.string.about_icons_disclaimer_mdi), getString(R.string.url_about_icons_disclaimer_mdi))
                )
        );
        binding.aboutAppLicenseButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_license)))));
        return binding.getRoot();
    }
}