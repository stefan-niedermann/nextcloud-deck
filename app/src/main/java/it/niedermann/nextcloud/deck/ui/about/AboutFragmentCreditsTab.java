package it.niedermann.nextcloud.deck.ui.about;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.DateUtil;
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
    @BindView(R.id.last_background_sync)
    TextView lastBackgroundSyncExecutionTime;

    @BindString(R.string.shared_preference_last_background_sync)
    String sharedPreferencesLastBackgroundSync;
    @BindString(R.string.you_are_currently_offline)
    String offlineText;
    @BindString(R.string.strong_start)
    String strongStart;
    @BindString(R.string.strong_end)
    String strongEnd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_credits_tab, container, false);
        ButterKnife.bind(this, v);
        LinkUtil.setHtml(aboutVersion, getString(R.string.about_version, getVersionStrongTag(BuildConfig.VERSION_NAME)));
        SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
        try {
            syncManager.getServerVersion(new IResponseCallback<Capabilities>(null) {
                @Override
                public void onResponse(Capabilities response) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> LinkUtil.setHtml(aboutServerAppVersion, getVersionStrongTag(response.getDeckVersion().toString())));
                }
            });
        } catch (OfflineException e) {
            Spannable offlineTextSpannable = new SpannableString(offlineText);
            offlineTextSpannable.setSpan(new StyleSpan(Typeface.ITALIC), 0, offlineTextSpannable.length(), 0);
            offlineTextSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.fg_secondary)), 0, offlineTextSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            aboutServerAppVersion.setText(offlineTextSpannable);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()).getApplicationContext());
        long lastBackgroundSync = sharedPreferences.getLong(sharedPreferencesLastBackgroundSync, 0);
        LinkUtil.setHtml(lastBackgroundSyncExecutionTime, getLastBackgroundSyncStrongTag(lastBackgroundSync));
        LinkUtil.setHtml(aboutMaintainer, LinkUtil.concatenateResources(v.getResources(),
                R.string.anchor_start, R.string.url_maintainer, R.string.anchor_middle, R.string.about_maintainer, R.string.anchor_end));
        LinkUtil.setHtml(aboutTranslators,
                v.getResources().getString(R.string.about_translators_transifex, LinkUtil.concatenateResources(v.getResources(),
                        R.string.anchor_start, R.string.url_translations, R.string.anchor_middle, R.string.about_translators_transifex_label, R.string.anchor_end
                )));
        return v;
    }

    private String getVersionStrongTag(String version) {
        return strongStart + "v" + version + strongEnd;
    }

    private String getLastBackgroundSyncStrongTag(long lastBackgroundSync) {
        return strongStart + DateUtil.getRelativeDateTimeString(getContext(), lastBackgroundSync) + strongEnd;
    }
}