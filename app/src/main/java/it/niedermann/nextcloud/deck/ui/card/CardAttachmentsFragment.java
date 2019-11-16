package it.niedermann.nextcloud.deck.ui.card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardAttachmentsFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.attachments_list)
    RecyclerView attachmentsList;
    @BindView(R.id.no_attachments)
    RelativeLayout noAttachments;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_edit_tab_attachments, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);

            SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
            observeOnce(syncManager.getCardByLocalId(accountId, localId), CardAttachmentsFragment.this, (fullCard) -> {
                if (fullCard.getAttachments().size() == 0) {
                    this.noAttachments.setVisibility(View.VISIBLE);
                    this.attachmentsList.setVisibility(View.GONE);
                } else {
                    this.noAttachments.setVisibility(View.GONE);
                    this.attachmentsList.setVisibility(View.VISIBLE);
                    syncManager.readAccount(accountId).observe(CardAttachmentsFragment.this, (Account account) -> {
                        RecyclerView.Adapter adapter = new AttachmentAdapter(account, fullCard.getCard().getId(), fullCard.getAttachments());
                        attachmentsList.setAdapter(adapter);
                    });
                }
            });
        }

        fab.setOnClickListener(v -> {
            Snackbar.make(coordinatorLayout, "Adding attachments is not yet implemented", Snackbar.LENGTH_LONG).show();
        });

        return view;
    }

    public CardAttachmentsFragment() {}

    public static CardAttachmentsFragment newInstance(long accountId, long localId, long boardId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);

        CardAttachmentsFragment fragment = new CardAttachmentsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
