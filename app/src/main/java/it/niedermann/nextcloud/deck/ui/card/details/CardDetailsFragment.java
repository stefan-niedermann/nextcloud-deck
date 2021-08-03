package it.niedermann.nextcloud.deck.ui.card.details;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditTextInputLayout;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.android.markdown.MarkdownEditor;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.branding.BrandedTimePickerDialog;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.LabelAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeDialog;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

public class CardDetailsFragment extends Fragment implements OnDateSetListener, OnTimeSetListener, CardAssigneeListener {

    private FragmentCardEditTabDetailsBinding binding;
    private EditCardViewModel viewModel;
    private AssigneeAdapter adapter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    public static Fragment newInstance() {
        return new CardDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardDetailsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        @Px final int avatarSize = DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.avatar_size);
        final var avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.spacer_1x), 0);

        setupAssignees();
        setupLabels();
        setupDueDate();
        setupDescription();
        setupProjects();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getBrandingColor().observe(getViewLifecycleOwner(), this::applyBrand);
    }

    @Override
    public void onResume() {
        super.onResume();

        // https://github.com/wdullaer/MaterialDateTimePicker#why-are-my-callbacks-lost-when-the-device-changes-orientation
        final var dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag(BrandedDatePickerDialog.class.getCanonicalName());
        final var tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag(BrandedTimePickerDialog.class.getCanonicalName());
        if (tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    private void applyBrand(@ColorInt int boardColor) {
        // TODO apply correct branding on the BrandedDatePicker
        applyBrandToEditTextInputLayout(boardColor, binding.labelsWrapper);
        applyBrandToEditTextInputLayout(boardColor, binding.dueDateDateWrapper);
        applyBrandToEditTextInputLayout(boardColor, binding.dueDateTimeWrapper);
        applyBrandToEditTextInputLayout(boardColor, binding.peopleWrapper);
        applyBrandToEditTextInputLayout(boardColor, binding.descriptionEditorWrapper);
        binding.descriptionEditor.setSearchColor(boardColor);
        binding.descriptionViewer.setSearchColor(boardColor);
    }

    private void setupDescription() {
        if (viewModel.canEdit()) {
            binding.descriptionViewer.setMovementMethod(LinkMovementMethod.getInstance());
            viewModel.getDescriptionMode().observe(getViewLifecycleOwner(), (isPreviewMode) -> {
                if (isPreviewMode) {
                    toggleEditorView(binding.descriptionViewer, binding.descriptionEditorWrapper, binding.descriptionViewer);
                    binding.descriptionToggle.setImageResource(R.drawable.ic_edit_grey600_24dp);
                } else {
                    toggleEditorView(binding.descriptionEditorWrapper, binding.descriptionViewer, binding.descriptionEditor);
                    binding.descriptionToggle.setImageResource(R.drawable.ic_baseline_eye_24);
                }
            });
            binding.descriptionToggle.setOnClickListener((v) -> viewModel.toggleDescriptionPreviewMode());
        } else {
            binding.descriptionEditor.setEnabled(false);
            binding.descriptionEditorWrapper.setVisibility(VISIBLE);
            binding.descriptionViewer.setEnabled(false);
            binding.descriptionViewer.setVisibility(GONE);
            binding.descriptionViewer.setMarkdownString(viewModel.getFullCard().getCard().getDescription());
        }
    }

    private void toggleEditorView(@NonNull View viewToShow, @NonNull View viewToHide, @NonNull MarkdownEditor editorToShow) {
        editorToShow.setMarkdownString(viewModel.getFullCard().getCard().getDescription());
        if (!editorToShow.getMarkdownString().hasActiveObservers()) {
            editorToShow.getMarkdownString().observe(getViewLifecycleOwner(), (description) -> {
                if (viewModel.getFullCard() != null) {
                    viewModel.getFullCard().getCard().setDescription(description == null ? "" : description.toString());
                } else {
                    ExceptionDialogFragment.newInstance(new IllegalStateException(FullCard.class.getSimpleName() + " was empty when trying to setup description"), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
                binding.descriptionToggle.setVisibility(TextUtils.isEmpty(description) ? INVISIBLE : VISIBLE);
            });
        }
        viewToHide.setVisibility(GONE);
        viewToShow.setVisibility(VISIBLE);
    }

    private void setupDueDate() {
        if (this.viewModel.getFullCard().getCard().getDueDate() != null) {
            final var dueDate = this.viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault());
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
        final long accountId = viewModel.getAccount().getId();
        final long boardId = viewModel.getBoardId();
        binding.labelsGroup.removeAllViews();
        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.labels.setAdapter(new LabelAutoCompleteAdapter(requireActivity(), accountId, boardId, localCardId));
            binding.labels.setOnItemClickListener((adapterView, view, position, id) -> {
                final var label = (Label) adapterView.getItemAtPosition(position);
                if (LabelAutoCompleteAdapter.ITEM_CREATE == label.getLocalId()) {
                    final Label newLabel = new Label(label);
                    newLabel.setBoardId(boardId);
                    newLabel.setTitle(((LabelAutoCompleteAdapter) binding.labels.getAdapter()).getLastFilterText());
                    newLabel.setLocalId(null);
                    viewModel.createLabel(accountId, newLabel, boardId, new IResponseCallback<>() {
                        @Override
                        public void onResponse(Label response) {
                            requireActivity().runOnUiThread(() -> {
                                newLabel.setLocalId(response.getLocalId());
                                ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(response);
                                viewModel.getFullCard().getLabels().add(response);
                                binding.labelsGroup.addView(createChipFromLabel(newLabel));
                                binding.labelsGroup.setVisibility(VISIBLE);
                            });
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            requireActivity().runOnUiThread(() -> BrandedSnackbar.make(requireView(), getString(R.string.error_create_label, newLabel.getTitle()), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName())).show());
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
            for (final var label : viewModel.getFullCard().getLabels()) {
                binding.labelsGroup.addView(createChipFromLabel(label));
            }
            binding.labelsGroup.setVisibility(VISIBLE);
        } else {
            binding.labelsGroup.setVisibility(INVISIBLE);
        }
    }

    private Chip createChipFromLabel(Label label) {
        final var chip = new Chip(requireContext());
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
            binding.people.setAdapter(new UserAutoCompleteAdapter(requireActivity(), viewModel.getAccount(), viewModel.getBoardId(), localCardId));
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

        final var selectedTime = binding.dueDateTime.getText();
        if (TextUtils.isEmpty(selectedTime)) {
            hourOfDay = 0;
            minute = 0;
        } else {
            final LocalTime oldTime = LocalTime.from(this.viewModel.getFullCard().getCard().getDueDate().atZone(ZoneId.systemDefault()));
            hourOfDay = oldTime.getHour();
            minute = oldTime.getMinute();
        }

        final var newDateTime = ZonedDateTime.of(
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
        final var oldInstant = this.viewModel.getFullCard().getCard().getDueDate();
        final var oldDateTime = oldInstant == null ? ZonedDateTime.now() : oldInstant.atZone(ZoneId.systemDefault());
        final var newDateTime = oldDateTime.with(
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
            final var adapter = new CardProjectsAdapter(viewModel.getFullCard().getProjects(), getChildFragmentManager());
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
