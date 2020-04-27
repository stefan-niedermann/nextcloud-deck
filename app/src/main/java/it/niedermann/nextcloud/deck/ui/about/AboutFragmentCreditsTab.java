package it.niedermann.nextcloud.deck.ui.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentAboutCreditsTabBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.util.SpannableUtil.disabled;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.setTextWithURL;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.strong;
import static it.niedermann.nextcloud.deck.util.SpannableUtil.url;

public class AboutFragmentCreditsTab extends Fragment {

    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final int BACKGROUND_SYNC_NEVER_EXECUTED = -1;

    private FragmentAboutCreditsTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutCreditsTabBinding.inflate(inflater, container, false);

        // VERSIONS

        binding.aboutVersion.setText(getString(R.string.about_version, strong(BuildConfig.VERSION_NAME)));
        final Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_KEY_ACCOUNT)) {
            final Account account = (Account) requireArguments().getSerializable(BUNDLE_KEY_ACCOUNT);
            requireActivity().runOnUiThread(() -> binding.aboutServerAppVersion.setText(strong(account == null ? getString(R.string.simple_error) : account.getServerDeckVersion())));
        } else {
            binding.aboutServerAppVersionContainer.setVisibility(View.GONE);
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

    public static Fragment newInstance() {
        return new AboutFragmentCreditsTab();
    }

    public static Fragment newInstance(@Nullable Account account) {
        if (account == null) {
            return newInstance();
        }
        final Fragment fragment = new AboutFragmentCreditsTab();
        final Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }
}