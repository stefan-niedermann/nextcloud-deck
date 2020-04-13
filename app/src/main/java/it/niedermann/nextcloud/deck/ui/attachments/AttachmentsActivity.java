package it.niedermann.nextcloud.deck.ui.attachments;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class AttachmentsActivity extends AppCompatActivity {

    private ActivityAttachmentsBinding binding;

    public static final String BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID = "currentAttachmenLocaltId";

    private ViewPager2.OnPageChangeCallback onPageChangeCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityAttachmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        supportPostponeEnterTransition();

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
                    final List<Attachment> attachments = new ArrayList<>();
                    for (Attachment a : fullCard.getAttachments()) {
                        if (a.getMimetype().startsWith("image")) {
                            attachments.add(a);
                        }
                    }
                    if (fullCard.getAttachments().size() == 0) {
                        DeckLog.logError(new IllegalStateException(AttachmentsActivity.class.getSimpleName() + " called, but card " + fullCard.getLocalId() + "has no attachments"));
                        supportFinishAfterTransition();
                        return;
                    }
                    binding.toolbar.setSubtitle(fullCard.getCard().getTitle());
                    onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            binding.toolbar.setTitle(attachments.get(position).getBasename());
                        }
                    };
                    RecyclerView.Adapter adapter = new AttachmentAdapter(account, fullCard.getId(), attachments);
                    binding.viewPager.setAdapter(adapter);
                    binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback);
                    if (currentAttachment != 0L) {
                        for (int i = 0; i < attachments.size(); i++) {
                            if (attachments.get(i).getLocalId() == currentAttachment) {
                                binding.viewPager.setCurrentItem(i, false);
                                break;
                            }
                        }
                    }

                    // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html?m=1
                    // https://github.com/android/animation-samples/blob/master/GridToPager/app/src/main/java/com/google/samples/gridtopager/fragment/ImagePagerFragment.java
                    setEnterSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                            // TODO Fix shared elements mapping

                            // This does only show an animation if origin and current ViewPager item match
                            long currentAttachmentLocalId = attachments.get(binding.viewPager.getCurrentItem()).getLocalId();
                            String transitionKey = getString(R.string.transition_attachment_preview, String.valueOf(currentAttachmentLocalId));
                            if (transitionKey.equals(names.get(0))) {
                                sharedElements.put(transitionKey, binding.viewPager.getRootView().findViewById(R.id.preview)
                                );
                            }

                            // This will move the picture back to the origin, regardless where the ViewPager has been scrolled in the meantime
                            // sharedElements.put(names.get(0), binding.viewPager.getRootView().findViewById(R.id.preview));
                        }
                    });
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

    @Override
    protected void onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback);
        super.onDestroy();
    }
}
