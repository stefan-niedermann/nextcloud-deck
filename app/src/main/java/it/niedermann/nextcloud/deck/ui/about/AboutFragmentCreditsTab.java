package it.niedermann.nextcloud.deck.ui.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutCreditsTabBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.util.SpannableUtil.disabled;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.setTextWithURL;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.strong;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.url;

public class AboutFragmentCreditsTab extends Fragment {

    private static final int BACKGROUND_SYNC_NEVER_EXECUTED = -1;

    private FragmentAboutCreditsTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutCreditsTabBinding.inflate(inflater, container, false);

        // VERSIONS

        binding.aboutVersion.setText(getString(R.string.about_version, strong("v" + BuildConfig.VERSION_NAME)));
        SyncManager syncManager = new SyncManager(requireActivity());
        try {
            syncManager.getServerVersion(new IResponseCallback<Capabilities>(null) {
                @Override
                public void onResponse(Capabilities response) {
                    requireActivity().runOnUiThread(() -> binding.aboutServerAppVersion.setText(strong("v" + response.getDeckVersion().toString())));
                }
            });
        } catch (OfflineException e) {
            binding.aboutServerAppVersion.setText(disabled(getString(R.string.you_are_currently_offline), requireContext()));
        }

        String backgroundSyncOffValue = getString(R.string.pref_value_background_sync_off);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());
        String settingsBackgroundSync = sharedPreferences.getString(getString(R.string.pref_key_background_sync), backgroundSyncOffValue);
        long lastBackgroundSync = sharedPreferences.getLong(getString(R.string.shared_preference_last_background_sync), BACKGROUND_SYNC_NEVER_EXECUTED);

        // BACKGROUND SYNC

        binding.lastBackgroundSync.setText(
                lastBackgroundSync == BACKGROUND_SYNC_NEVER_EXECUTED || settingsBackgroundSync.equals(backgroundSyncOffValue)
                        ? disabled(getString(R.string.simple_disabled), requireContext())
                        : strong(DateUtil.getRelativeDateTimeString(getContext(), lastBackgroundSync))
        );
        binding.aboutMaintainer.setText(url(getString(R.string.about_maintainer), getString(R.string.url_maintainer)));
        binding.aboutMaintainer.setMovementMethod(new LinkMovementMethod());
        setTextWithURL(binding.aboutTranslators, getResources(), R.string.about_translators_transifex, R.string.about_translators_transifex_label, R.string.url_translations);
        return binding.getRoot();
    }
}