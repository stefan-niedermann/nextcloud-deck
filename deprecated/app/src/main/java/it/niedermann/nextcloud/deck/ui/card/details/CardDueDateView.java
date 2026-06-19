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
import it.niedermann.nextcloud.deck.databinding.ViewCardDueDateBinding;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.ui.theme.ThemedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.theme.ThemedTimePickerDialog;

public class CardDueDateView extends FrameLayout implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Themed {

    private final ViewCardDueDateBinding binding;
    @Nullable
    private DueDateChangedListener dueDateChangedListener;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d. MMM yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    boolean supportsDone = false;
    @Nullable
    private Instant dueDate = null;
    @Nullable
    private Instant done = null;
    @Nullable
    @ColorInt
    private Integer color = null;
    private FragmentManager fragmentManager = null;

    public CardDueDateView(Context context) {
        super(context);
        binding = ViewCardDueDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public CardDueDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = ViewCardDueDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public CardDueDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ViewCardDueDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    /**
     * @noinspection unused
     */
    public CardDueDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        binding = ViewCardDueDateBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            super.setEnabled(enabled);
            render();
        }
    }

    public void setDueDate(@NonNull FragmentManager fragmentManager, @NonNull Version version, @Nullable Instant dueDate, @Nullable Instant done) {
        this.fragmentManager = fragmentManager;
        this.supportsDone = version.supportsDone();
        this.dueDate = dueDate;
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
                    binding.dueDateDateWrapper,
                    binding.dueDateTimeWrapper
            ).forEach(v -> v.setVisibility(View.VISIBLE));

            Stream.of(
                    binding.doneCheck,
                    binding.doneDueDate,
                    binding.doneDate,
                    binding.clearDone
            ).forEach(v -> v.setVisibility(View.GONE));

            binding.markAsDone.setVisibility(supportsDone ? View.VISIBLE : View.GONE);
            binding.clearDueDate.setVisibility(dueDate == null || !isEnabled() ? View.GONE : View.VISIBLE);
        } else {

            Stream.of(
                    binding.doneCheck,
                    binding.doneDate
            ).forEach(v -> v.setVisibility(View.VISIBLE));

            Stream.of(
                    binding.markAsDone,
                    binding.dueDateDateWrapper,
                    binding.dueDateTimeWrapper,
                    binding.clearDueDate
            ).forEach(v -> v.setVisibility(View.GONE));

            binding.clearDone.setVisibility(supportsDone ? View.VISIBLE : View.GONE);
            binding.doneDueDate.setVisibility(dueDate == null || !isEnabled() ? View.GONE : View.VISIBLE);
        }
    }

    private void setTextState() {
        if (done == null) {
            binding.doneDate.setText(null);
            binding.doneDueDate.setText(null);

            if (this.dueDate == null) {
                binding.dueDateDate.setText(null);
                binding.dueDateTime.setText(null);

            } else {
                final var dueDate = this.dueDate.atZone(ZoneId.systemDefault());
                binding.dueDateDate.setText(dueDate.format(dateFormatter));
                binding.dueDateTime.setText(dueDate.format(timeFormatter));
            }

        } else {
            binding.dueDateDate.setText(null);
            binding.dueDateTime.setText(null);

            binding.doneDate.setText(done.atZone(ZoneId.systemDefault()).format(dateTimeFormatter));

            if (this.dueDate == null) {
                binding.doneDueDate.setText(null);

            } else {
                final var dueDate = this.dueDate.atZone(ZoneId.systemDefault());
                binding.doneDueDate.setText(getContext().getString(R.string.label_due_at, dueDate.format(dateTimeFormatter)));
            }
        }
    }

    private void setInteraction() {
        final var enabled = isEnabled();

        binding.dueDateDate.setEnabled(enabled);
        binding.dueDateTime.setEnabled(enabled);

        if (enabled) {
            binding.clearDone.setOnClickListener(v -> {
                if (this.dueDateChangedListener != null) {
                    this.dueDateChangedListener.onDoneChanged(null);
                }
            });

            if (supportsDone) {
                binding.markAsDone.setOnClickListener(v -> {
                    if (this.dueDateChangedListener != null) {
                        this.dueDateChangedListener.onDoneChanged(Instant.now());
                    }
                });
            } else {
                binding.markAsDone.setOnClickListener(null);
            }

            binding.clearDueDate.setOnClickListener(v -> {
                if (this.dueDateChangedListener != null) {
                    this.dueDateChangedListener.onDueDateChanged(null);
                }
            });

            binding.dueDateDate.setOnClickListener(v -> {
                if (this.fragmentManager == null || this.color == null) {
                    return;
                }
                final LocalDate date;
                if (this.dueDate != null) {
                    date = this.dueDate.atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    date = LocalDate.now();
                }

                ThemedDatePickerDialog.newInstance(this, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), this.color)
                        .show(this.fragmentManager, ThemedDatePickerDialog.class.getCanonicalName());
            });

            binding.dueDateTime.setOnClickListener(v -> {
                if (this.fragmentManager == null || this.color == null) {
                    return;
                }
                final LocalTime time;
                if (this.dueDate != null) {
                    time = this.dueDate.atZone(ZoneId.systemDefault()).toLocalTime();
                } else {
                    time = LocalTime.now();
                }
                ThemedTimePickerDialog.newInstance(this, time.getHour(), time.getMinute(), true, this.color)
                        .show(this.fragmentManager, ThemedTimePickerDialog.class.getCanonicalName());
            });
        } else {
            Stream.of(
                    binding.clearDone,
                    binding.markAsDone,
                    binding.clearDueDate,
                    binding.dueDateDate,
                    binding.dueDateTime
            ).forEach(v -> v.setOnClickListener(null));
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int hourOfDay;
        int minute;

        final var selectedTime = binding.dueDateTime.getText();
        if (TextUtils.isEmpty(selectedTime)) {
            hourOfDay = 0;
            minute = 0;
        } else {
            assert this.dueDate != null; // Since selectedTime is not empty and is derived from dueDate, dueDate itself shouldn't be null here.
            final var oldTime = LocalTime.from(this.dueDate.atZone(ZoneId.systemDefault()));
            hourOfDay = oldTime.getHour();
            minute = oldTime.getMinute();
        }

        final var newDateTime = ZonedDateTime.of(
                LocalDate.of(year, monthOfYear + 1, dayOfMonth),
                LocalTime.of(hourOfDay, minute),
                ZoneId.systemDefault()
        );
        this.dueDate = newDateTime.toInstant();

        if (dueDateChangedListener != null) {
            dueDateChangedListener.onDueDateChanged(newDateTime.toInstant());
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        final var oldDateTime = this.dueDate == null ? ZonedDateTime.now() : this.dueDate.atZone(ZoneId.systemDefault());
        final var newDateTime = oldDateTime.with(
                LocalTime.of(hourOfDay, minute)
        );

        if (dueDateChangedListener != null) {
            dueDateChangedListener.onDueDateChanged(newDateTime.toInstant());
        }
    }

    @Override
    public void applyTheme(int color) {
        this.color = color;
        final var utils = ThemeUtils.of(color, getContext());

        Stream.of(
                binding.dueDateDateWrapper,
                binding.dueDateTimeWrapper
        ).forEach(utils.material::colorTextInputLayout);

        Stream.of(
                binding.clearDone,
                binding.clearDueDate
        ).forEach(v -> utils.platform.colorImageView(v, ColorRole.SECONDARY));

        utils.platform.colorTextView(binding.doneDate, ColorRole.ON_SURFACE);
        utils.platform.colorTextView(binding.doneDueDate, ColorRole.ON_SURFACE_VARIANT);
        utils.material.colorMaterialButtonPrimaryTonal(binding.markAsDone);
    }

    public void setDueDateListener(@Nullable DueDateChangedListener dueDateChangedListener) {
        this.dueDateChangedListener = dueDateChangedListener;
    }

    public interface DueDateChangedListener {
        void onDueDateChanged(@Nullable Instant dueDate);

        void onDoneChanged(@Nullable Instant done);
    }
}
