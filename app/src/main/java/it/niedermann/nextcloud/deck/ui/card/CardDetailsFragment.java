package it.niedermann.nextcloud.deck.ui.card;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.viewmodel.FullCardViewModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.widget.DelayedAutoCompleteTextView;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = CardDetailsFragment.class.getCanonicalName();

    private FullCardViewModel fullCardViewModel;
    private FullCard card;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private String baseUrl;
    private int avatarSize;
    private LinearLayout.LayoutParams avatarLayoutParams;
    private Unbinder unbinder;

    @BindView(R.id.people)
    DelayedAutoCompleteTextView people;

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

    public static CardDetailsFragment newInstance(long accountId, long localId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);

        CardDetailsFragment fragment = new CardDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        fullCardViewModel = ViewModelProviders.of(this)
                .get(FullCardViewModel.class);


        FragmentCardEditTabDetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_edit_tab_details, container, false);

        binding.setLifecycleOwner(this);
        binding.setEditmodel(fullCardViewModel);

        unbinder = ButterKnife.bind(this, binding.getRoot());
        dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);

            setupView(accountId, localId);
        }

        avatarSize = DimensionUtil.getAvatarDimension(getContext());
        avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, DimensionUtil.dpToPx(getContext(), 8), 0);

        try {
            baseUrl = syncManager.getServerUrl();
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        return binding.getRoot();
    }

    private void setupView(long accountId, long localId) {
        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());

        this.fullCardViewModel.fullCard = syncManager.getCardByLocalId(accountId, localId);
        this.fullCardViewModel.fullCard.observe(CardDetailsFragment.this, (FullCard card) -> {
            this.card = card;
            if (this.card != null) {
                // people
                setupPeople(accountId);

                // labels
                setupLabels();

                // due date
                setupDueDate();
            }
        });

        dueDate.setOnClickListener(v -> {
            createDatePickerDialogFromDate(getActivity(), this, card.getCard().getDueDate()).show();
        });

        dueDateTime.setOnClickListener(v -> {
            createTimePickerDialogFromDate(getActivity(), this, card.getCard().getDueDate()).show();
        });

        clearDueDate.setOnClickListener(v -> {
            this.card.getCard().setDueDate(null);
            syncManager.updateCard(this.card.getCard());
        });
    }

    private TimePickerDialog createTimePickerDialogFromDate(
            @NonNull Context context,
            @Nullable TimePickerDialog.OnTimeSetListener listener,
            Date date
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
            Date date
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
        if (this.card.getCard().getDueDate() != null) {
            dueDate.setText(dateFormat.format(this.card.getCard().getDueDate()));
            dueDateTime.setText(dueTime.format(this.card.getCard().getDueDate()));
            clearDueDate.setVisibility(View.VISIBLE);
        } else {
            clearDueDate.setVisibility(View.INVISIBLE);
            dueDate.setText(null);
            dueDateTime.setText(null);
        }
    }

    private void setupLabels() {
        labelsGroup.removeAllViews();
        if (card.getLabels() != null && card.getLabels().size() > 0) {
            for (Label label : card.getLabels()) {
                final Chip chip = createChipFromLabel(label);
                chip.setOnCloseIconClickListener(v -> {
                    labelsGroup.removeView(chip);
                    syncManager.unassignLabelToCard(label, card.getCard());
                });
                labelsGroup.addView(chip);
            }
            labelsGroup.setVisibility(View.VISIBLE);
        } else {
            labelsGroup.setVisibility(View.INVISIBLE);
        }
    }

    private Chip createChipFromLabel(Label label) {
        Chip chip = new Chip(getActivity());
        chip.setText(label.getTitle());
        chip.setCloseIcon(getContext().getResources().getDrawable(R.drawable.ic_close_circle_grey600));
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
        people.setAdapter(new UserAutoCompleteAdapter(this, getContext(), accountId));
        people.setOnItemClickListener((adapterView, view, position, id) -> {
            User user = (User) adapterView.getItemAtPosition(position);

            syncManager.assignUserToCard(user.getLocalId(), card.getCard());

            if (baseUrl != null) {
                addAvatar(baseUrl, user);
            }
            people.setText("");
        });

        if (this.card.getAssignedUsers() != null) {
            peopleList.removeAllViews();
            if (baseUrl != null) {
                for (User user : this.card.getAssignedUsers()) {
                    addAvatar(baseUrl, user);
                }
            }
        }
    }

    private void addAvatar(String baseUrl, User user) {
        ImageView avatar = new ImageView(getActivity());
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
            hourOfDay = this.card.getCard().getDueDate().getHours();
            minute = this.card.getCard().getDueDate().getMinutes();
        } else {
            clearDueDate.setVisibility(View.GONE);
            hourOfDay = 0;
            minute = 0;
        }

        c.set(year, month, dayOfMonth, hourOfDay, minute);
        this.card.getCard().setDueDate(c.getTime());
        dueDate.setText(dateFormat.format(c.getTime()));
        syncManager.updateCard(this.card.getCard());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.card.getCard().getDueDate().setHours(hourOfDay);
        this.card.getCard().getDueDate().setMinutes(minute);
        dueDateTime.setText(dueTime.format(this.card.getCard().getDueDate().getTime()));
        syncManager.updateCard(this.card.getCard());
    }
}
