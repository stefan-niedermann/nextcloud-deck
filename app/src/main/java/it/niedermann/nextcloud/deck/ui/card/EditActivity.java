package it.niedermann.nextcloud.deck.ui.card;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.CardUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends BrandedActivity {

    private ActivityEditBinding binding;
    private EditCardViewModel viewModel;
    private SyncManager syncManager;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        final Bundle args = getIntent().getExtras();

        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT_ID) || !args.containsKey(BUNDLE_KEY_BOARD_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT_ID + " and " + BUNDLE_KEY_BOARD_ID + " of the card that should be edited or created.");
        }

        long localId = args.getLong(BUNDLE_KEY_LOCAL_ID, NO_LOCAL_ID);

        viewModel = new ViewModelProvider(this).get(EditCardViewModel.class);
        syncManager = new SyncManager(this);

        if (localId == NO_LOCAL_ID) {
            viewModel.setCreateMode(true);
            if (!args.containsKey(BUNDLE_KEY_STACK_ID)) {
                throw new IllegalArgumentException("When creating a card, passing the " + BUNDLE_KEY_STACK_ID + " is mandatory");
            }
        }

        long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
        long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);

        observeOnce(syncManager.getFullBoardById(accountId, boardId), EditActivity.this, (fullBoard -> {
            viewModel.setCanEdit(fullBoard.getBoard().isPermissionEdit());
            invalidateOptionsMenu();
            if (viewModel.isCreateMode()) {
                viewModel.initializeNewCard(accountId, boardId, args.getLong(BUNDLE_KEY_STACK_ID));
                setupViewPager();
                setupTitle(viewModel.isCreateMode());
            } else {
                observeOnce(syncManager.getCardByLocalId(accountId, localId), EditActivity.this, (next) -> {
                    viewModel.initializeExistingCard(accountId, boardId, next);
                    setupViewPager();
                    setupTitle(viewModel.isCreateMode());
                });
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewModel.canEdit()) {
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
        if (!viewModel.isPendingCreation()) {
            viewModel.setPendingCreation(true);
            final String title = viewModel.getFullCard().getCard().getTitle();
            if (title == null || title.isEmpty()) {
                viewModel.getFullCard().getCard().setTitle(CardUtil.generateTitleFromDescription(viewModel.getFullCard().getCard().getDescription()));
            }
            if (viewModel.getFullCard().getCard().getTitle().isEmpty()) {
                new BrandedAlertDialogBuilder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> viewModel.setPendingCreation(false))
                        .show();
            } else {
                if (viewModel.isCreateMode()) {
                    observeOnce(syncManager.createFullCard(viewModel.getAccountId(), viewModel.getBoardId(), viewModel.getFullCard().getCard().getStackId(), viewModel.getFullCard()), EditActivity.this, (card) -> super.finish());
                } else {
                    observeOnce(syncManager.updateCard(viewModel.getFullCard()), EditActivity.this, (card) -> super.finish());
                }
            }
        }
    }

    private void setupViewPager() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), getLifecycle());
        final TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            tab.setIcon(viewModel.hasCommentsAbility()
                    ? tabIconsWithComments[position]
                    : tabIcons[position]
            );
            tab.setContentDescription(viewModel.hasCommentsAbility()
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
        if (!viewModel.isCreateMode()) {
            syncManager.readAccount(viewModel.getAccountId()).observe(this, (account) -> {
                viewModel.setHasCommentsAbility(account.getServerDeckVersionAsObject().isGreaterOrEqualTo(new Version("1.0.0", 1, 0, 0)));
                if (viewModel.hasCommentsAbility()) {
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
        binding.title.setText(viewModel.getFullCard().getCard().getTitle());
        if (viewModel.canEdit()) {
            if (createMode) {
                binding.title.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                if (viewModel.getFullCard().getCard().getTitle() != null) {
                    binding.title.setSelection(viewModel.getFullCard().getCard().getTitle().length());
                }
            }
            binding.title.setHint(getString(createMode ? R.string.simple_add : R.string.edit));
            binding.title.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewModel.getFullCard().getCard().setTitle(binding.title.getText().toString());
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
        if (!viewModel.hasChanges() && viewModel.canEdit()) {
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
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
        applyBrandToPrimaryTabLayout(mainColor, textColor, binding.tabLayout);
        final int highlightColor = Color.argb(77, Color.red(textColor), Color.green(textColor), Color.blue(textColor));
        binding.title.setHighlightColor(highlightColor);
        binding.title.setTextColor(textColor);
//        DrawableCompat.setTintList(binding.title.getBackground(), ColorStateList.valueOf(textColor));
    }
}
