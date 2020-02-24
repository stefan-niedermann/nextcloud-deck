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

    private static final int BACKGROUND_SYNC_NEVER_EXECUTED = -1;

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
    @BindString(R.string.pref_key_background_sync)
    String sharedPreferencesBackgroundSync;
    @BindString(R.string.pref_value_background_sync_off)
    String backgroundSyncOffValue;
    @BindString(R.string.you_are_currently_offline)
    String offlineText;
    @BindString(R.string.simple_disabled)
    String disabledText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_credits_tab, container, false);
        ButterKnife.bind(this, v);

        // VERSIONS

        LinkUtil.setHtml(aboutVersion, getString(R.string.about_version, strong("v" + BuildConfig.VERSION_NAME)));
        SyncManager syncManager = new SyncManager(requireActivity());
        try {
            syncManager.getServerVersion(new IResponseCallback<Capabilities>(null) {
                @Override
                public void onResponse(Capabilities response) {
                    requireActivity().runOnUiThread(() -> LinkUtil.setHtml(aboutServerAppVersion, strong("v" + response.getDeckVersion().toString())));
                }
            });
        } catch (OfflineException e) {
            aboutServerAppVersion.setText(disabled(offlineText));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());
        String settingsBackgroundSync = sharedPreferences.getString(sharedPreferencesBackgroundSync, backgroundSyncOffValue);
        long lastBackgroundSync = sharedPreferences.getLong(sharedPreferencesLastBackgroundSync, BACKGROUND_SYNC_NEVER_EXECUTED);

        // BACKGROUND SYNC

        lastBackgroundSyncExecutionTime.setText(
                lastBackgroundSync == BACKGROUND_SYNC_NEVER_EXECUTED || settingsBackgroundSync.equals(backgroundSyncOffValue)
                        ? disabled(disabledText)
                        : strong(DateUtil.getRelativeDateTimeString(getContext(), lastBackgroundSync))
        );
        LinkUtil.setHtml(aboutMaintainer, LinkUtil.concatenateResources(v.getResources(),
                R.string.anchor_start, R.string.url_maintainer, R.string.anchor_middle, R.string.about_maintainer, R.string.anchor_end));
        LinkUtil.setHtml(aboutTranslators,
                v.getResources().getString(R.string.about_translators_transifex, LinkUtil.concatenateResources(v.getResources(),
                        R.string.anchor_start, R.string.url_translations, R.string.anchor_middle, R.string.about_translators_transifex_label, R.string.anchor_end
                )));
        return v;
    }

    private SpannableString strong(CharSequence text) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, span.length(), 0);
        return span;
    }

    private SpannableString disabled(CharSequence text) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.ITALIC), 0, span.length(), 0);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.fg_secondary)), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}