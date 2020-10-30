package it.niedermann.nextcloud.deck.ui.card.details;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.edit.EditFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.branding.BrandedTimePickerDialog;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.LabelAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeDialog;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.util.MarkDownUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditText;

public class CardDetailsFragment extends BrandedFragment implements OnDateSetListener, OnTimeSetListener, CardAssigneeListener {

    private FragmentCardEditTabDetailsBinding binding;
    private EditCardViewModel viewModel;
    private SyncManager syncManager;
    private AssigneeAdapter adapter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private AppCompatActivity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.activity = (AppCompatActivity) context;
        } else {
            throw new ClassCastException("Calling context must be an " + AppCompatActivity.class.getCanonicalName());
        }
    }

    public static Fragment newInstance() {
        return new CardDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabDetailsBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(activity).get(EditCardViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardDetailsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        syncManager = new SyncManager(requireContext());

        @Px
        final int avatarSize = DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.avatar_size);
        final LinearLayout.LayoutParams avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.spacer_1x), 0);

        setupAssignees();
        setupLabels();
        setupDueDate();
        setupDescription();
        setupProjects();
        binding.description.setText(viewModel.getFullCard().getCard().getDescription());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        // https://github.com/wdullaer/MaterialDateTimePicker#why-are-my-callbacks-lost-when-the-device-changes-orientation
        final DatePickerDialog dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag(BrandedDatePickerDialog.class.getCanonicalName());
        final TimePickerDialog tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag(BrandedTimePickerDialog.class.getCanonicalName());
        if (tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToEditText(mainColor, binding.labels);
        applyBrandToEditText(mainColor, binding.dueDateDate);
        applyBrandToEditText(mainColor, binding.dueDateTime);
        applyBrandToEditText(mainColor, binding.people);
        applyBrandToEditText(mainColor, binding.description);
    }

    private void setupDescription() {
        if (viewModel.canEdit()) {
            MarkdownProcessor markdownProcessor = new MarkdownProcessor(requireContext());
            markdownProcessor.config(MarkDownUtil.getMarkDownConfiguration(binding.description.getContext()).build());
            markdownProcessor.factory(EditFactory.create());
            markdownProcessor.live(binding.description);
            binding.description.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (viewModel.getFullCard() != null) {
                        viewModel.getFullCard().getCard().setDescription(binding.description.getText().toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing to do
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Nothing to do
                }
            });
        } else {
            binding.description.setEnabled(false);
        }
    }

    private void setupDueDate() {
        if (this.viewModel.getFullCard().getCard().getDueDate() != null) {
            final ZonedDateTime dueDate = this.viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault());
            binding.dueDateDate.setText(dueDate == null ? null : dueDate.format(dateFormatter));
            binding.dueDateTime.setText(dueDate == null ? null : dueDate.format(timeFormatter));
            binding.clearDueDate.setVisibility(VISIBLE);
        } else {
            binding.clearDueDate.setVisibility(GONE);
            binding.dueDateDate.setText(null);
            binding.dueDateTime.setText(null);
        }

        if (viewModel.canEdit()) {

            binding.dueDateDate.setOnClickListener(v -> {
                final LocalDate date;
                if (viewModel.getFullCard() != null && viewModel.getFullCard().getCard() != null && viewModel.getFullCard().getCard().getDueDate() != null) {
                    date = viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    date = LocalDate.now();
                }
                BrandedDatePickerDialog.newInstance(this, date.getYear(), date.getMonthValue(), date.getDayOfMonth())
                        .show(getChildFragmentManager(), BrandedDatePickerDialog.class.getCanonicalName());
            });

            binding.dueDateTime.setOnClickListener(v -> {
                final LocalTime time;
                if (viewModel.getFullCard() != null && viewModel.getFullCard().getCard() != null && viewModel.getFullCard().getCard().getDueDate() != null) {
                    time = viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault()).toLocalTime();
                } else {
                    time = LocalTime.now();
                }
                BrandedTimePickerDialog.newInstance(this, time.getHour(), time.getMinute(), true)
                        .show(getChildFragmentManager(), BrandedTimePickerDialog.class.getCanonicalName());
            });

            binding.clearDueDate.setOnClickListener(v -> {
                binding.dueDateDate.setText(null);
                binding.dueDateTime.setText(null);
                viewModel.getFullCard().getCard().setDueDate(null);
                binding.clearDueDate.setVisibility(GONE);
            });
        } else {
            binding.dueDateDate.setEnabled(false);
            binding.dueDateTime.setEnabled(false);
            binding.clearDueDate.setVisibility(GONE);
        }
    }

    private void setupLabels() {
        long accountId = viewModel.getAccount().getId();
        long boardId = viewModel.getBoardId();
        binding.labelsGroup.removeAllViews();
        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.labels.setAdapter(new LabelAutoCompleteAdapter(activity, accountId, boardId, localCardId));
            binding.labels.setOnItemClickListener((adapterView, view, position, id) -> {
                final Label label = (Label) adapterView.getItemAtPosition(position);
                if (LabelAutoCompleteAdapter.ITEM_CREATE == label.getLocalId()) {
                    final Label newLabel = new Label(label);
                    newLabel.setBoardId(boardId);
                    newLabel.setTitle(((LabelAutoCompleteAdapter) binding.labels.getAdapter()).getLastFilterText());
                    newLabel.setLocalId(null);
                    WrappedLiveData<Label> createLabelLiveData = syncManager.createLabel(accountId, newLabel, boardId);
                    observeOnce(createLabelLiveData, CardDetailsFragment.this, createdLabel -> {
                        if (createLabelLiveData.hasError()) {
                            DeckLog.logError(createLabelLiveData.getError());
                            BrandedSnackbar.make(requireView(), getString(R.string.error_create_label, newLabel.getTitle()), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(createLabelLiveData.getError(), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName())).show();
                        } else {
                            newLabel.setLocalId(createdLabel.getLocalId());
                            ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(createdLabel);
                            viewModel.getFullCard().getLabels().add(createdLabel);
                            binding.labelsGroup.addView(createChipFromLabel(newLabel));
                            binding.labelsGroup.setVisibility(VISIBLE);
                        }
                    });
                } else {
                    ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
                    viewModel.getFullCard().getLabels().add(label);
                    binding.labelsGroup.addView(createChipFromLabel(label));
                    binding.labelsGroup.setVisibility(VISIBLE);
                }

                binding.labels.setText("");
            });
        } else {
            binding.labels.setEnabled(false);
        }
        if (viewModel.getFullCard().getLabels() != null && viewModel.getFullCard().getLabels().size() > 0) {
            for (Label label : viewModel.getFullCard().getLabels()) {
                binding.labelsGroup.addView(createChipFromLabel(label));
            }
            binding.labelsGroup.setVisibility(VISIBLE);
        } else {
            binding.labelsGroup.setVisibility(View.INVISIBLE);
        }
    }

    private Chip createChipFromLabel(Label label) {
        final Chip chip = new Chip(activity);
        chip.setText(label.getTitle());
        if (viewModel.canEdit()) {
            chip.setCloseIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_circle_grey600));
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                binding.labelsGroup.removeView(chip);
                viewModel.getFullCard().getLabels().remove(label);
                ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
            });
        }
        try {
            final int labelColor = label.getColor();
            chip.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
            final int color = ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(labelColor);
            chip.setTextColor(color);

            if (chip.getCloseIcon() != null) {
                Drawable wrapDrawable = DrawableCompat.wrap(chip.getCloseIcon());
                DrawableCompat.setTint(wrapDrawable, ColorUtils.setAlphaComponent(color, 150));
            }
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
        }
        return chip;
    }

    private void setupAssignees() {
        adapter = new AssigneeAdapter((user) -> CardAssigneeDialog.newInstance(user).show(getChildFragmentManager(), CardAssigneeDialog.class.getSimpleName()), viewModel.getAccount());
        binding.assignees.setAdapter(adapter);
        binding.assignees.post(() -> {
            @Px final int gutter = DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.spacer_1x);
            final int spanCount = (int) (float) binding.assignees.getWidth() / (DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.avatar_size) + gutter);
            binding.assignees.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            binding.assignees.addItemDecoration(new AssigneeDecoration(gutter));
        });
        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.people.setAdapter(new UserAutoCompleteAdapter(activity, viewModel.getAccount(), viewModel.getBoardId(), localCardId));
            binding.people.setOnItemClickListener((adapterView, view, position, id) -> {
                User user = (User) adapterView.getItemAtPosition(position);
                viewModel.getFullCard().getAssignedUsers().add(user);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                adapter.addUser(user);
                binding.people.setText("");
            });

            if (this.viewModel.getFullCard().getAssignedUsers() != null) {
                adapter.setUsers(this.viewModel.getFullCard().getAssignedUsers());
            }
        } else {
            binding.people.setEnabled(false);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int hourOfDay;
        int minute;

        final CharSequence selectedTime = binding.dueDateTime.getText();
        if (TextUtils.isEmpty(selectedTime)) {
            hourOfDay = 0;
            minute = 0;
        } else {
            final LocalTime oldTime = LocalTime.from(this.viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault()));
            hourOfDay = oldTime.getHour();
            minute = oldTime.getMinute();
        }

        final ZonedDateTime newDateTime = ZonedDateTime.of(
                LocalDate.of(year, monthOfYear + 1, dayOfMonth),
                LocalTime.of(hourOfDay, minute),
                ZoneId.systemDefault()
        );
        this.viewModel.getFullCard().getCard().setDueDate(newDateTime.toInstant());
        binding.dueDateDate.setText(newDateTime.format(dateFormatter));

        if (this.viewModel.getFullCard().getCard().getDueDate() == null || this.viewModel.getFullCard().getCard().getDueDate().toEpochMilli() == 0) {
            binding.clearDueDate.setVisibility(GONE);
        } else {
            binding.clearDueDate.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        final Instant oldInstant = this.viewModel.getFullCard().getCard().getDueDate();
        final ZonedDateTime oldDateTime = oldInstant == null ? ZonedDateTime.now() : oldInstant.atZone(ZoneId.systemDefault());
        final ZonedDateTime newDateTime = oldDateTime.with(
                LocalTime.of(hourOfDay, minute)
        );

        this.viewModel.getFullCard().getCard().setDueDate(newDateTime.toInstant());
        binding.dueDateTime.setText(newDateTime.format(timeFormatter));
        if (this.viewModel.getFullCard().getCard().getDueDate() == null || this.viewModel.getFullCard().getCard().getDueDate().toEpochMilli() == 0) {
            binding.clearDueDate.setVisibility(GONE);
        } else {
            binding.clearDueDate.setVisibility(VISIBLE);
        }
    }

    private void setupProjects() {
        if (viewModel.getFullCard().getProjects().size() > 0) {
            binding.projectsTitle.setVisibility(VISIBLE);
            binding.projects.setNestedScrollingEnabled(false);
            final CardProjectsAdapter adapter = new CardProjectsAdapter(viewModel.getFullCard().getProjects(), getChildFragmentManager());
            binding.projects.setAdapter(adapter);
            binding.projects.setVisibility(VISIBLE);
        } else {
            binding.projectsTitle.setVisibility(GONE);
            binding.projects.setVisibility(GONE);
        }
    }

    @Override
    public void onUnassignUser(@NonNull User user) {
        viewModel.getFullCard().getAssignedUsers().remove(user);
        adapter.removeUser(user);
        ((UserAutoCompleteAdapter) binding.people.getAdapter()).include(user);
        BrandedSnackbar.make(
                requireView(), getString(R.string.unassigned_user, user.getDisplayname()),
                Snackbar.LENGTH_LONG)
                .setAction(R.string.simple_undo, v1 -> {
                    viewModel.getFullCard().getAssignedUsers().add(user);
                    ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                    adapter.addUser(user);
                }).show();
    }
}
