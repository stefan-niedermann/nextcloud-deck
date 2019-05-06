package it.niedermann.nextcloud.deck.ui.card;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardAttachmentsFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.attachments_list)
    LinearLayout attachmentsList;
    @BindView(R.id.no_attachments)
    LinearLayout noAttachments;

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
            long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);

            setupView(accountId, localId, boardId);
        }

        return view;
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

    private void setupView(long accountId, long localId, long boardId) {
        SyncManager syncManager = new SyncManager(Objects.requireNonNull(getActivity()).getApplicationContext(), getActivity());
        syncManager.getCardByLocalId(accountId, localId).observe(CardAttachmentsFragment.this, (fullCard) -> {
            if (fullCard.getAttachments().size() == 0) {
                this.noAttachments.setVisibility(View.VISIBLE);
                this.attachmentsList.setVisibility(View.GONE);
            } else {
                this.noAttachments.setVisibility(View.GONE);
                this.attachmentsList.setVisibility(View.VISIBLE);
                syncManager.readAccount(accountId).observe(CardAttachmentsFragment.this, (Account account) -> {
                    for (Attachment a : fullCard.getAttachments()) {
                        View v = getLayoutInflater().inflate(R.layout.fragment_card_edit_tab_attachment, null);
                        ((TextView) v.findViewById(R.id.filename)).setText(a.getFilename());
                        ((TextView) v.findViewById(R.id.filesize)).setText(Formatter.formatFileSize(getContext(), a.getFilesize()));
                        ((TextView) v.findViewById(R.id.modified)).setText(DateUtils.getRelativeTimeSpanString(getContext(), a.getLastModified().getTime()));
                        this.attachmentsList.addView(v);
                        v.setOnClickListener((event) -> {
                            Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                            openURL.setData(Uri.parse(account.getUrl() + "/index.php/apps/deck/cards/" + a.getCardId() + "/attachment/" + a.getId()));
                            startActivity(openURL);
                        });
                    }
                });
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
