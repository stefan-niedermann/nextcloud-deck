package it.niedermann.nextcloud.deck.ui.movecard;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogMoveCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackFragment;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackListener;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class MoveCardDialogFragment extends DialogFragment implements Themed, PickStackListener {

    private static final String KEY_ORIGIN_ACCOUNT_ID = "account_id";
    private static final String KEY_ORIGIN_BOARD_LOCAL_ID = "board_local_id";
    private static final String KEY_ORIGIN_CARD_TITLE = "card_title";
    private static final String KEY_ORIGIN_CARD_LOCAL_ID = "card_local_id";
    private static final String KEY_ORIGIN_CARD_HAS_ATTACHMENTS_OR_COMMENTS = "card_has_attachments_or_comments";
    private Long originAccountId;
    private Long originBoardLocalId;
    private String originCardTitle;
    private Long originCardLocalId;
    private boolean originCardHasAttachmentsOrComments;
    private View dialogView;

    private DialogMoveCardBinding binding;
    private PickStackViewModel viewModel;
    private MoveCardListener moveCardListener;

    private Account selectedAccount;
    private Board selectedBoard;
    private Stack selectedStack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof MoveCardListener) {
            this.moveCardListener = (MoveCardListener) getParentFragment();
        } else if (context instanceof MoveCardListener) {
            this.moveCardListener = (MoveCardListener) context;
        } else {
            throw new IllegalArgumentException("Caller must implement " + MoveCardListener.class.getSimpleName());
        }

        final Bundle args = requireArguments();
        originAccountId = args.getLong(KEY_ORIGIN_ACCOUNT_ID, -1L);
        if (originAccountId < 0) {
            throw new IllegalArgumentException("Missing " + KEY_ORIGIN_ACCOUNT_ID);
        }
        originCardLocalId = args.getLong(KEY_ORIGIN_CARD_LOCAL_ID, -1L);
        if (originCardLocalId < 0) {
            throw new IllegalArgumentException("Missing " + KEY_ORIGIN_CARD_LOCAL_ID);
        }
        originBoardLocalId = args.getLong(KEY_ORIGIN_BOARD_LOCAL_ID, -1L);
        if (originBoardLocalId < 0) {
            throw new IllegalArgumentException("Missing " + KEY_ORIGIN_BOARD_LOCAL_ID);
        }
        originCardHasAttachmentsOrComments = args.getBoolean(KEY_ORIGIN_CARD_HAS_ATTACHMENTS_OR_COMMENTS, false);
        originCardTitle = args.getString(KEY_ORIGIN_CARD_TITLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final var dialogBuilder = new MaterialAlertDialogBuilder(requireContext());

        binding = DialogMoveCardBinding.inflate(getLayoutInflater());
        binding.title.setText(getString(R.string.action_card_move_title, originCardTitle));
        binding.submit.setOnClickListener((v) -> {
            DeckLog.verbose("[Move card] Attempt to move to", Stack.class.getSimpleName(), "#" + selectedStack.getLocalId());
            this.moveCardListener.move(originAccountId, originCardLocalId, selectedAccount.getId(), selectedBoard.getLocalId(), selectedStack.getLocalId());
            dismiss();
        });
        binding.cancel.setOnClickListener((v) -> dismiss());
        dialogView = binding.getRoot();
        return dialogBuilder
                .setView(dialogView)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PickStackFragment.newInstance(false), PickStackFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable Stack stack) {
        this.selectedAccount = account;
        this.selectedBoard = board;
        this.selectedStack = stack;

        applyTheme(board == null
                ? ContextCompat.getColor(requireContext(), R.color.onSurface)
                : board.getColor()
        );

        if (board == null || stack == null) {
            binding.submit.setEnabled(false);
            binding.moveWarning.setVisibility(GONE);
        } else {
            binding.submit.setEnabled(true);
            binding.moveWarning.setVisibility(originCardHasAttachmentsOrComments && !board.getLocalId().equals(originBoardLocalId) ? VISIBLE : GONE);
        }
    }

    @Override
    public void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.colorMaterialButtonText(binding.cancel);
        utils.material.colorMaterialButtonText(binding.submit);
    }

    public static DialogFragment newInstance(long originAccountId, long originBoardLocalId, String originCardTitle, Long originCardLocalId, boolean hasAttachmentsOrComments) {
        final var fragment = new MoveCardDialogFragment();
        final var args = new Bundle();
        args.putLong(KEY_ORIGIN_ACCOUNT_ID, originAccountId);
        args.putLong(KEY_ORIGIN_BOARD_LOCAL_ID, originBoardLocalId);
        args.putString(KEY_ORIGIN_CARD_TITLE, originCardTitle);
        args.putLong(KEY_ORIGIN_CARD_LOCAL_ID, originCardLocalId);
        args.putBoolean(KEY_ORIGIN_CARD_HAS_ATTACHMENTS_OR_COMMENTS, hasAttachmentsOrComments);
        fragment.setArguments(args);
        return fragment;
    }
}
