package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final String TAG = CardAttachmentsFragment.class.getCanonicalName();

    private static final int REQUEST_CODE_ADD_ATTACHMENT = 1;

    private SyncManager syncManager;

    private long accountId;
    private long cardId;

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
            accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            cardId = args.getLong(BUNDLE_KEY_LOCAL_ID);

            syncManager = new SyncManager(Objects.requireNonNull(getActivity()));
            observeOnce(syncManager.getCardByLocalId(accountId, cardId), CardAttachmentsFragment.this, (fullCard) -> {
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                fab.setOnClickListener(v -> {
                    Snackbar.make(coordinatorLayout, "Adding attachments is not yet implemented", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
//                    startActivityForResult(intent, REQUEST_CODE_ADD_ATTACHMENT);
                });
                fab.show();
                attachmentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0)
                            fab.hide();
                        else if (dy < 0)
                            fab.show();
                    }
                });
            }
        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                syncManager.addAttachmentToCard(accountId, cardId, uri);
            }

        }
    }

    public CardAttachmentsFragment() {
    }

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
