package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingWidgetConfigurationBinding;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.ui.theme.DeckViewThemeUtils;
import it.niedermann.nextcloud.deck.ui.widget.WidgetColorPickerDialogFragment;

public class UpcomingWidgetConfigurationActivity extends AppCompatActivity {

    private static final String RESULT_KEY_TITLE_COLOR = "titleColor";
    private static final String RESULT_KEY_WIDGET_BACKGROUND = "widgetBackground";
    private static final String RESULT_KEY_SECTION_TEXT = "sectionText";
    private static final String RESULT_KEY_LIST_BACKGROUND = "listBackground";
    private static final String RESULT_KEY_ENTRY_TEXT = "entryText";

    private int appWidgetId;
    private ActivityUpcomingWidgetConfigurationBinding binding;
    private UpcomingWidgetConfigurationViewModel viewModel;

    @Nullable
    private FilterWidget config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpcomingWidgetConfigurationBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(UpcomingWidgetConfigurationViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.configure_upcoming_widget);
        }

        setResult(RESULT_CANCELED);
        final Bundle args = getIntent().getExtras();

        if (args != null) {
            appWidgetId = args.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            DeckLog.error("INVALID_APPWIDGET_ID");
            finish();
            return;
        }

        setupColorRow(binding.titleColorSwatch, RESULT_KEY_TITLE_COLOR, false, FilterWidget::getTitleColor, FilterWidget::setTitleColor);
        setupColorRow(binding.widgetBackgroundSwatch, RESULT_KEY_WIDGET_BACKGROUND, true, FilterWidget::getWidgetBackgroundColor, FilterWidget::setWidgetBackgroundColor);
        setupColorRow(binding.sectionTextSwatch, RESULT_KEY_SECTION_TEXT, false, FilterWidget::getSectionTextColor, FilterWidget::setSectionTextColor);
        setupColorRow(binding.listBackgroundSwatch, RESULT_KEY_LIST_BACKGROUND, true, FilterWidget::getListBackgroundColor, FilterWidget::setListBackgroundColor);
        setupColorRow(binding.entryTextSwatch, RESULT_KEY_ENTRY_TEXT, false, FilterWidget::getEntryTextColor, FilterWidget::setEntryTextColor);

        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> submit());

        viewModel.getConfig().observe(this, this::applyConfig);
        viewModel.loadConfig(appWidgetId);
    }

    private void applyConfig(@Nullable FilterWidget config) {
        if (config == null) {
            return;
        }
        this.config = config;
        binding.titleEt.setText(config.getTitle());
        updateSwatch(binding.titleColorSwatch, config.getTitleColor());
        updateSwatch(binding.widgetBackgroundSwatch, config.getWidgetBackgroundColor());
        updateSwatch(binding.sectionTextSwatch, config.getSectionTextColor());
        updateSwatch(binding.listBackgroundSwatch, config.getListBackgroundColor());
        updateSwatch(binding.entryTextSwatch, config.getEntryTextColor());
    }

    private void setupColorRow(@NonNull ImageView swatch, @NonNull String resultKey, boolean allowTransparent, @NonNull ColorGetter getter, @NonNull ColorSetter setter) {
        getSupportFragmentManager().setFragmentResultListener(WidgetColorPickerDialogFragment.resultKeyFor(resultKey), this, (requestKey, bundle) -> {
            if (config == null) {
                return;
            }
            final Integer color = bundle.getBoolean(WidgetColorPickerDialogFragment.EXTRA_USE_DEFAULT)
                    ? null
                    : bundle.getInt(WidgetColorPickerDialogFragment.EXTRA_COLOR);
            setter.set(config, color);
            updateSwatch(swatch, color);
        });
        swatch.setOnClickListener((v) -> {
            if (config == null) {
                return;
            }
            WidgetColorPickerDialogFragment.newInstance(resultKey, getter.get(config), allowTransparent)
                    .show(getSupportFragmentManager(), resultKey);
        });
    }

    private void updateSwatch(@NonNull ImageView swatch, @Nullable @ColorInt Integer color) {
        if (color == null) {
            swatch.setImageDrawable(DeckViewThemeUtils.getTintedImageView(this, R.drawable.circle_36dp, ContextCompat.getColor(this, R.color.board_default_custom_color)));
        } else if (Color.alpha(color) == 0) {
            swatch.setImageDrawable(DeckViewThemeUtils.getTintedImageView(this, R.drawable.circle_alpha_colorize_36dp, ContextCompat.getColor(this, R.color.board_default_custom_color)));
        } else {
            swatch.setImageDrawable(DeckViewThemeUtils.getTintedImageView(this, R.drawable.circle_36dp, color));
        }
    }

    private void submit() {
        if (config == null) {
            return;
        }
        final String title = binding.titleEt.getText() == null ? null : binding.titleEt.getText().toString().trim();
        config.setTitle(TextUtils.isEmpty(title) ? null : title);

        viewModel.saveConfig(config, () -> {
            final Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), UpcomingWidget.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, updateIntent);
            getApplicationContext().sendBroadcast(updateIntent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    private interface ColorGetter {
        @Nullable
        @ColorInt
        Integer get(@NonNull FilterWidget config);
    }

    private interface ColorSetter {
        void set(@NonNull FilterWidget config, @Nullable @ColorInt Integer color);
    }
}
