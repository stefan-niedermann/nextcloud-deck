package it.niedermann.nextcloud.deck.ui.card.details;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ViewCardStartDateBinding;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.ui.theme.ThemedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.theme.ThemedTimePickerDialog;

public class CardStartDateView extends FrameLayout implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Themed {

    private final ViewCardStartDateBinding binding;
    @Nullable
    private StartDateChangedListener startDateChangedListener;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d. MMM yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    @Nullable
    private Instant startDate = null;
    @Nullable
    private Instant done = null;
    @Nullable
    @ColorInt
    private Integer color = null;
    private FragmentManager fragmentManager = null;

    public CardStartDateView(Context context) {
        super(context);
        binding = ViewCardStartDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public CardStartDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = ViewCardStartDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public CardStartDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ViewCardStartDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    /**
     * @noinspection unused
     */
    public CardStartDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        binding = ViewCardStartDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            super.setEnabled(enabled);
            render();
        }
    }

    public void setStartDate(@NonNull FragmentManager fragmentManager, @NonNull Version version, @Nullable Instant startDate, @Nullable Instant done) {
        this.fragmentManager = fragmentManager;
        this.startDate = startDate;
        this.done = done;
        render();
    }

    private void render() {
        setVisibilityState();
        setTextState();
        setInteraction();
    }

    private void setVisibilityState() {
        if (done == null) {
            Stream.of(
                    binding.startDateDateWrapper,
                    binding.startDateTimeWrapper
            ).forEach(v -> v.setVisibility(View.VISIBLE));

            Stream.of(
                    binding.doneCheck,
                    binding.doneStartDate,
                    binding.doneDate,
                    binding.clearStartDate
            ).forEach(v -> v.setVisibility(View.GONE));

            binding.clearStartDate.setVisibility(startDate == null || !isEnabled() ? View.GONE : View.VISIBLE);
        } else {

            Stream.of(
                    binding.doneCheck,
                    binding.doneDate
            ).forEach(v -> v.setVisibility(View.VISIBLE));

            Stream.of(
                    binding.startDateDateWrapper,
                    binding.startDateTimeWrapper,
                    binding.clearStartDate
            ).forEach(v -> v.setVisibility(View.GONE));
        }
    }

    private void setTextState() {
        if (done == null) {
            binding.doneDate.setText(null);
            binding.doneStartDate.setText(null);

            if (this.startDate == null) {
                binding.startDateDate.setText(null);
                binding.startDateTime.setText(null);

            } else {
                final var dueDate = this.startDate.atZone(ZoneId.systemDefault());
                binding.startDateDate.setText(dueDate.format(dateFormatter));
                binding.startDateTime.setText(dueDate.format(timeFormatter));
            }

        } else {
            binding.startDateDate.setText(null);
            binding.startDateTime.setText(null);

            binding.doneDate.setText(done.atZone(ZoneId.systemDefault()).format(dateTimeFormatter));

            if (this.startDate == null) {
                binding.doneStartDate.setText(null);

            } else {
                final var dueDate = this.startDate.atZone(ZoneId.systemDefault());
                binding.doneStartDate.setText(getContext().getString(R.string.label_due_at, dueDate.format(dateTimeFormatter)));
            }
        }
    }

    private void setInteraction() {
        final var enabled = isEnabled();

        binding.startDateDate.setEnabled(enabled);
        binding.startDateTime.setEnabled(enabled);

        if (enabled) {
            binding.clearStartDate.setOnClickListener(v -> {
                if (this.startDateChangedListener != null) {
                    this.startDateChangedListener.onStartDateChanged(null);
                }
            });

            binding.clearStartDate.setOnClickListener(v -> {
                if (this.startDateChangedListener != null) {
                    this.startDateChangedListener.onStartDateChanged(null);
                }
            });

            binding.startDateDate.setOnClickListener(v -> {
                if (this.fragmentManager == null || this.color == null) {
                    return;
                }
                final LocalDate date;
                if (this.startDate != null) {
                    date = this.startDate.atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    date = LocalDate.now();
                }

                ThemedDatePickerDialog.newInstance(this, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), this.color)
                        .show(this.fragmentManager, ThemedDatePickerDialog.class.getCanonicalName());
            });

            binding.startDateTime.setOnClickListener(v -> {
                if (this.fragmentManager == null || this.color == null) {
                    return;
                }
                final LocalTime time;
                if (this.startDate != null) {
                    time = this.startDate.atZone(ZoneId.systemDefault()).toLocalTime();
                } else {
                    time = LocalTime.now();
                }
                ThemedTimePickerDialog.newInstance(this, time.getHour(), time.getMinute(), true, this.color)
                        .show(this.fragmentManager, ThemedTimePickerDialog.class.getCanonicalName());
            });
        } else {
            Stream.of(
                    binding.clearStartDate,
                    binding.clearStartDate,
                    binding.startDateDate,
                    binding.startDateTime
            ).forEach(v -> v.setOnClickListener(null));
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int hourOfDay;
        int minute;

        final var selectedTime = binding.startDateTime.getText();
        if (TextUtils.isEmpty(selectedTime)) {
            hourOfDay = 0;
            minute = 0;
        } else {
            assert this.startDate != null; // Since selectedTime is not empty and is derived from dueDate, dueDate itself shouldn't be null here.
            final var oldTime = LocalTime.from(this.startDate.atZone(ZoneId.systemDefault()));
            hourOfDay = oldTime.getHour();
            minute = oldTime.getMinute();
        }

        final var newDateTime = ZonedDateTime.of(
                LocalDate.of(year, monthOfYear + 1, dayOfMonth),
                LocalTime.of(hourOfDay, minute),
                ZoneId.systemDefault()
        );
        this.startDate = newDateTime.toInstant();

        if (startDateChangedListener != null) {
            startDateChangedListener.onStartDateChanged(newDateTime.toInstant());
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        final var oldDateTime = this.startDate == null ? ZonedDateTime.now() : this.startDate.atZone(ZoneId.systemDefault());
        final var newDateTime = oldDateTime.with(
                LocalTime.of(hourOfDay, minute)
        );

        if (startDateChangedListener != null) {
            startDateChangedListener.onStartDateChanged(newDateTime.toInstant());
        }
    }

    @Override
    public void applyTheme(int color) {
        this.color = color;
        final var utils = ThemeUtils.of(color, getContext());

        Stream.of(
                binding.startDateDateWrapper,
                binding.startDateTimeWrapper
        ).forEach(utils.material::colorTextInputLayout);

        Stream.of(
                binding.clearStartDate,
                binding.clearStartDate
        ).forEach(v -> utils.platform.colorImageView(v, ColorRole.SECONDARY));

        utils.platform.colorTextView(binding.doneDate, ColorRole.ON_SURFACE);
        utils.platform.colorTextView(binding.doneStartDate, ColorRole.ON_SURFACE_VARIANT);
    }

    public void setStartDateListener(@Nullable StartDateChangedListener startDateChangedListener) {
        this.startDateChangedListener = startDateChangedListener;
    }

    public interface StartDateChangedListener {
        void onStartDateChanged(@Nullable Instant startDate);
    }
}
