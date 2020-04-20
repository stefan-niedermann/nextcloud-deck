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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
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
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDatePickerDialog;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandedTimePickerDialog;
import it.niedermann.nextcloud.deck.ui.card.LabelAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.MarkDownUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.applyBrandToEditText;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class CardDetailsFragment extends BrandedFragment implements OnDateSetListener, OnTimeSetListener {

    private FragmentCardEditTabDetailsBinding binding;

    private boolean canEdit = false;
    private FullCard fullCard;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private String baseUrl;
    private int avatarSize;
    private LinearLayout.LayoutParams avatarLayoutParams;
    private CardDetailsListener cardDetailsListener;
    private AppCompatActivity activity;

    public CardDetailsFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CardDetailsListener) {
            this.cardDetailsListener = (CardDetailsListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + CardDetailsListener.class.getCanonicalName());
        }
        if (context instanceof AppCompatActivity) {
            this.activity = (AppCompatActivity) context;
        } else {
            throw new ClassCastException("Calling context must be an " + AppCompatActivity.class.getCanonicalName());
        }
    }

    public static CardDetailsFragment newInstance(long accountId, long localId, long boardId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardDetailsFragment fragment = new CardDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabDetailsBinding.inflate(inflater, container, false);
        dateFormat = android.text.format.DateFormat.getDateFormat(activity);

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);
            if (args.containsKey(BUNDLE_KEY_CAN_EDIT)) {
                this.canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);
            }

            syncManager = new SyncManager(activity);

            if (NO_LOCAL_ID.equals(localId)) {
                fullCard = new FullCard();
                fullCard.setCard(new Card());
                setupView(accountId, boardId, canEdit);
            } else {
                observeOnce(syncManager.getCardByLocalId(accountId, localId), CardDetailsFragment.this, (next) -> {
                    fullCard = next;
                    binding.description.setText(fullCard.getCard().getDescription());
                    setupView(accountId, boardId, canEdit);
                });
            }
        }

        avatarSize = DimensionUtil.dpToPx(requireContext(), R.dimen.avatar_size);
        avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, requireContext().getResources().getDimensionPixelSize(R.dimen.spacer_1x), 0);

        try {
            baseUrl = syncManager.getServerUrl();
        } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }
        return binding.getRoot();
    }

    private void setupView(long accountId, long boardId, boolean canEdit) {
        setupPeople(accountId, boardId);
        setupLabels(accountId, boardId, canEdit);
        setupDueDate();
        setupDescription();
    }

    private void setupDescription() {
        if (canEdit) {
            MarkdownProcessor markdownProcessor = new MarkdownProcessor(requireContext());
            markdownProcessor.config(MarkDownUtil.getMarkDownConfiguration(binding.description.getContext()).build());
            markdownProcessor.factory(EditFactory.create());
            markdownProcessor.live(binding.description);
            binding.description.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (fullCard != null) {
                        cardDetailsListener.onDescriptionChanged(binding.description.getText().toString());
                        fullCard.getCard().setDescription(binding.description.getText().toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
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
        if (this.fullCard.getCard().getDueDate() != null) {
            binding.dueDateDate.setText(dateFormat.format(this.fullCard.getCard().getDueDate()));
            binding.dueDateTime.setText(dueTime.format(this.fullCard.getCard().getDueDate()));
            binding.clearDueDate.setVisibility(View.VISIBLE);
        } else {
            binding.clearDueDate.setVisibility(View.GONE);
            binding.dueDateDate.setText(null);
            binding.dueDateTime.setText(null);
        }

        if (canEdit) {

            binding.dueDateDate.setOnClickListener(v -> {
                if (fullCard != null && fullCard.getCard() != null) {
                    createDatePickerDialogFromDate(this, fullCard.getCard().getDueDate()).show(getChildFragmentManager(), BrandedDatePickerDialog.class.getCanonicalName());
                } else {
                    createDatePickerDialogFromDate(this, null).show(getChildFragmentManager(), BrandedDatePickerDialog.class.getCanonicalName());
                }
            });

            binding.dueDateTime.setOnClickListener(v -> {
                if (fullCard != null && fullCard.getCard() != null) {
                    createTimePickerDialogFromDate(this, fullCard.getCard().getDueDate()).show(getChildFragmentManager(), BrandedTimePickerDialog.class.getCanonicalName());
                } else {
                    createTimePickerDialogFromDate(this, null).show(getChildFragmentManager(), BrandedTimePickerDialog.class.getCanonicalName());
                }
            });

            binding.clearDueDate.setOnClickListener(v -> {
                cardDetailsListener.onDueDateChanged(null);
                binding.dueDateDate.setText(null);
                binding.dueDateTime.setText(null);
                fullCard.getCard().setDueDate(null);
                binding.clearDueDate.setVisibility(View.GONE);
            });
        } else {
            binding.dueDateDate.setEnabled(false);
            binding.dueDateTime.setEnabled(false);
            binding.clearDueDate.setVisibility(View.GONE);
        }
    }

    private void setupLabels(long accountId, long boardId, boolean canEdit) {
        binding.labelsGroup.removeAllViews();
        if (canEdit) {
            Long localCardId = fullCard.getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.labels.setAdapter(new LabelAutoCompleteAdapter(activity, accountId, boardId, localCardId));
            binding.labels.setOnItemClickListener((adapterView, view, position, id) -> {
                Label label = (Label) adapterView.getItemAtPosition(position);
                if (LabelAutoCompleteAdapter.ITEM_CREATE == label.getLocalId()) {
                    Label newLabel = new Label(label);
                    newLabel.setBoardId(boardId);
                    newLabel.setTitle(((LabelAutoCompleteAdapter) binding.labels.getAdapter()).getLastFilterText());
                    newLabel.setLocalId(null);
                    observeOnce(syncManager.createLabel(accountId, newLabel, boardId), CardDetailsFragment.this, createdLabel -> {
                        newLabel.setLocalId(createdLabel.getLocalId());
                        ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(createdLabel);
                        cardDetailsListener.onLabelAdded(createdLabel);
                        binding.labelsGroup.addView(createChipFromLabel(newLabel));
                        binding.labelsGroup.setVisibility(View.VISIBLE);
                    });
                } else {
                    ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
                    cardDetailsListener.onLabelAdded(label);
                    binding.labelsGroup.addView(createChipFromLabel(label));
                    binding.labelsGroup.setVisibility(View.VISIBLE);
                }

                binding.labels.setText("");
            });
        } else {
            binding.labels.setEnabled(false);
        }
        if (fullCard.getLabels() != null && fullCard.getLabels().size() > 0) {
            for (Label label : fullCard.getLabels()) {
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
        if (canEdit) {
            chip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_circle_grey600));
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                binding.labelsGroup.removeView(chip);
                cardDetailsListener.onLabelRemoved(label);
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

    private void setupPeople(long accountId, long boardId) {
        if (canEdit) {
            Long localCardId = fullCard.getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            binding.people.setAdapter(new UserAutoCompleteAdapter(activity, accountId, boardId, localCardId));
            binding.people.setOnItemClickListener((adapterView, view, position, id) -> {
                User user = (User) adapterView.getItemAtPosition(position);
                cardDetailsListener.onUserAdded(user);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                if (baseUrl != null) {
                    addAvatar(baseUrl, user);
                }
                binding.people.setText("");
            });

            if (this.fullCard.getAssignedUsers() != null) {
                binding.peopleList.removeAllViews();
                if (baseUrl != null) {
                    for (User user : this.fullCard.getAssignedUsers()) {
                        addAvatar(baseUrl, user);
                    }
                }
            }
        } else {
            binding.people.setEnabled(false);
        }
    }

    private void addAvatar(String baseUrl, User user) {
        ImageView avatar = new ImageView(activity);
        avatar.setLayoutParams(avatarLayoutParams);
        if (canEdit) {
            avatar.setOnClickListener(v -> {
                cardDetailsListener.onUserRemoved(user);
                binding.peopleList.removeView(avatar);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).include(user);
                Snackbar.make(
                        requireView(), getString(R.string.unassigned_user, user.getDisplayname()),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_undo, v1 -> {
                            cardDetailsListener.onUserAdded(user);
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

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        int hourOfDay;
        int minute;

        if (binding.dueDateTime.getText() != null && binding.dueDateTime.length() > 0) {
            hourOfDay = this.fullCard.getCard().getDueDate().getHours();
            minute = this.fullCard.getCard().getDueDate().getMinutes();
        } else {
            hourOfDay = 0;
            minute = 0;
        }

        c.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        this.fullCard.getCard().setDueDate(c.getTime());
        binding.dueDateDate.setText(dateFormat.format(c.getTime()));
        cardDetailsListener.onDueDateChanged(fullCard.card.getDueDate());

        if (this.fullCard.getCard().getDueDate() == null || this.fullCard.getCard().getDueDate().getTime() == 0) {
            binding.clearDueDate.setVisibility(View.GONE);
        } else {
            binding.clearDueDate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (this.fullCard.getCard().getDueDate() == null) {
            this.fullCard.getCard().setDueDate(new Date());
        }
        this.fullCard.getCard().getDueDate().setHours(hourOfDay);
        this.fullCard.getCard().getDueDate().setMinutes(minute);
        binding.dueDateTime.setText(dueTime.format(this.fullCard.getCard().getDueDate().getTime()));
        cardDetailsListener.onDueDateChanged(fullCard.card.getDueDate());
        if (this.fullCard.getCard().getDueDate() == null || this.fullCard.getCard().getDueDate().getTime() == 0) {
            binding.clearDueDate.setVisibility(View.GONE);
        } else {
            binding.clearDueDate.setVisibility(View.VISIBLE);
        }
    }
}
