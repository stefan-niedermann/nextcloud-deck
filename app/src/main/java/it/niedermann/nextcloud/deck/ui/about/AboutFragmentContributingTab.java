package it.niedermann.nextcloud.deck.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutContributionTabBinding;

public class AboutFragmentContributingTab extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutContributionTabBinding binding = FragmentAboutContributionTabBinding.inflate(inflater, container, false);
        binding.aboutSource.setText(getString(R.string.about_source, getString(R.string.url_source)));
        binding.aboutIssues.setText(getString(R.string.about_issues, getString(R.string.url_issues)));
        binding.aboutTranslate.setText(getString(R.string.about_translate, getString(R.string.url_translations)));
        return binding.getRoot();
    }
}