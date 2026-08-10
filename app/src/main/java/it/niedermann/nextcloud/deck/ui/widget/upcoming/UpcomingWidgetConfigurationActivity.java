package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingWidgetConfigurationBinding;
import it.niedermann.nextcloud.deck.databinding.RowWidgetAppearanceColorBinding;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.repository.BaseRepository;
import it.niedermann.nextcloud.deck.ui.theme.DeckViewThemeUtils;

/**
 * Lets the user customize the appearance of an {@link UpcomingWidget} instance: title,
 * title/background/section-header/list/entry colors.
 *
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/1792">#1792</a>
 */
public class UpcomingWidgetConfigurationActivity extends AppCompatActivity {

    private int appWidgetId;
    private ActivityUpcomingWidgetConfigurationBinding binding;
    private BaseRepository baseRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    private Integer titleColor;
    @Nullable
    private Integer backgroundColor;
    @Nullable
    private Integer headerTextColor;
    @Nullable
    private Integer listBackgroundColor;
    @Nullable
    private Integer entryTextColor;

    private List<ColorOption> colorOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpcomingWidgetConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        setResult(RESULT_CANCELED);
        final Bundle extras = getIntent().getExtras();
        appWidgetId = extras == null
                ? AppWidgetManager.INVALID_APPWIDGET_ID
                : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            DeckLog.error("INVALID_APPWIDGET_ID");
            finish();
            return;
        }

        baseRepository = new BaseRepository(this);

        colorOptions = List.of(
                new ColorOption("title_color", binding.titleColorRow, R.string.widget_appearance_title_color,
                        ContextCompat.getColor(this, R.color.widget_foreground),
                        () -> titleColor, color -> titleColor = color),
                new ColorOption("background_color", binding.backgroundColorRow, R.string.widget_appearance_background_color,
                        ContextCompat.getColor(this, R.color.widget_outer_background),
                        () -> backgroundColor, color -> backgroundColor = color),
                new ColorOption("header_text_color", binding.headerTextColorRow, R.string.widget_appearance_header_text_color,
                        ContextCompat.getColor(this, R.color.widget_foreground),
                        () -> headerTextColor, color -> headerTextColor = color),
                new ColorOption("list_background_color", binding.listBackgroundColorRow, R.string.widget_appearance_list_background_color,
                        Color.TRANSPARENT,
                        () -> listBackgroundColor, color -> listBackgroundColor = color),
                new ColorOption("entry_text_color", binding.entryTextColorRow, R.string.widget_appearance_entry_text_color,
                        ContextCompat.getColor(this, R.color.widget_foreground),
                        () -> entryTextColor, color -> entryTextColor = color)
        );

        for (final var option : colorOptions) {
            bindColorRow(option);
        }

        binding.cancel.setOnClickListener(v -> finish());
        binding.submit.setOnClickListener(v -> save());

        loadExistingConfig();
    }

    private void bindColorRow(@NonNull ColorOption option) {
        option.row.label.setText(option.labelRes);
        updateSwatch(option.row.swatch, option.currentOrDefault());
        option.row.getRoot().setOnClickListener(v -> {
            getSupportFragmentManager().setFragmentResultListener(option.requestKey, this, (key, bundle) -> {
                if (bundle.getBoolean(WidgetColorPickerDialogFragment.BUNDLE_KEY_USE_DEFAULT, false)) {
                    option.setter.accept(null);
                } else if (bundle.containsKey(WidgetColorPickerDialogFragment.BUNDLE_KEY_COLOR)) {
                    option.setter.accept(bundle.getInt(WidgetColorPickerDialogFragment.BUNDLE_KEY_COLOR));
                }
                updateSwatch(option.row.swatch, option.currentOrDefault());
            });
            WidgetColorPickerDialogFragment.newInstance(option.requestKey, option.currentOrDefault())
                    .show(getSupportFragmentManager(), option.requestKey);
        });
    }

    private void refreshSwatches() {
        for (final var option : colorOptions) {
            updateSwatch(option.row.swatch, option.currentOrDefault());
        }
    }

    private void updateSwatch(@NonNull ImageView swatch, @ColorInt int color) {
        // circle_36dp is a plain filled circle, i.e. an actual color preview. Don't swap this for
        // circle_alpha_check_36dp: that drawable is ColorChooser's own "currently selected"
        // checkmark overlay, not a general-purpose swatch, and tinting it fully hides the check.
        swatch.setImageDrawable(DeckViewThemeUtils.getTintedImageView(this, R.drawable.circle_36dp, color));
    }

    private void loadExistingConfig() {
        executor.execute(() -> {
            final FilterWidget existing = baseRepository.getFilterWidgetDirectly(appWidgetId);
            if (existing == null) {
                return;
            }
            runOnUiThread(() -> {
                binding.titleEt.setText(existing.getTitle());
                titleColor = existing.getTitleColor();
                backgroundColor = existing.getBackgroundColor();
                headerTextColor = existing.getHeaderTextColor();
                listBackgroundColor = existing.getListBackgroundColor();
                entryTextColor = existing.getEntryTextColor();
                refreshSwatches();
            });
        });
    }

    private void save() {
        final String title = binding.titleEt.getText() == null ? null : binding.titleEt.getText().toString().trim();
        binding.submit.setEnabled(false);

        executor.execute(() -> {
            FilterWidget config = baseRepository.getFilterWidgetDirectly(appWidgetId);
            final boolean isNew = config == null;
            if (isNew) {
                config = UpcomingWidget.buildDefaultConfig(baseRepository, appWidgetId);
            }

            config.setTitle(title == null || title.isEmpty() ? null : title);
            config.setTitleColor(titleColor);
            config.setBackgroundColor(backgroundColor);
            config.setHeaderTextColor(headerTextColor);
            config.setListBackgroundColor(listBackgroundColor);
            config.setEntryTextColor(entryTextColor);

            if (isNew) {
                baseRepository.createFilterWidget(config, (response, headers) -> runOnUiThread(this::finishSuccessfully));
            } else {
                baseRepository.updateFilterWidget(config, (response, headers) -> runOnUiThread(this::finishSuccessfully));
            }
        });
    }

    private void finishSuccessfully() {
        final Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), UpcomingWidget.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private static final class ColorOption {
        @NonNull
        private final String requestKey;
        @NonNull
        private final RowWidgetAppearanceColorBinding row;
        @StringRes
        private final int labelRes;
        @ColorInt
        private final int defaultColor;
        @NonNull
        private final Supplier<Integer> getter;
        @NonNull
        private final Consumer<Integer> setter;

        private ColorOption(@NonNull String requestKey, @NonNull RowWidgetAppearanceColorBinding row, @StringRes int labelRes,
                             @ColorInt int defaultColor, @NonNull Supplier<Integer> getter, @NonNull Consumer<Integer> setter) {
            this.requestKey = requestKey;
            this.row = row;
            this.labelRes = labelRes;
            this.defaultColor = defaultColor;
            this.getter = getter;
            this.setter = setter;
        }

        @ColorInt
        private int currentOrDefault() {
            final Integer current = getter.get();
            return current == null ? defaultColor : current;
        }
    }
}
