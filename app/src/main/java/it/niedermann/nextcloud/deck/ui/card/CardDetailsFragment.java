package it.niedermann.nextcloud.deck.ui.card;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.ColorUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.SupportUtil;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = CardDetailsFragment.class.getCanonicalName();

    private FullCard card;
    private SyncManager syncManager;
    private DateFormat dateFormat;
    private DateFormat dueTime = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private Unbinder unbinder;

    @BindView(R.id.dueDateDate)
    TextView dueDate;

    @BindView(R.id.dueDateTime)
    TextView dueDateTime;

    @BindView(R.id.clearDueDate)
    ImageView clearDueDate;

    @BindView(R.id.labelsGroup)
    ChipGroup labelsGroup;

    @BindView(R.id.description)
    EditText description;

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

        View view = inflater.inflate(R.layout.fragment_card_edit_tab_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        //dueTime = android.text.format.DateFormat.getTimeFormat(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);

            setupView(accountId, localId);
        }

        return view;
    }

    private void setupView(long accountId, long localId) {
        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());

        syncManager.getCardByLocalId(accountId, localId)
                .observe(CardDetailsFragment.this, (FullCard card) -> {
                    // TODO read/set available card details data
                    this.card = card;
                    if (this.card != null) {
                        // people
                        // TODO find out how to get the people

                        // labels
                        // TODO load labels
                        labelsGroup.removeAllViews();
                        if (this.card.getLabels() != null && this.card.getLabels().size() > 0) {
                            Chip chip;
                            for (Label label : this.card.getLabels()) {
                                chip = new Chip(getActivity());
                                chip.setText(label.getTitle());
                                chip.setCloseIconResource(R.drawable.ic_close_circle_grey600);

                                try {
                                    int labelColor = Color.parseColor("#" + label.getColor());
                                    ColorStateList c = ColorStateList.valueOf(labelColor);
                                    chip.setChipBackgroundColor(c);
                                    chip.setTextColor(ColorUtil.getForegroundColorForBackgroundColor(labelColor));
                                } catch (IllegalArgumentException e) {
                                    Log.e(TAG, "error parsing label color", e);
                                }

                                labelsGroup.addView(chip);
                            }
                            labelsGroup.setVisibility(View.VISIBLE);
                        } else {
                            labelsGroup.setVisibility(View.GONE);
                        }

                        // due date
                        if (this.card.getCard().getDueDate() != null) {
                            dueDate.setText(dateFormat.format(this.card.getCard().getDueDate()));
                            dueDateTime.setText(dueTime.format(this.card.getCard().getDueDate()));
                        } else {
                            dueDate.setText(null);
                            dueDateTime.setText(null);
                        }

                        // description
                        if (this.card.getCard().getDescription() != null) {
                            description.setText(this.card.getCard().getDescription());
                        }
                    }
                });

        dueDate.setOnClickListener(v -> {
            int year;
            int month;
            int day;

            Calendar cal = Calendar.getInstance();
            if (card.getCard().getDueDate() != null) {
                cal.setTime(card.getCard().getDueDate());
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            } else {
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.show();
        });

        dueDateTime.setOnClickListener(v -> {
            int hourOfDay = 0;
            int minutes = 0;

            if (card.getCard().getDueDate() != null) {
                hourOfDay = card.getCard().getDueDate().getHours();
                minutes = card.getCard().getDueDate().getMinutes();
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getActivity(), this, hourOfDay, minutes, true);
            timePickerDialog.show();
        });

        clearDueDate.setOnClickListener(v -> {
            this.card.getCard().setDueDate(null);
            syncManager.updateCard(this.card.getCard());
        });
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

        if (dueDateTime.getText() != null && dueDateTime.length()>0) {
            hourOfDay = this.card.getCard().getDueDate().getHours();
            minute = this.card.getCard().getDueDate().getMinutes();
        } else {
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
