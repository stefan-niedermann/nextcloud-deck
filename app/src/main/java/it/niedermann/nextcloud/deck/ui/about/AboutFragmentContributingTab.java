package it.niedermann.nextcloud.deck.ui.about;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentContributingTab extends Fragment {

    @BindView(R.id.about_source)
    TextView aboutSource;
    @BindView(R.id.about_issues)
    TextView aboutIssues;
    @BindView(R.id.about_translate)
    TextView aboutTranslate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_contribution_tab, container, false);
        ButterKnife.bind(this, v);
        LinkUtil.setHtml(aboutSource, R.string.about_source, getString(R.string.url_source));
        LinkUtil.setHtml(aboutIssues, R.string.about_issues, getString(R.string.url_issues));
        LinkUtil.setHtml(aboutTranslate, R.string.about_translate, getString(R.string.url_translations));
        return v;
    }
}