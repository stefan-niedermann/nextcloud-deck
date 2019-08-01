package it.niedermann.nextcloud.deck.ui.about;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentCreditsTab extends Fragment {

    @BindView(R.id.about_version)
    TextView aboutVersion;
    @BindView(R.id.about_server_app_version)
    TextView aboutServerAppVersion;
    @BindView(R.id.about_maintainer)
    TextView aboutMaintainer;
    @BindView(R.id.about_translators)
    TextView aboutTranslators;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_credits_tab, container, false);
        ButterKnife.bind(this, v);
        LinkUtil.setHtml(aboutVersion, getString(R.string.about_version, getVersionStrongTag(getResources(), BuildConfig.VERSION_NAME)));
        SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
        syncManager.getServerVersion(new IResponseCallback<Capabilities>(null) {
            @Override
            public void onResponse(Capabilities response) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> LinkUtil.setHtml(aboutServerAppVersion, getVersionStrongTag(getResources(), response.getDeckVersion().toString())));
            }
        });
        LinkUtil.setHtml(aboutMaintainer, LinkUtil.concatenateResources(v.getResources(),
                R.string.anchor_start, R.string.url_maintainer, R.string.anchor_middle, R.string.about_maintainer, R.string.anchor_end));
        LinkUtil.setHtml(aboutTranslators,
                v.getResources().getString(R.string.about_translators_transifex, LinkUtil.concatenateResources(v.getResources(),
                        R.string.anchor_start, R.string.url_translations, R.string.anchor_middle, R.string.about_translators_transifex_label, R.string.anchor_end
                )));
        return v;
    }

    private static String getVersionStrongTag(Resources resources, String version) {
        return resources.getString(R.string.strong_start) +
                "v" +
                version +
                resources.getString(R.string.strong_end);
    }
}