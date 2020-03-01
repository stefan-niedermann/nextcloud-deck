package it.niedermann.nextcloud.deck.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutContributionTabBinding;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentContributingTab extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutContributionTabBinding binding = FragmentAboutContributionTabBinding.inflate(inflater, container, false);
        LinkUtil.setHtml(binding.aboutSource,
                getString(
                        R.string.about_source,
                        LinkUtil.makeLink(getResources(), R.string.url_source, R.string.url_source)
                ));
        LinkUtil.setHtml(binding.aboutIssues,
                getString(
                        R.string.about_issues,
                        LinkUtil.makeLink(getResources(), R.string.url_issues, R.string.url_issues)
                ));
        LinkUtil.setHtml(binding.aboutTranslate,
                getString(
                        R.string.about_translate,
                        LinkUtil.makeLink(getResources(), R.string.url_translations, R.string.url_translations)
                ));
        return binding.getRoot();
    }
}