package it.niedermann.nextcloud.deck.ui.about;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.LinkUtil;

public class AboutFragmentLicenseTab extends Fragment {

    @BindView(R.id.about_icons_disclaimer)
    TextView iconsDisclaimer;
    @BindView(R.id.about_app_license_button)
    Button appLicenseButton;

    @OnClick(R.id.about_app_license_button)
    void openLicense() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_license))));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_license_tab, container, false);
        ButterKnife.bind(this, v);
        Resources resources = getResources();
        LinkUtil.setHtmlFromStringResources(iconsDisclaimer,
                resources.getString(R.string.paragraph_start),
                resources.getString(R.string.about_icons_disclaimer, getAppIconHint(resources), getMdiLink(resources)),
                resources.getString(R.string.paragraph_end)
        );
        return v;
    }

    private String getAppIconHint(Resources resources) {
        return LinkUtil.makeLink(resources, R.string.url_about_icon_author, R.string.about_app_icon_author_link_label) +
                resources.getString(R.string.paragraph_end) +
                resources.getString(R.string.paragraph_start);
    }

    private String getMdiLink(Resources resources) {
        return LinkUtil.makeLink(resources, R.string.url_about_icons_disclaimer_mdi, R.string.about_icons_disclaimer_mdi);
    }
}