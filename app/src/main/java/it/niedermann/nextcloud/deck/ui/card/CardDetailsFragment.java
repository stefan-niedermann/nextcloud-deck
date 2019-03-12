package it.niedermann.nextcloud.deck.ui.card;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.SupportUtil;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.viewmodel.FullCardViewModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.widget.DelayedAutoCompleteTextView;

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
        //dueTime = android.text.format.DateFormat.getTimeFormat(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);

            setupView(accountId, localId);
        }

        return binding.getRoot();
    }

    private void setupView(long accountId, long localId) {
        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());

        this.fullCardViewModel.fullCard = syncManager.getCardByLocalId(accountId, localId);
        this.fullCardViewModel.fullCard.observe(CardDetailsFragment.this, (FullCard card) -> {
            // TODO read/set available card details data
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

    private void setupDueDate() {
        if (this.card.getCard().getDueDate() != null) {
            dueDate.setText(dateFormat.format(this.card.getCard().getDueDate()));
            dueDateTime.setText(dueTime.format(this.card.getCard().getDueDate()));
        } else {
            dueDate.setText(null);
            dueDateTime.setText(null);
        }
    }

    private void setupLabels() {
        labelsGroup.removeAllViews();
        if (this.card.getLabels() != null && this.card.getLabels().size() > 0) {
            Chip chip;
            for (Label label : this.card.getLabels()) {
                chip = new Chip(getActivity());
                chip.setText(label.getTitle());
                // TODO use grey/white icon depending on textTinting
                chip.setCloseIcon(getContext().getResources().getDrawable(R.drawable.ic_close_circle_grey600));
                chip.setCloseIconVisible(true);
                try {
                    int labelColor = Color.parseColor("#" + label.getColor());
                    ColorStateList c = ColorStateList.valueOf(labelColor);
                    chip.setChipBackgroundColor(c);
                    int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
                    chip.setTextColor(color);

                    Drawable wrapDrawable = DrawableCompat.wrap(chip.getCloseIcon());
                    DrawableCompat.setTint(wrapDrawable, ColorUtils.setAlphaComponent(color, 150));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "error parsing label color", e);
                }

                labelsGroup.addView(chip);
            }
            labelsGroup.setVisibility(View.VISIBLE);
        } else {
            labelsGroup.setVisibility(View.GONE);
        }
    }

    private void setupPeople(long accountId) {
        people.setThreshold(2);
        people.setAdapter(new UserAutoCompleteAdapter(this, getContext(), accountId));
        people.setOnItemClickListener((adapterView, view, position, id) -> {
            User user = (User) adapterView.getItemAtPosition(position);
            people.setText(user.getDisplayname());
            // TODO: store chosen user, trigger avatar display/fetch
        });

        // TODO implement proper people display + avatar fetching
        // TODO find out how to get the server's Nextcloud URL to build the avatar URL
        if (this.card.getAssignedUsers() != null) {
            try {
                // TODO FIX: NullPointerException!
                // syncManager.getServerUrl()

                //Workaround
                SingleSignOnAccount account = SingleAccountHelper.getCurrentSingleSignOnAccount(getContext());
                ImageView avatar;
                String baseUrl = account.url;
                int px = SupportUtil.getAvatarDimension(getContext());
                int margin = SupportUtil.dpToPx(getContext(), 8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
                params.setMargins(
                        0, 0, margin, 0);
                peopleList.removeAllViews();
                for (User user : this.card.getAssignedUsers()) {
                    avatar = new ImageView(getActivity());
                    avatar.setLayoutParams(params);
                    String uri = baseUrl + "/index.php/avatar/" + Uri.encode(user.getUid()) + "/" + px;
                    peopleList.addView(avatar);
                    avatar.requestLayout();
                    Glide.with(getContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(avatar);
                }
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                DeckLog.logError(e);
            } catch (NoCurrentAccountSelectedException e) {
                DeckLog.logError(e);
            }
        }
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
