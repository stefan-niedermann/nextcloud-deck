package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class AttachmentsActivity extends AppCompatActivity {

    private ActivityAttachmentsBinding binding;

    public static final String BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID = "currentAttachmenLocaltId";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityAttachmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalArgumentException("Provide localId");
        }
        long accountId = extras.getLong(BUNDLE_KEY_ACCOUNT_ID);
        long cardLocalId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
        long currentAttachment = extras.getLong(BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID);

        SyncManager syncManager = new SyncManager(this);

        syncManager.readAccount(accountId).observe(this, account ->
                syncManager.getCardByLocalId(accountId, cardLocalId).observe(this, fullCard -> {
                    binding.toolbar.setSubtitle(fullCard.getCard().getTitle());
                    List<Attachment> attachments = fullCard.getAttachments();
                    if (fullCard.getAttachments().size() == 0) {
                        DeckLog.logError(new IllegalStateException(AttachmentsActivity.class.getSimpleName() + " called, but card " + fullCard.getLocalId() + "has no attachments"));
                        finish();
                    } else {
                        if (currentAttachment != 0L) {
                            for (Attachment a : attachments) {
                                if (a.getLocalId() == currentAttachment) {
                                    Glide.with(this)
                                            .load(AttachmentUtil.getUrl(account.getUrl(), fullCard.getId(), a.getId()))
                                            .into(binding.image);
                                    binding.toolbar.setTitle(a.getBasename());
                                    break;
                                }
                            }
                        } else {
                            DeckLog.logError(new IllegalStateException("No " + BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID + " was provided. Falling back to displaying first image."));
                            Glide.with(this)
                                    .load(AttachmentUtil.getUrl(account.getUrl(), fullCard.getId(), attachments.get(0).getId()))
                                    .into(binding.image);
                            binding.toolbar.setTitle(attachments.get(0).getBasename());
                        }
                    }
                }));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }
}
