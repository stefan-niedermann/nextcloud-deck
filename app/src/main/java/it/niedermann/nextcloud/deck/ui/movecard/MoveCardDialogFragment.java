package it.niedermann.nextcloud.deck.ui.movecard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogMoveCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackFragment;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MoveCardDialogFragment extends BrandedDialogFragment implements PickStackListener {

    private static final String KEY_ORIGIN_ACCOUNT_ID = "account_id";
    private static final String KEY_ORIGIN_BOARD_LOCAL_ID = "board_local_id";
    private static final String KEY_ORIGIN_CARD_LOCAL_ID = "card_local_id";
    private Long originAccountId;
    private Long originBoardLocalId;
    private Long originCardLocalId;

    private DialogMoveCardBinding binding;
    private PickStackFragment fragment;
    private MoveCardListener moveCardListener;

    private Account selectedAccount;
    private Board selectedBoard;
    private FullStack selectedStack;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogMoveCardBinding.inflate(inflater);
        binding.submit.setOnClickListener((v) -> {
            DeckLog.verbose("MOVE - Attempt to move to " + Stack.class.getSimpleName() + " #" + selectedStack.getLocalId());
            this.moveCardListener.move(originAccountId, originCardLocalId, selectedAccount.getId(), selectedBoard.getLocalId(), selectedStack.getLocalId());
            dismiss();
        });
        binding.cancel.setOnClickListener((v) -> dismiss());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragment = new PickStackFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable FullStack fullStack) {
        this.selectedAccount = account;
        this.selectedBoard = board;
        this.selectedStack = fullStack;
//        DeckLog.log("MOVE - Stack changed to " + fullStack.getStack().getTitle());
        if (board == null || fullStack == null) {
            binding.submit.setEnabled(false);
            binding.moveWarning.setVisibility(GONE);
        } else {
            binding.submit.setEnabled(true);
            binding.moveWarning.setVisibility(board.getLocalId().equals(originBoardLocalId) ? GONE : VISIBLE);
        }
    }

    @Override
    public void applyBrand(int mainColor) {
        final ColorStateList mainColorStateList = ColorStateList.valueOf(BrandingUtil.getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor));
        binding.cancel.setTextColor(mainColorStateList);
        binding.submit.setTextColor(mainColorStateList);
    }

    public static DialogFragment newInstance(long originAccountId, long originBoardLocalId, Long originCardLocalId) {
        final DialogFragment dialogFragment = new MoveCardDialogFragment();
        final Bundle args = new Bundle();
        args.putLong(KEY_ORIGIN_ACCOUNT_ID, originAccountId);
        args.putLong(KEY_ORIGIN_BOARD_LOCAL_ID, originBoardLocalId);
        args.putLong(KEY_ORIGIN_CARD_LOCAL_ID, originCardLocalId);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
}
