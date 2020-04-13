package it.niedermann.nextcloud.deck.ui.card;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.card.attachments.NewCardAttachmentHandler;
import it.niedermann.nextcloud.deck.ui.card.comments.CommentAddedListener;
import it.niedermann.nextcloud.deck.ui.card.comments.CommentDeletedListener;
import it.niedermann.nextcloud.deck.ui.card.details.CardDetailsListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.CardUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends BrandedActivity implements CardDetailsListener, CommentAddedListener, CommentDeletedListener, NewCardAttachmentHandler {

    private ActivityEditBinding binding;
    private SyncManager syncManager;
    boolean hasCommentsAbility = false;

    private static final int[] tabTitles = new int[]{
            R.string.card_edit_details,
            R.string.card_edit_attachments,
            R.string.card_edit_activity
    };

    private static final int[] tabTitlesWithComments = new int[]{
            R.string.card_edit_details,
            R.string.card_edit_attachments,
            R.string.card_edit_comments,
            R.string.card_edit_activity
    };

    private static final int[] tabIcons = new int[]{
            R.drawable.ic_home_grey600_24dp,
            R.drawable.ic_attach_file_grey600_24dp,
            R.drawable.ic_activity_light_grey
    };

    private static final int[] tabIconsWithComments = new int[]{
            R.drawable.ic_home_grey600_24dp,
            R.drawable.ic_attach_file_grey600_24dp,
            R.drawable.type_comment_grey600_36dp,
            R.drawable.ic_activity_light_grey
    };

    private FullCard originalCard;
    private FullCard fullCard;

    private long accountId;
    private long boardId;
    private long stackId;
    private long localId;

    private boolean pendingCreation = false;
    private boolean canEdit;
    private boolean createMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Bundle args = getIntent().getExtras();

        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT_ID) || !args.containsKey(BUNDLE_KEY_BOARD_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT_ID + " and " + BUNDLE_KEY_BOARD_ID + " so we know where to create this new card.");
        }

        accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
        boardId = args.getLong(BUNDLE_KEY_BOARD_ID);
        localId = args.getLong(BUNDLE_KEY_LOCAL_ID, NO_LOCAL_ID);

        if (localId == NO_LOCAL_ID) {
            createMode = true;
            if (args.containsKey(BUNDLE_KEY_STACK_ID)) {
                stackId = args.getLong(BUNDLE_KEY_STACK_ID);
            } else {
                throw new IllegalArgumentException("When creating a card, passing the " + BUNDLE_KEY_STACK_ID + " is mandatory");
            }
        }

        syncManager = new SyncManager(this);

        observeOnce(syncManager.getFullBoardById(accountId, boardId), EditActivity.this, (fullBoard -> {
            canEdit = fullBoard.getBoard().isPermissionEdit();
            invalidateOptionsMenu();
            if (createMode) {
                fullCard = new FullCard();
                originalCard = new FullCard();
                fullCard.setLabels(new ArrayList<>());
                fullCard.setAssignedUsers(new ArrayList<>());
                fullCard.setAttachments(new ArrayList<>());
                Card card = new Card();
                card.setStackId(stackId);
                fullCard.setCard(card);
                setupViewPager();
                setupTitle(createMode);
            } else {
                observeOnce(syncManager.getCardByLocalId(accountId, localId), EditActivity.this, (next) -> {
                    fullCard = next;
                    originalCard = new FullCard(fullCard);
                    setupViewPager();
                    setupTitle(createMode);
                });
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (canEdit) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.card_edit_menu, menu);
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_card_save) {
            saveAndFinish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndFinish() {
        if (!pendingCreation) {
            pendingCreation = true;
            if (fullCard.getCard().getTitle() == null || fullCard.getCard().getTitle().isEmpty()) {
                fullCard.getCard().setTitle(CardUtil.generateTitleFromDescription(fullCard.getCard().getDescription()));
            }
            if (fullCard.getCard().getTitle().isEmpty()) {
                new BrandedAlertDialogBuilder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> pendingCreation = false)
                        .show();
            } else {
                if (createMode) {
                    observeOnce(syncManager.createFullCard(accountId, boardId, stackId, fullCard), EditActivity.this, (card) -> super.finish());
                } else {
                    observeOnce(syncManager.updateCard(fullCard), EditActivity.this, (card) -> super.finish());
                }
            }
        }
    }

    private void setupViewPager() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        CardTabAdapter adapter = new CardTabAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                accountId,
                localId,
                boardId,
                canEdit);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            tab.setIcon(
                    hasCommentsAbility
                            ? tabIconsWithComments[position]
                            : tabIcons[position]
            );
            tab.setContentDescription(
                    hasCommentsAbility
                            ? tabTitlesWithComments[position]
                            : tabTitles[position]
            );
        });
        runOnUiThread(() -> {
            binding.pager.setOffscreenPageLimit(2);
            binding.pager.setAdapter(adapter);
            mediator.attach();
        });

        // Comments API only available starting with version 1.0.0-alpha1
        if (!createMode) {
            syncManager.readAccount(accountId).observe(this, (account) -> {
                hasCommentsAbility = ((account.getServerDeckVersionAsObject().compareTo(new Version("1.0.0", 1, 0, 0)) >= 0));
                if (hasCommentsAbility) {
                    runOnUiThread(() -> {
                        mediator.detach();
                        adapter.enableComments();
                        binding.pager.setOffscreenPageLimit(3);
                        mediator.attach();
                    });
                }
            });
        }
    }

    private void setupTitle(boolean createMode) {
        binding.title.setText(fullCard.getCard().getTitle());
        if (canEdit) {
            if (createMode) {
                binding.title.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                if (fullCard.getCard().getTitle() != null) {
                    binding.title.setSelection(fullCard.getCard().getTitle().length());
                }
            }
            binding.title.setHint(getString(createMode ? R.string.simple_add : R.string.edit));
            binding.title.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fullCard.getCard().setTitle(binding.title.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
//            binding.titleTextInputLayout.setHintEnabled(false);
            binding.title.setEnabled(false);
        }
    }

    @Override
    public void onDescriptionChanged(String description) {
        this.fullCard.getCard().setDescription(description);
    }

    @Override
    public void onUserAdded(User user) {
        this.fullCard.getAssignedUsers().add(user);
    }

    @Override
    public void onUserRemoved(User user) {
        this.fullCard.getAssignedUsers().remove(user);
    }

    @Override
    public void onLabelAdded(Label label) {
        this.fullCard.getLabels().add(label);
    }

    @Override
    public void onLabelRemoved(Label label) {
        this.fullCard.getLabels().remove(label);
    }

    @Override
    public void onDueDateChanged(Date dueDate) {
        this.fullCard.getCard().setDueDate(dueDate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        if (!fullCard.equals(originalCard) && canEdit) {
            new BrandedAlertDialogBuilder(this)
                    .setTitle(R.string.simple_save)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.simple_save, (dialog, whichButton) -> saveAndFinish())
                    .setNegativeButton(R.string.simple_discard, (dialog, whichButton) -> super.finish()).show();
        } else {
            super.finish();
        }
    }

    @Override
    public void onCommentAdded(DeckComment comment) {
        syncManager.addCommentToCard(accountId, boardId, localId, comment);
    }

    @Override
    public void attachmentAdded(Attachment attachment) {
        fullCard.getAttachments().add(attachment);
    }

    @Override
    public void attachmentRemoved(Attachment attachment) {
        fullCard.getAttachments().remove(attachment);
    }

    @Override
    public void onCommentDeleted(Long localCommentId) {
        syncManager.deleteComment(this.accountId, this.localId, localCommentId);
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        super.applyBrand(mainColor, textColor);
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
        applyBrandToPrimaryTabLayout(mainColor, textColor, binding.tabLayout);
        applyBrandToTitle(textColor, binding.title);
    }

    private static void applyBrandToTitle(@ColorInt int textColor, @NonNull EditText editText) {
        final int highlightColor = Color.argb(77, Color.red(textColor), Color.green(textColor), Color.blue(textColor));
        editText.setHighlightColor(highlightColor);
        editText.setTextColor(textColor);
        DrawableCompat.setTintList(editText.getBackground(), ColorStateList.valueOf(textColor));

        final Drawable background = editText.getBackground();
        final ColorFilter oldColorFilter = DrawableCompat.getColorFilter(background);
        final View.OnFocusChangeListener oldOnFocusChangeListener = editText.getOnFocusChangeListener();

        final boolean isFocused = editText.isFocused();
        if (isFocused) {
            editText.clearFocus();
        }
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHintTextColor(textColor);
                editText.setTextColor(textColor);
                background.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                background.setColorFilter(oldColorFilter);
            }
            if (oldOnFocusChangeListener != null) {
                oldOnFocusChangeListener.onFocusChange(v, hasFocus);
            }
        });
        if (isFocused) {
            editText.requestFocus();
        }
    }
}
