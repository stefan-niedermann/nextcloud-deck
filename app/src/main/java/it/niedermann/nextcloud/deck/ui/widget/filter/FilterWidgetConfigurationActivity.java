package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityFilterWidgetBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class FilterWidgetConfigurationActivity extends AppCompatActivity {
    private int appWidgetId;
    private ActivityFilterWidgetBinding binding;
    private FilterWidgetViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityFilterWidgetBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(FilterWidgetViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_filter_widget);
        }

        setResult(RESULT_CANCELED);
        final Bundle args = getIntent().getExtras();

        if (args != null) {
            appWidgetId = args.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            DeckLog.error("INVALID_APPWIDGET_ID");
            finish();
        }
        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> {
            final Bundle extras = new Bundle();

            viewModel.updateFilterWidget(response -> DeckLog.verbose("Successfully updated", FilterWidget.class.getSimpleName(), "with id", appWidgetId));
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), FilterWidget.class);
            extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // The `extras` bundle is added to the intent this way because using putExtras(extras)
            // would have the OS attempt to reassemle the data and cause a crash
            // when it finds classes that are only known to this application.
            updateIntent.putExtra(FilterWidget.BUNDLE_KEY, extras);
            setResult(RESULT_OK, updateIntent);
            getApplicationContext().sendBroadcast(updateIntent);

            finish();
        });
    }
}
