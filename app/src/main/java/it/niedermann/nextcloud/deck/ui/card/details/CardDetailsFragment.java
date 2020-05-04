package it.niedermann.nextcloud.deck.ui.card.details;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.edit.EditFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandedTimePickerDialog;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.LabelAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.MarkDownUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static android.text.format.DateFormat.getDateFormat;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.applyBrandToEditText;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class CardDetailsFragment extends BrandedFragment implements OnDateSetListener, OnTimeSetListener {

    private FragmentCardEditTabDetailsBinding binding;
    private EditCardViewModel viewModel;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    @Px
    private int avatarSize;
    private LinearLayout.LayoutParams avatarLayoutParams;
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
        dateFormat = getDateFormat(activity);

        viewModel = new ViewModelProvider(activity).get(EditCardViewModel.class);
        syncManager = new SyncManager(requireContext());

        avatarSize = dpToPx(requireContext(), R.dimen.avatar_size);
        avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, dpToPx(requireContext(), R.dimen.spacer_1x), 0);

        setupPeople();
        setupLabels();
        setupDueDate();
        setupDescription();
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
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToEditText(mainColor, textColor, binding.labels);
        applyBrandToEditText(mainColor, textColor, binding.dueDateDate);
        applyBrandToEditText(mainColor, textColor, binding.dueDateTime);
        applyBrandToEditText(mainColor, textColor, binding.people);
        applyBrandToEditText(mainColor, textColor, binding.description);
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

    private TimePickerDialog createTimePickerDialogFromDate(
            @Nullable OnTimeSetListener listener,
            @Nullable Date date
    ) {
        int hourOfDay = 0;
        int minutes = 0;

        if (date != null) {
            hourOfDay = date.getHours();
            minutes = date.getMinutes();
        }
        return BrandedTimePickerDialog.newInstance(listener, hourOfDay, minutes, true);
    }

    private DatePickerDialog createDatePickerDialogFromDate(
            @Nullable OnDateSetListener listener,
            @Nullable Date date
    ) {
        int year;
        int month;
        int day;

        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
        return BrandedDatePickerDialog.newInstance(listener, year, month, day);
    }

    private void setupDueDate() {
        if (this.viewModel.getFullCard().getCard().getDueDate() != null) {
            binding.dueDateDate.setText(dateFormat.format(this.viewModel.getFullCard().getCard().getDueDate()));
            binding.dueDateTime.setText(dueTime.format(this.viewModel.getFullCard().getCard().getDueDate()));
            binding.clearDueDate.setVisibility(View.VISIBLE);
        } else {
            binding.clearDueDate.setVisibility(View.GONE);
            binding.dueDateDate.setText(null);
            binding.dueDateTime.setText(null);
        }

        if (viewModel.canEdit()) {

            binding.dueDateDate.setOnClickListener(v -> {
                if (viewModel.getFullCard() != null && viewModel.getFullCard().getCard() != null) {
                    createDatePickerDialogFromDate(this, viewModel.getFullCard().getCard().getDueDate()).show(getChildFragmentManager(), BrandedDatePickerDialog.class.getCanonicalName());
                } else {
                    createDatePickerDialogFromDate(this, null).show(getChildFragmentManager(), BrandedDatePickerDialog.class.getCanonicalName());
                }
            });

            binding.dueDateTime.setOnClickListener(v -> {
                if (viewModel.getFullCard() != null && viewModel.getFullCard().getCard() != null) {
                    createTimePickerDialogFromDate(this, viewModel.getFullCard().getCard().getDueDate()).show(getChildFragmentManager(), BrandedTimePickerDialog.class.getCanonicalName());
                } else {
                    createTimePickerDialogFromDate(this, null).show(getChildFragmentManager(), BrandedTimePickerDialog.class.getCanonicalName());
                }
            });

            binding.clearDueDate.setOnClickListener(v -> {
                binding.dueDateDate.setText(null);
                binding.dueDateTime.setText(null);
                viewModel.getFullCard().getCard().setDueDate(null);
                binding.clearDueDate.setVisibility(View.GONE);
            });
        } else {
            binding.dueDateDate.setEnabled(false);
            binding.dueDateTime.setEnabled(false);
            binding.clearDueDate.setVisibility(View.GONE);
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
                            Snackbar.make(requireView(), getString(R.string.error_create_label, newLabel.getTitle()), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(createLabelLiveData.getError()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName())).show();
                        } else {
                            newLabel.setLocalId(createdLabel.getLocalId());
                            ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(createdLabel);
                            viewModel.getFullCard().getLabels().add(createdLabel);
                            binding.labelsGroup.addView(createChipFromLabel(newLabel));
                            binding.labelsGroup.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
                    viewModel.getFullCard().getLabels().add(label);
                    binding.labelsGroup.addView(createChipFromLabel(label));
                    binding.labelsGroup.setVisibility(View.VISIBLE);
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
            binding.labelsGroup.setVisibility(View.VISIBLE);
        } else {
            binding.labelsGroup.setVisibility(View.INVISIBLE);
        }
    }


    private Chip createChipFromLabel(Label label) {
        final Chip chip = new Chip(activity);
        chip.setText(label.getTitle());
        if (viewModel.canEdit()) {
            chip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_circle_grey600));
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                binding.labelsGroup.removeView(chip);
                viewModel.getFullCard().getLabels().remove(label);
                ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
            });
        }
        try {
            final int labelColor = Color.parseColor("#" + label.getColor());
            chip.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
            final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
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

    private void setupPeople() {
        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.people.setAdapter(new UserAutoCompleteAdapter(activity, viewModel.getAccount(), viewModel.getBoardId(), localCardId));
            binding.people.setOnItemClickListener((adapterView, view, position, id) -> {
                User user = (User) adapterView.getItemAtPosition(position);
                viewModel.getFullCard().getAssignedUsers().add(user);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                addAvatar(viewModel.getAccount().getUrl(), user);
                binding.people.setText("");
            });

            if (this.viewModel.getFullCard().getAssignedUsers() != null) {
                binding.peopleList.removeAllViews();
                for (User user : this.viewModel.getFullCard().getAssignedUsers()) {
                    addAvatar(viewModel.getAccount().getUrl(), user);
                }
            }
        } else {
            binding.people.setEnabled(false);
        }
    }

    private void addAvatar(String baseUrl, User user) {
        ImageView avatar = new ImageView(activity);
        avatar.setLayoutParams(avatarLayoutParams);
        if (viewModel.canEdit()) {
            avatar.setOnClickListener(v -> {
                viewModel.getFullCard().getAssignedUsers().remove(user);
                binding.peopleList.removeView(avatar);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).include(user);
                Snackbar.make(
                        requireView(), getString(R.string.unassigned_user, user.getDisplayname()),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_undo, v1 -> {
                            viewModel.getFullCard().getAssignedUsers().add(user);
                            ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                            addAvatar(baseUrl, user);
                        }).show();
            });
        }
        binding.peopleList.addView(avatar);
        avatar.requestLayout();
        ViewUtil.addAvatar(avatar, baseUrl, user.getUid(), avatarSize, R.drawable.ic_person_grey600_24dp);
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        int hourOfDay;
        int minute;

        if (binding.dueDateTime.getText() != null && binding.dueDateTime.length() > 0) {
            hourOfDay = this.viewModel.getFullCard().getCard().getDueDate().getHours();
            minute = this.viewModel.getFullCard().getCard().getDueDate().getMinutes();
        } else {
            hourOfDay = 0;
            minute = 0;
        }

        c.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        this.viewModel.getFullCard().getCard().setDueDate(c.getTime());
        binding.dueDateDate.setText(dateFormat.format(c.getTime()));

        if (this.viewModel.getFullCard().getCard().getDueDate() == null || this.viewModel.getFullCard().getCard().getDueDate().getTime() == 0) {
            binding.clearDueDate.setVisibility(View.GONE);
        } else {
            binding.clearDueDate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (this.viewModel.getFullCard().getCard().getDueDate() == null) {
            this.viewModel.getFullCard().getCard().setDueDate(new Date());
        }
        this.viewModel.getFullCard().getCard().getDueDate().setHours(hourOfDay);
        this.viewModel.getFullCard().getCard().getDueDate().setMinutes(minute);
        binding.dueDateTime.setText(dueTime.format(this.viewModel.getFullCard().getCard().getDueDate().getTime()));
        if (this.viewModel.getFullCard().getCard().getDueDate() == null || this.viewModel.getFullCard().getCard().getDueDate().getTime() == 0) {
            binding.clearDueDate.setVisibility(View.GONE);
        } else {
            binding.clearDueDate.setVisibility(View.VISIBLE);
        }
    }
}
