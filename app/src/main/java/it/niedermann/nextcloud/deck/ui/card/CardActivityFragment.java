package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.helper.emptycontentview.EmptyContentView;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardActivityFragment extends Fragment {
    private Unbinder unbinder;

    @BindString(R.string.simple_comment)
    String addComment;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.activity_list)
    RecyclerView activitiesList;
    @BindView(R.id.no_activities)
    EmptyContentView emptyContentView;

    public CardActivityFragment() {
    }

    public static CardActivityFragment newInstance(long accountId, long localId, long boardId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardActivityFragment fragment = new CardActivityFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof CommentDialogFragment.AddCommentListener)) {
            throw new ClassCastException("Caller must implement " + CommentDialogFragment.AddCommentListener.class.getCanonicalName());
        }
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
            boolean canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);

            SyncManager syncManager = new SyncManager(requireActivity());
            syncManager.getCardByLocalId(accountId, localId).observe(CardActivityFragment.this, (fullCard) -> {
                syncManager.syncActivitiesForCard(fullCard.getCard()).observe(CardActivityFragment.this, (activities -> {
                    if (activities == null || activities.size() == 0) {
                        emptyContentView.setVisibility(View.VISIBLE);
                        activitiesList.setVisibility(View.GONE);
                    } else {
                        emptyContentView.setVisibility(View.GONE);
                        activitiesList.setVisibility(View.VISIBLE);
                        RecyclerView.Adapter adapter = new ActivityAdapter(activities);
                        activitiesList.setAdapter(adapter);
                    }
                }));
            });
            if (canEdit) {
                fab.setOnClickListener(v -> {
                    Snackbar.make(coordinatorLayout, "Adding comments is not yet implemented", Snackbar.LENGTH_LONG).show();
//                    CommentDialogFragment.newInstance().show(getActivity().getSupportFragmentManager(), addComment);
                });
                activitiesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0)
                            fab.hide();
                        else if (dy < 0)
                            fab.show();
                    }
                });
            } else {
                emptyContentView.hideDescription();
                fab.hide();
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
