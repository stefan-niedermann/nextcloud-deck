package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.attachments.AttachmentAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

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
                        supportFinishAfterTransition();
                        return;
                    }
                    RecyclerView.Adapter adapter = new AttachmentAdapter(account, fullCard.getId(), attachments);
                    binding.viewPager.setAdapter(adapter);
                    if (currentAttachment != 0L) {
                        for (int i = 0; i < attachments.size(); i++) {
                            if (attachments.get(i).getLocalId() == currentAttachment) {
                                binding.viewPager.setCurrentItem(i, false);
                                break;
                            }
                        }
                    }
                    // TODO
                    // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html?m=1
                    // https://github.com/android/animation-samples/blob/master/GridToPager/app/src/main/java/com/google/samples/gridtopager/fragment/ImagePagerFragment.java
//                    setEnterSharedElementCallback(new SharedElementCallback() {
//                        @Override
//                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                            View view = binding.viewPager.getRootView();
//                            if (view == null) {
//                                return;
//                            }
//                            // Map the first shared element name to the child ImageView.
//                            sharedElements.put(names.get(0), view.findViewById(R.id.image));
//                            Log.v("SHARED", "names" + names);
//                        }
//                    });
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
