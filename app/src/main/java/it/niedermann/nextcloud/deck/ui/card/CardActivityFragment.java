package it.niedermann.nextcloud.deck.ui.card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardActivityFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.activity_list)
    LinearLayout activitiesList;
    @BindView(R.id.no_activities)
    RelativeLayout noActivities;

    public CardActivityFragment() {
    }

    public static CardActivityFragment newInstance(long accountId, long localId, long boardId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);

        CardActivityFragment fragment = new CardActivityFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_edit_tab_activities, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);

            setupView(accountId, localId, boardId);
        }
        return view;
    }

    private void setupView(long accountId, long localId, long boardId) {
        SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
        syncManager.getCardByLocalId(accountId, localId).observe(CardActivityFragment.this, (fullCard) -> {
            syncManager.syncActivitiesForCard(fullCard.getCard()).observe(CardActivityFragment.this, (activities -> {
                if (activities == null || activities.size() == 0) {
                    noActivities.setVisibility(View.VISIBLE);
                    activitiesList.setVisibility(View.GONE);
                } else {
                    noActivities.setVisibility(View.GONE);
                    activitiesList.setVisibility(View.VISIBLE);
                    activitiesList.removeAllViews();
                    for (Activity a : activities) {
                        View v = getLayoutInflater().inflate(R.layout.fragment_card_edit_tab_activity, null);
                        ((TextView) v.findViewById(R.id.date)).setText(DateUtil.getRelativeDateTimeString(getContext(), a.getLastModified().getTime()));
                        ((TextView) v.findViewById(R.id.subject)).setText(a.getSubject());
                        AppCompatImageView type = v.findViewById(R.id.type);
                        switch (ActivityType.findById(a.getType())) {
                            case DECK:
                                break;
                            case CHANGE:
                                type.setImageResource(R.drawable.type_change_36dp);
                                break;
                            case ADD:
                                type.setImageResource(R.drawable.type_add_color_36dp);
                                break;
                            case DELETE:
                                type.setImageResource(R.drawable.type_delete_color_36dp);
                                break;
                            case ARCHIVE:
                                break;
                            case HISTORY:
                                break;
                            case FILES:
                                break;
                            case COMMENT:
                                break;
                        }
                        activitiesList.addView(v);
                    }
                }
            }));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
