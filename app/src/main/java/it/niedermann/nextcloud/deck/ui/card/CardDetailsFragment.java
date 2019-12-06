package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.yydcdut.markdown.syntax.edit.EditFactory;
import com.yydcdut.rxmarkdown.RxMDEditText;
import com.yydcdut.rxmarkdown.RxMarkdown;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.widget.DelayedAutoCompleteTextView;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.MarkDownUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;
import rx.Subscriber;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class CardDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = CardDetailsFragment.class.getCanonicalName();

    private boolean canEdit = false;
    private FullCard fullCard;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private String baseUrl;
    private int avatarSize;
    private LinearLayout.LayoutParams avatarLayoutParams;
    private Unbinder unbinder;
    private CardDetailsListener cardDetailsListener;
    private Activity activity;

    @BindView(R.id.description)
    RxMDEditText description;
    @BindView(R.id.people)
    DelayedAutoCompleteTextView people;
    @BindView(R.id.labels)
    DelayedAutoCompleteTextView labels;
    @BindView(R.id.peopleList)
    LinearLayout peopleList;
    @BindView(R.id.dueDateDate)
    TextView dueDate;
    @BindView(R.id.dueDateTime)
    TextView dueDateTime;
    @BindView(R.id.clearDueDate)
    ImageView clearDueDate;
    @BindView(R.id.labelsGroup)
    ChipGroup labelsGroup;

    @BindDrawable(R.drawable.ic_close_circle_grey600)
    Drawable closeCircleDrawable;

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
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else {
            throw new ClassCastException("Calling context must be an activity");
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
        View view = inflater.inflate(R.layout.fragment_card_edit_tab_details, container, false);
        unbinder = ButterKnife.bind(this, view);
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
                    description.setText(fullCard.getCard().getDescription());
                    setupView(accountId, boardId, canEdit);
                });
            }
        }

        avatarSize = DimensionUtil.getAvatarDimension(Objects.requireNonNull(getContext()));
        avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.standard_half_padding), 0);

        try {
            baseUrl = syncManager.getServerUrl();
        } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        return view;
    }

    private void setupView(long accountId, long boardId, boolean canEdit) {
        setupPeople(accountId, boardId);
        setupLabels(accountId, boardId, canEdit);
        setupDueDate();
        setupDescription();
    }

    private void setupDescription() {
        if (canEdit) {
            RxMarkdown.live(description)
                    .config(MarkDownUtil.getMarkDownConfiguration(description.getContext()).build())
                    .factory(EditFactory.create())
                    .intoObservable()
                    .subscribe(new Subscriber<CharSequence>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(CharSequence charSequence) {
                            description.setText(charSequence, TextView.BufferType.SPANNABLE);
                        }
                    });
            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (fullCard != null) {
                        cardDetailsListener.onDescriptionChanged(description.getText().toString());
                        fullCard.getCard().setDescription(description.getText().toString());
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
            description.setEnabled(false);
        }
    }

    private TimePickerDialog createTimePickerDialogFromDate(
            @NonNull Context context,
            @Nullable TimePickerDialog.OnTimeSetListener listener,
            @Nullable Date date
    ) {
        int hourOfDay = 0;
        int minutes = 0;

        if (date != null) {
            hourOfDay = date.getHours();
            minutes = date.getMinutes();
        }
        return new TimePickerDialog(context, listener, hourOfDay, minutes, true);
    }

    private DatePickerDialog createDatePickerDialogFromDate(
            @NonNull Context context,
            @Nullable DatePickerDialog.OnDateSetListener listener,
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
        return new DatePickerDialog(context, listener, year, month, day);
    }

    private void setupDueDate() {
        if (this.fullCard.getCard().getDueDate() != null) {
            dueDate.setText(dateFormat.format(this.fullCard.getCard().getDueDate()));
            dueDateTime.setText(dueTime.format(this.fullCard.getCard().getDueDate()));
            clearDueDate.setVisibility(View.VISIBLE);
        } else {
            clearDueDate.setVisibility(View.GONE);
            dueDate.setText(null);
            dueDateTime.setText(null);
        }

        if (canEdit) {

            dueDate.setOnClickListener(v -> {
                if (fullCard != null && fullCard.getCard() != null) {
                    createDatePickerDialogFromDate(activity, this, fullCard.getCard().getDueDate()).show();
                } else {
                    createDatePickerDialogFromDate(activity, this, null).show();
                }
            });

            dueDateTime.setOnClickListener(v -> {
                if (fullCard != null && fullCard.getCard() != null) {
                    createTimePickerDialogFromDate(activity, this, fullCard.getCard().getDueDate()).show();
                } else {
                    createTimePickerDialogFromDate(activity, this, null).show();
                }
            });

            clearDueDate.setOnClickListener(v -> {
                cardDetailsListener.onDueDateChanged(null);
                dueDate.setText(null);
                dueDateTime.setText(null);
                fullCard.getCard().setDueDate(null);
                clearDueDate.setVisibility(View.GONE);
            });
        } else {
            dueDate.setEnabled(false);
            dueDateTime.setEnabled(false);
            clearDueDate.setVisibility(View.GONE);
        }
    }

    private void setupLabels(long accountId, long boardId, boolean canEdit) {
        labelsGroup.removeAllViews();
        if (canEdit) {
            Long localCardId = fullCard.getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            labels.setAdapter(new LabelAutoCompleteAdapter(this, activity, accountId, boardId, localCardId));
            labels.setOnItemClickListener((adapterView, view, position, id) -> {
                Label label = (Label) adapterView.getItemAtPosition(position);
                if (LabelAutoCompleteAdapter.CREATE_ID == label.getLocalId()) {
                    Label newLabel = new Label(label);
                    newLabel.setTitle(((LabelAutoCompleteAdapter) labels.getAdapter()).getLastFilterText());
                    newLabel.setLocalId(null);
                    observeOnce(syncManager.createLabel(accountId, newLabel, boardId), CardDetailsFragment.this, createdLabel -> {
                        newLabel.setLocalId(createdLabel.getLocalId());
                        cardDetailsListener.onLabelAdded(createdLabel);
                        labelsGroup.addView(createChipFromLabel(newLabel));
                        labelsGroup.setVisibility(View.VISIBLE);
                    });
                } else {
                    cardDetailsListener.onLabelAdded(label);
                    labelsGroup.addView(createChipFromLabel(label));
                    labelsGroup.setVisibility(View.VISIBLE);
                }

                labels.setText("");
            });
        } else {
            labels.setEnabled(false);
        }
        if (fullCard.getLabels() != null && fullCard.getLabels().size() > 0) {
            for (Label label : fullCard.getLabels()) {
                labelsGroup.addView(createChipFromLabel(label));
            }
            labelsGroup.setVisibility(View.VISIBLE);
        } else {
            labelsGroup.setVisibility(View.INVISIBLE);
        }
    }


    private Chip createChipFromLabel(Label label) {
        Chip chip = new Chip(activity);
        chip.setText(label.getTitle());
        if (canEdit) {
            chip.setCloseIcon(closeCircleDrawable);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                labelsGroup.removeView(chip);
                cardDetailsListener.onLabelRemoved(label);
            });
        }
        try {
            int labelColor = Color.parseColor("#" + label.getColor());
            ColorStateList c = ColorStateList.valueOf(labelColor);
            chip.setChipBackgroundColor(c);
            int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
            chip.setTextColor(color);

            if (chip.getCloseIcon() != null) {
                Drawable wrapDrawable = DrawableCompat.wrap(chip.getCloseIcon());
                DrawableCompat.setTint(wrapDrawable, ColorUtils.setAlphaComponent(color, 150));
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "error parsing label color", e);
        }
        return chip;
    }

    private void setupPeople(long accountId, long boardId) {
        if (canEdit) {
            Long localCardId = fullCard.getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            people.setAdapter(new UserAutoCompleteAdapter(this, activity, accountId, boardId, localCardId));
            people.setOnItemClickListener((adapterView, view, position, id) -> {
                User user = (User) adapterView.getItemAtPosition(position);
                cardDetailsListener.onUserAdded(user);
                if (baseUrl != null) {
                    addAvatar(baseUrl, user);
                }
                people.setText("");
            });

            if (this.fullCard.getAssignedUsers() != null) {
                peopleList.removeAllViews();
                if (baseUrl != null) {
                    for (User user : this.fullCard.getAssignedUsers()) {
                        addAvatar(baseUrl, user);
                    }
                }
            }
        } else {
            people.setEnabled(false);
        }
    }

    private void addAvatar(String baseUrl, User user) {
        ImageView avatar = new ImageView(activity);
        avatar.setLayoutParams(avatarLayoutParams);
        if (canEdit) {
            avatar.setOnClickListener(v -> {
                cardDetailsListener.onUserRemoved(user);
                peopleList.removeView(avatar);
                Snackbar.make(
                        Objects.requireNonNull(getView()), getString(R.string.unassigned_user, user.getDisplayname()),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_undo, v1 -> {
                            cardDetailsListener.onUserAdded(user);
                            addAvatar(baseUrl, user);
                        }).show();
            });
        }
        peopleList.addView(avatar);
        avatar.requestLayout();
        ViewUtil.addAvatar(getContext(), avatar, baseUrl, user.getUid(), avatarSize, R.drawable.ic_person_grey600_24dp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        int hourOfDay;
        int minute;

        if (dueDateTime.getText() != null && dueDateTime.length() > 0) {
            hourOfDay = this.fullCard.getCard().getDueDate().getHours();
            minute = this.fullCard.getCard().getDueDate().getMinutes();
        } else {
            hourOfDay = 0;
            minute = 0;
        }

        c.set(year, month, dayOfMonth, hourOfDay, minute);
        this.fullCard.getCard().setDueDate(c.getTime());
        dueDate.setText(dateFormat.format(c.getTime()));
        cardDetailsListener.onDueDateChanged(fullCard.card.getDueDate());

        if (this.fullCard.getCard().getDueDate() == null || this.fullCard.getCard().getDueDate().getTime() == 0) {
            clearDueDate.setVisibility(View.GONE);
        } else {
            clearDueDate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (this.fullCard.getCard().getDueDate() == null) {
            this.fullCard.getCard().setDueDate(new Date());
        }
        this.fullCard.getCard().getDueDate().setHours(hourOfDay);
        this.fullCard.getCard().getDueDate().setMinutes(minute);
        dueDateTime.setText(dueTime.format(this.fullCard.getCard().getDueDate().getTime()));
        cardDetailsListener.onDueDateChanged(fullCard.card.getDueDate());
        if (this.fullCard.getCard().getDueDate() == null || this.fullCard.getCard().getDueDate().getTime() == 0) {
            clearDueDate.setVisibility(View.GONE);
        } else {
            clearDueDate.setVisibility(View.VISIBLE);
        }
    }

    public interface CardDetailsListener {

        void onDescriptionChanged(String toString);

        void onDueDateChanged(Date dueDate);

        void onUserAdded(User user);

        void onUserRemoved(User user);

        void onLabelRemoved(Label label);

        void onLabelAdded(Label createdLabel);
    }
}
