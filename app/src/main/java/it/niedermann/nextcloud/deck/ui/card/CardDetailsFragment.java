package it.niedermann.nextcloud.deck.ui.card;

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
import it.niedermann.nextcloud.deck.ui.EditActivity;
import it.niedermann.nextcloud.deck.ui.MarkDownUtil;
import it.niedermann.nextcloud.deck.ui.widget.DelayedAutoCompleteTextView;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;
import rx.Subscriber;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class CardDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = CardDetailsFragment.class.getCanonicalName();

    private boolean createMode;
    private FullCard fullCard;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private String baseUrl;
    private int avatarSize;
    private LinearLayout.LayoutParams avatarLayoutParams;
    private Unbinder unbinder;
    private EditActivity activity;

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

    public static CardDetailsFragment newInstance(long accountId, long localId, long boardId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);

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


        if (Objects.requireNonNull(getActivity()) instanceof EditActivity) {
            activity = (EditActivity) getActivity();
        } else {
            DeckLog.log("activity is not an instance of EditActivity");
        }

        dateFormat = android.text.format.DateFormat.getDateFormat(activity);

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);

            syncManager = new SyncManager(activity);


            createMode = NO_LOCAL_ID.equals(localId);
            if (createMode) {
                fullCard = new FullCard();
                Card card = new Card();
                fullCard.setCard(card);
                setupView(accountId, boardId);
            } else {
                observeOnce(syncManager.getCardByLocalId(accountId, localId), CardDetailsFragment.this, (next) -> {
                    fullCard = next;

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
                    description.setText(fullCard.getCard().getDescription());
                    setupView(accountId, boardId);
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

    private void setupView(long accountId, long boardId) {
        setupPeople(accountId);
        setupLabels(accountId, boardId);
        setupDueDate();
        setupDescriptionListener();
    }

    private void setupDescriptionListener() {
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fullCard != null) {
                    activity.setDescription(description.getText().toString());
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
            activity.setDueDate(null);
            dueDate.setText(null);
            dueDateTime.setText(null);
            fullCard.getCard().setDueDate(null);
        });
    }

    private void setupLabels(long accountId, long boardId) {
        labelsGroup.removeAllViews();
        labels.setAdapter(new LabelAutoCompleteAdapter(this, activity, accountId, boardId));
        labels.setOnItemClickListener((adapterView, view, position, id) -> {
            Label label = (Label) adapterView.getItemAtPosition(position);
            if (LabelAutoCompleteAdapter.CREATE_ID == label.getLocalId()) {
                Label newLabel = new Label(label);
                newLabel.setTitle(((LabelAutoCompleteAdapter) labels.getAdapter()).getLastFilterText());
                newLabel.setLocalId(null);
                if (createMode) {
                    observeOnce(syncManager.createLabel(accountId, newLabel, boardId), CardDetailsFragment.this, createdLabel -> {
                        activity.addLabel(createdLabel);
                    });
                } else {
                    observeOnce(syncManager.createAndAssignLabelToCard(accountId, newLabel, fullCard.getLocalId()), CardDetailsFragment.this, createdLabel -> {
                        addLabelAsChip(createdLabel);
                        labelsGroup.setVisibility(View.VISIBLE);
                    });
                }
            } else {
                if (createMode) {
                    activity.addLabel(label);
                } else {
                    syncManager.assignLabelToCard(label, fullCard.getCard());
                }
                addLabelAsChip(label);
                labelsGroup.setVisibility(View.VISIBLE);
            }

            labels.setText("");
        });
        if (fullCard.getLabels() != null && fullCard.getLabels().size() > 0) {
            for (Label label : fullCard.getLabels()) {
                addLabelAsChip(label);
            }
            labelsGroup.setVisibility(View.VISIBLE);
        } else {
            labelsGroup.setVisibility(View.INVISIBLE);
        }
    }

    private void addLabelAsChip(Label label) {
        Chip chip = createChipFromLabel(label);
        chip.setOnCloseIconClickListener(v -> {
            labelsGroup.removeView(chip);
            if (createMode) {
                activity.removeLabel(label);
            } else {
                syncManager.unassignLabelFromCard(label, fullCard.getCard());
            }
        });
        labelsGroup.addView(chip);
    }

    private Chip createChipFromLabel(Label label) {
        Chip chip = new Chip(activity);
        chip.setText(label.getTitle());
        chip.setCloseIcon(activity.getResources().getDrawable(R.drawable.ic_close_circle_grey600));
        chip.setCloseIconVisible(true);
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

    private void setupPeople(long accountId) {
        people.setThreshold(2);
        people.setAdapter(new UserAutoCompleteAdapter(this, activity, accountId));
        people.setOnItemClickListener((adapterView, view, position, id) -> {
            User user = (User) adapterView.getItemAtPosition(position);

            if (createMode) {
                activity.addUser(user);
            } else {
                syncManager.assignUserToCard(user, fullCard.getCard());
            }

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
    }

    private void addAvatar(String baseUrl, User user) {
        ImageView avatar = new ImageView(activity);
        avatar.setLayoutParams(avatarLayoutParams);
        peopleList.addView(avatar);
        avatar.requestLayout();
        ViewUtil.addAvatar(getContext(), avatar, baseUrl, user.getUid(), avatarSize);
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
            clearDueDate.setVisibility(View.VISIBLE);
            hourOfDay = this.fullCard.getCard().getDueDate().getHours();
            minute = this.fullCard.getCard().getDueDate().getMinutes();
        } else {
            clearDueDate.setVisibility(View.GONE);
            hourOfDay = 0;
            minute = 0;
        }

        c.set(year, month, dayOfMonth, hourOfDay, minute);
        this.fullCard.getCard().setDueDate(c.getTime());
        dueDate.setText(dateFormat.format(c.getTime()));
        activity.setDueDate(fullCard.card.getDueDate());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (this.fullCard.getCard().getDueDate() == null) {
            this.fullCard.getCard().setDueDate(new Date());
        }
        this.fullCard.getCard().getDueDate().setHours(hourOfDay);
        this.fullCard.getCard().getDueDate().setMinutes(minute);
        dueDateTime.setText(dueTime.format(this.fullCard.getCard().getDueDate().getTime()));
        activity.setDueDate(fullCard.card.getDueDate());
    }
}
