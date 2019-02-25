package it.niedermann.nextcloud.deck.ui.card;

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
import android.widget.EditText;

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

public class CardDetailsFragment extends Fragment {
    private static final String TAG = CardDetailsFragment.class.getCanonicalName();

    FullCard card;
    SyncManager syncManager;

    private Unbinder unbinder;

    @BindView(R.id.dueDate)
    EditText dueDate;

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
                        if (this.card.getCard().getDueDate() != null) {
                            dueDate.setText(
                                    SupportUtil.getRelativeDateTimeString(
                                            getActivity(),
                                            this.card.getCard().getDueDate().getTime())
                            );
                        }

                    }
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
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
