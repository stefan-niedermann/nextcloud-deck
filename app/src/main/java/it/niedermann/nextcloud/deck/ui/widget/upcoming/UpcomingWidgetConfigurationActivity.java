package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingWidgetBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class UpcomingWidgetConfigurationActivity extends AppCompatActivity {
    private int appWidgetId;
    private ActivityUpcomingWidgetBinding binding;
    private UpcomingWidgetViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityUpcomingWidgetBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(UpcomingWidgetViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.widget_upcoming_title);
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

            viewModel.addUpcomingWidget(appWidgetId, new IResponseCallback<Integer>(null) {
                @Override
                public void onResponse(Integer response) {
                }
            });
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), UpcomingWidget.class);
            extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // The `extras` bundle is added to the intent this way because using putExtras(extras)
            // would have the OS attempt to reassemble the data and cause a crash
            // when it finds classes that are only known to this application.
            updateIntent.putExtra(UpcomingWidget.BUNDLE_KEY, extras);
            setResult(RESULT_OK, updateIntent);
            getApplicationContext().sendBroadcast(updateIntent);

            finish();
        });
    }
}
