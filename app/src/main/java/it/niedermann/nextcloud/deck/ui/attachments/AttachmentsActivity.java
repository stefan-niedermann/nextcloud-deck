package it.niedermann.nextcloud.deck.ui.attachments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

public class AttachmentsActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final String BUNDLE_KEY_CARD_ID = "cardId";
    private static final String BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID = "currentAttachmenLocaltId";

    private ActivityAttachmentsBinding binding;
    private ViewPager2.OnPageChangeCallback onPageChangeCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityAttachmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        supportPostponeEnterTransition();

        setSupportActionBar(binding.toolbar);
        final Drawable navigationIcon = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        DrawableCompat.setTint(navigationIcon, ContextCompat.getColor(this, android.R.color.white));
        binding.toolbar.setNavigationIcon(navigationIcon);

        final Bundle args = getIntent().getExtras();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_CARD_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT + " and " + BUNDLE_KEY_CARD_ID);
        }

        final Account account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);

        if (account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }

        long cardId = args.getLong(BUNDLE_KEY_CARD_ID);

        final SyncManager syncManager = new SyncManager(this);
        syncManager.getFullCardWithProjectsByLocalId(account.getId(), cardId).observe(this, fullCard -> {
            final List<Attachment> attachments = new ArrayList<>();
            for (Attachment a : fullCard.getAttachments()) {
                if (MimeTypeUtil.isImage(a.getMimetype())) {
                    attachments.add(a);
                }
            }
            if (fullCard.getAttachments().size() == 0) {
                DeckLog.logError(new IllegalStateException(AttachmentsActivity.class.getSimpleName() + " called, but card " + fullCard.getCard().getTitle() + " has no attachments"));
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
            RecyclerView.Adapter<AttachmentViewHolder> adapter = new AttachmentAdapter(account, fullCard.getId(), attachments);
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback);

            long currentAttachment = args.getLong(BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID);
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
                        sharedElements.put(transitionKey, binding.viewPager.getRootView().findViewById(R.id.avatar)
                        );
                    }

                    // This will move the picture back to the origin, regardless where the ViewPager has been scrolled in the meantime
                    // sharedElements.put(names.get(0), binding.viewPager.getRootView().findViewById(R.id.preview));
                }
            });
        });
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

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account, Long cardLocalId, Long attachmentLocalId) {
        return new Intent(context, AttachmentsActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .putExtra(BUNDLE_KEY_CARD_ID, cardLocalId)
                .putExtra(BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID, attachmentLocalId)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
