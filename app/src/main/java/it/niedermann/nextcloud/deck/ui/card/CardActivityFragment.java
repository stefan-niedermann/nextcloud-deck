package it.niedermann.nextcloud.deck.ui.card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardActivityFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.activity_list)
    RecyclerView activitiesList;
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

            SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
            if (syncManager.hasInternetConnection()) {
                syncManager.getCardByLocalId(accountId, localId).observe(CardActivityFragment.this, (fullCard) ->
                        syncManager.syncActivitiesForCard(fullCard.getCard()).observe(CardActivityFragment.this, (activities -> {
                            if (activities == null || activities.size() == 0) {
                                noActivities.setVisibility(View.VISIBLE);
                                activitiesList.setVisibility(View.GONE);
                            } else {
                                noActivities.setVisibility(View.GONE);
                                activitiesList.setVisibility(View.VISIBLE);
                                RecyclerView.Adapter adapter = new ActivityAdapter(activities);
                                activitiesList.setAdapter(adapter);
                            }
                        })));
            }
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
