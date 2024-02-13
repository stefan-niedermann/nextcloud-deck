package it.niedermann.nextcloud.deck.ui.card.details;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.time.Instant;
import java.util.stream.Stream;

import it.niedermann.android.markdown.MarkdownEditor;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabDetailsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.LabelAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeDialog;
import it.niedermann.nextcloud.deck.ui.card.assignee.CardAssigneeListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.ThemedSnackbar;
import okhttp3.Headers;

public class CardDetailsFragment extends Fragment implements CardDueDateView.DueDateChangedListener, CardAssigneeListener {

    private FragmentCardEditTabDetailsBinding binding;
    private EditCardViewModel viewModel;
    private AssigneeAdapter adapter;
    private static final String KEY_ACCOUNT = "account";

    public static Fragment newInstance(@NonNull Account account) {
        final var fragment = new CardDetailsFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        final var args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalStateException(KEY_ACCOUNT + " must be provided");
        }

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardDetailsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        @Px final int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size);
        final var avatarLayoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLayoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.spacer_1x), 0);

        setupAssignees();
        setupLabels((Account) args.getSerializable(KEY_ACCOUNT));
        setupDueDate();
        setupDescription();
        setupProjects();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final var ssoAccount = viewModel.getAccount().getSingleSignOnAccount(requireContext()).orElse(null);

        viewModel.getBoardColor().observe(getViewLifecycleOwner(), color -> {
            applyTheme(color);
            binding.descriptionEditor.setCurrentSingleSignOnAccount(ssoAccount, color);
            binding.descriptionViewer.setCurrentSingleSignOnAccount(ssoAccount, color);
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // https://github.com/wdullaer/MaterialDateTimePicker#why-are-my-callbacks-lost-when-the-device-changes-orientation
//        final var dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag(ThemedDatePickerDialog.class.getCanonicalName());
//        final var tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag(ThemedTimePickerDialog.class.getCanonicalName());
//        if (tpd != null) tpd.setOnTimeSetListener(this);
//        if (dpd != null) dpd.setOnDateSetListener(this);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    private void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        Stream.of(
                binding.labelsWrapper,
                binding.peopleWrapper,
                binding.descriptionEditorWrapper
        ).forEach(utils.material::colorTextInputLayout);

        utils.platform.colorImageView(binding.descriptionToggle, ColorRole.SECONDARY);

        binding.cardDueDateView.applyTheme(color);

        // TODO apply correct branding on the BrandedDatePicker
    }

    private void setupDescription() {
        if (viewModel.canEdit()) {
            binding.descriptionViewer.setMovementMethod(LinkMovementMethod.getInstance());

            binding.descriptionEditor.setMarkdownString(viewModel.getFullCard().getCard().getDescription());
            binding.descriptionViewer.setMarkdownString(viewModel.getFullCard().getCard().getDescription());

            viewModel.getDescriptionMode().observe(getViewLifecycleOwner(), (isPreviewMode) -> {
                if (isPreviewMode) {
                    toggleEditorView(binding.descriptionViewer, binding.descriptionEditorWrapper, binding.descriptionViewer);
                    binding.descriptionToggle.setImageResource(R.drawable.ic_edit_24dp);
                } else {
                    toggleEditorView(binding.descriptionEditorWrapper, binding.descriptionViewer, binding.descriptionEditor);
                    binding.descriptionToggle.setImageResource(R.drawable.ic_eye_24);
                }
            });

            viewModel.descriptionChangedFromExternal().observe(getViewLifecycleOwner(), description -> {
                binding.descriptionEditor.setMarkdownString(description);
                binding.descriptionViewer.setMarkdownString(description);
            });

            binding.descriptionToggle.setOnClickListener((v) -> viewModel.toggleDescriptionPreviewMode());

            registerEditorListener(binding.descriptionEditor);
            registerEditorListener(binding.descriptionViewer);
        } else {
            binding.descriptionEditor.setEnabled(false);
            binding.descriptionEditorWrapper.setVisibility(GONE);
            binding.descriptionViewer.setEnabled(false);
            binding.descriptionViewer.setVisibility(VISIBLE);

            viewModel.descriptionChangedFromExternal().observe(getViewLifecycleOwner(), description -> {
                binding.descriptionViewer.setMarkdownString(description);
            });
        }
    }

    private void registerEditorListener(@NonNull MarkdownEditor editor) {
        if (!editor.getMarkdownString().hasActiveObservers()) {
            editor.getMarkdownString().observe(getViewLifecycleOwner(), (newDescription) -> {
                if (viewModel.getFullCard() != null) {
                    // TODO This is the preferred way, but we need to preserve scroll and selection state
                    viewModel.getFullCard().getCard().setDescription(newDescription == null ? "" : newDescription.toString());
                } else {
                    ExceptionDialogFragment.newInstance(new IllegalStateException(FullCard.class.getSimpleName() + " was empty when trying to setup description"), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
                binding.descriptionToggle.setVisibility(TextUtils.isEmpty(newDescription) ? INVISIBLE : VISIBLE);
            });
        }
    }

    private void toggleEditorView(@NonNull View viewToShow, @NonNull View viewToHide, @NonNull MarkdownEditor editorToShow) {
        editorToShow.setMarkdownString(viewModel.getFullCard().getCard().getDescription());
        viewToHide.setVisibility(GONE);
        viewToShow.setVisibility(VISIBLE);
    }

    private void setupDueDate() {
        final var version = this.viewModel.getAccount().getServerDeckVersionAsObject();
        final var card = this.viewModel.getFullCard().getCard();
        binding.cardDueDateView.setDueDateListener(this);
        binding.cardDueDateView.setEnabled(this.viewModel.canEdit());
        binding.cardDueDateView.setDueDate(getChildFragmentManager(), version, card.getDueDate(), card.getDone());
    }

    @Override
    public void onDueDateChanged(@Nullable Instant dueDate) {
        final var version = this.viewModel.getAccount().getServerDeckVersionAsObject();
        final var card = this.viewModel.getFullCard().getCard();
        card.setDueDate(dueDate);
        binding.cardDueDateView.setDueDate(getChildFragmentManager(), version, card.getDueDate(), card.getDone());
    }

    @Override
    public void onDoneChanged(@Nullable Instant done) {
        final var version = this.viewModel.getAccount().getServerDeckVersionAsObject();
        final var card = this.viewModel.getFullCard().getCard();
        card.setDone(done);
        binding.cardDueDateView.setDueDate(getChildFragmentManager(), version, card.getDueDate(), card.getDone());
    }

    private void setupLabels(@NonNull Account account) {
        final long accountId = viewModel.getAccount().getId();
        final long boardId = viewModel.getBoardId();
        binding.labelsGroup.removeAllViews();
        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            try {
                binding.labels.setAdapter(new LabelAutoCompleteAdapter(requireActivity(), account, boardId, localCardId));
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                ExceptionDialogFragment.newInstance(e, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                // TODO Handle error
            }
            binding.labels.setOnItemClickListener((adapterView, view, position, id) -> {
                final var label = (Label) adapterView.getItemAtPosition(position);
                if (label.getLocalId() == null) {
                    viewModel.createLabel(accountId, label, boardId, new IResponseCallback<>() {
                        @Override
                        public void onResponse(Label response, Headers headers) {
                            requireActivity().runOnUiThread(() -> {
                                label.setLocalId(response.getLocalId());
                                ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(response);
                                viewModel.getFullCard().getLabels().add(response);
                                binding.labelsGroup.addView(createChipFromLabel(label));
                                binding.labelsGroup.setVisibility(VISIBLE);
                            });
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            viewModel.getCurrentBoardColor(viewModel.getAccount().getId(), viewModel.getBoardId())
                                    .thenAcceptAsync(color -> ThemedSnackbar.make(requireView(), getString(R.string.error_create_label, label.getTitle()), Snackbar.LENGTH_LONG, color)
                                            .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName())).show(), ContextCompat.getMainExecutor(requireContext()));
                        }
                    });
                } else {
                    ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).exclude(label);
                    viewModel.getFullCard().getLabels().add(label);
                    binding.labelsGroup.addView(createChipFromLabel(label));
                    binding.labelsGroup.setVisibility(VISIBLE);
                }

                binding.labels.setText("");
            });
        } else {
            binding.labels.setEnabled(false);
        }
        if (viewModel.getFullCard().getLabels() != null && viewModel.getFullCard().getLabels().size() > 0) {
            for (final var label : viewModel.getFullCard().getLabels()) {
                binding.labelsGroup.addView(createChipFromLabel(label));
            }
            binding.labelsGroup.setVisibility(VISIBLE);
        } else {
            binding.labelsGroup.setVisibility(INVISIBLE);
        }
    }

    private Chip createChipFromLabel(Label label) {
        final var chip = new Chip(requireContext());
        chip.setText(label.getTitle());
        if (viewModel.canEdit()) {
            chip.setCloseIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_circle));
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                binding.labelsGroup.removeView(chip);
                viewModel.getFullCard().getLabels().remove(label);
                ((LabelAutoCompleteAdapter) binding.labels.getAdapter()).doNotLongerExclude(label);
            });
        }
        try {
            final int labelColor = label.getColor();
            chip.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
            final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
            chip.setTextColor(color);

            if (chip.getCloseIcon() != null) {
                Drawable wrapDrawable = DrawableCompat.wrap(chip.getCloseIcon());
                DrawableCompat.setTint(wrapDrawable, ColorUtils.setAlphaComponent(color, 150));
            }
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
        }
        return chip;
    }

    private void setupAssignees() {
        adapter = new AssigneeAdapter((user) -> CardAssigneeDialog.newInstance(user).show(getChildFragmentManager(), CardAssigneeDialog.class.getSimpleName()), viewModel.getAccount());
        binding.assignees.setAdapter(adapter);
        binding.assignees.post(() -> {
            @Px final int gutter = getResources().getDimensionPixelSize(R.dimen.spacer_1x);
            final int spanCount = (int) (float) binding.labelsWrapper.getWidth() / (getResources().getDimensionPixelSize(R.dimen.avatar_size) + gutter);
            binding.assignees.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            binding.assignees.addItemDecoration(new AssigneeDecoration(spanCount, gutter));
        });

        if (viewModel.canEdit()) {
            Long localCardId = viewModel.getFullCard().getCard().getLocalId();
            localCardId = localCardId == null ? -1 : localCardId;
            try {
                binding.people.setAdapter(new UserAutoCompleteAdapter(requireActivity(), viewModel.getAccount(), viewModel.getBoardId(), localCardId));
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                ExceptionDialogFragment.newInstance(e, viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                // TODO Handle error
            }
            binding.people.setOnItemClickListener((adapterView, view, position, id) -> {
                final var user = (User) adapterView.getItemAtPosition(position);
                viewModel.getFullCard().getAssignedUsers().add(user);
                ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                adapter.addUser(user);
                binding.people.setText("");
            });
        } else {
            binding.people.setEnabled(false);
        }

        if (this.viewModel.getFullCard().getAssignedUsers() != null) {
            adapter.setUsers(this.viewModel.getFullCard().getAssignedUsers());
        }
    }

    private void setupProjects() {
        if (viewModel.getFullCard().getProjects().size() > 0) {
            binding.projectsTitle.setVisibility(VISIBLE);
            binding.projects.setNestedScrollingEnabled(false);
            final var adapter = new CardProjectsAdapter(viewModel.getFullCard().getProjects(), getChildFragmentManager());
            binding.projects.setAdapter(adapter);
            binding.projects.setVisibility(VISIBLE);
        } else {
            binding.projectsTitle.setVisibility(GONE);
            binding.projects.setVisibility(GONE);
        }
    }

    @Override
    public void onUnassignUser(@NonNull User user) {
        viewModel.getFullCard().getAssignedUsers().remove(user);
        adapter.removeUser(user);
        ((UserAutoCompleteAdapter) binding.people.getAdapter()).doNotLongerExclude(user);

        viewModel.getCurrentBoardColor(viewModel.getAccount().getId(), viewModel.getBoardId())
                .thenAcceptAsync(color -> ThemedSnackbar.make(requireView(), getString(R.string.unassigned_user, user.getDisplayname()), Snackbar.LENGTH_LONG, color)
                        .setAction(R.string.simple_undo, v1 -> {
                            viewModel.getFullCard().getAssignedUsers().add(user);
                            ((UserAutoCompleteAdapter) binding.people.getAdapter()).exclude(user);
                            adapter.addUser(user);
                        })
                        .show(), ContextCompat.getMainExecutor(requireContext()));
    }
}
