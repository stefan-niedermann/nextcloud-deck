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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutCreditsTabBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentCreditsTab extends Fragment {

    private static final int BACKGROUND_SYNC_NEVER_EXECUTED = -1;

    private FragmentAboutCreditsTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutCreditsTabBinding.inflate(inflater, container, false);

        // VERSIONS

        LinkUtil.setHtml(binding.aboutVersion, getString(R.string.about_version, strong("v" + BuildConfig.VERSION_NAME)));
        SyncManager syncManager = new SyncManager(requireActivity());
        try {
            syncManager.getServerVersion(new IResponseCallback<Capabilities>(null) {
                @Override
                public void onResponse(Capabilities response) {
                    requireActivity().runOnUiThread(() -> LinkUtil.setHtml(binding.aboutServerAppVersion, strong("v" + response.getDeckVersion().toString())));
                }
            });
        } catch (OfflineException e) {
            binding.aboutServerAppVersion.setText(disabled(getString(R.string.you_are_currently_offline)));
        }

        String backgroundSyncOffValue = getString(R.string.pref_value_background_sync_off);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());
        String settingsBackgroundSync = sharedPreferences.getString(getString(R.string.pref_key_background_sync), backgroundSyncOffValue);
        long lastBackgroundSync = sharedPreferences.getLong(getString(R.string.shared_preference_last_background_sync), BACKGROUND_SYNC_NEVER_EXECUTED);

        // BACKGROUND SYNC

        binding.lastBackgroundSync.setText(
                lastBackgroundSync == BACKGROUND_SYNC_NEVER_EXECUTED || settingsBackgroundSync.equals(backgroundSyncOffValue)
                        ? disabled(getString(R.string.simple_disabled))
                        : strong(DateUtil.getRelativeDateTimeString(getContext(), lastBackgroundSync))
        );
        LinkUtil.setHtml(binding.aboutMaintainer, LinkUtil.concatenateResources(getResources(),
                R.string.anchor_start, R.string.url_maintainer, R.string.anchor_middle, R.string.about_maintainer, R.string.anchor_end));
        LinkUtil.setHtml(binding.aboutTranslators,
                getString(R.string.about_translators_transifex, LinkUtil.concatenateResources(getResources(),
                        R.string.anchor_start, R.string.url_translations, R.string.anchor_middle, R.string.about_translators_transifex_label, R.string.anchor_end
                )));
        return binding.getRoot();
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