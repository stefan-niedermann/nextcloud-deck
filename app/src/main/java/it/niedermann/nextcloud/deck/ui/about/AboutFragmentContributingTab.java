package it.niedermann.nextcloud.deck.ui.about;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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
        Resources resources = v.getResources();
        ButterKnife.bind(this, v);
        LinkUtil.setHtmlFromStringResources(aboutSource,
                resources.getString(R.string.about_source, LinkUtil.concatenateResources(resources,
                        R.string.anchor_start, R.string.url_source, R.string.anchor_middle, R.string.url_source, R.string.anchor_end
                )));
        LinkUtil.setHtmlFromStringResources(aboutIssues,
                resources.getString(R.string.about_issues, LinkUtil.concatenateResources(resources,
                        R.string.anchor_start, R.string.url_issues, R.string.anchor_middle, R.string.url_issues, R.string.anchor_end
                )));
        LinkUtil.setHtmlFromStringResources(aboutTranslate,
                resources.getString(R.string.about_translate, LinkUtil.concatenateResources(resources,
                        R.string.anchor_start, R.string.url_translations, R.string.anchor_middle, R.string.url_translations, R.string.anchor_end
                )));
        return v;
    }
}