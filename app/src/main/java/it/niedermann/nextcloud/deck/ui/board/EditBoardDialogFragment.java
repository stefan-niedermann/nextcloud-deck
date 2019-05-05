package it.niedermann.nextcloud.deck.ui.board;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class EditBoardDialogFragment extends DialogFragment {

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";
    private static final Long NO_BOARD_ID = -1L;

    @NonNull private Activity activity;
    private Context context;
    private SyncManager syncManager;

    private FullBoard fullBoard = null;
    private Long boardId = null;
    private Long accountId = null;
    private String selectedColor;
    private String previouslySelectedColor;
    private ImageView previouslySelectedImageView;

    @BindView(R.id.input)
    EditText boardTitle;
    @BindView(R.id.colorPicker)
    FlexboxLayout colorPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = Objects.requireNonNull(getActivity());
        this.context = Objects.requireNonNull(getContext());
        View view = this.activity.getLayoutInflater().inflate(R.layout.dialog_board_create, null);
        ButterKnife.bind(this, view);
        boardId = Objects.requireNonNull(getArguments()).getLong(KEY_BOARD_ID);
        accountId = Objects.requireNonNull(getArguments()).getLong(KEY_ACCOUNT_ID);
        syncManager = new SyncManager(this.context, this.activity);

        AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(this.activity);

        if(NO_BOARD_ID.equals(boardId)) {
            initColorChooser();
            dialogBuilder.setTitle(R.string.create_board);
            dialogBuilder.setPositiveButton(R.string.simple_create, (dialog, which) -> ((MainActivity) getActivity()).onCreateBoard(boardTitle.getText().toString(), selectedColor));
        } else {
            dialogBuilder.setTitle(R.string.edit_board);
            dialogBuilder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
                this.fullBoard.board.setColor(selectedColor.substring(1));
                this.fullBoard.board.setTitle(this.boardTitle.getText().toString());
                ((MainActivity) getActivity()).onUpdateBoard(fullBoard);
            });
            syncManager.getFullBoardById(accountId, boardId).observe(EditBoardDialogFragment.this, (FullBoard fb) -> {
                if(fb != null && fb.board != null) {
                    this.fullBoard = fb;
                    this.boardTitle.setText(this.fullBoard.board.getTitle());
                    initColorChooser();
                }
            });
        }

        return dialogBuilder
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {})
                .create();
    }

    public static EditBoardDialogFragment newInstance(@NonNull Long boardId, @NonNull Long accountId) {
        EditBoardDialogFragment dialog = new EditBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardId);
        args.putLong(KEY_ACCOUNT_ID, accountId);
        dialog.setArguments(args);

        return dialog;
    }

    public static EditBoardDialogFragment newInstance() {
        EditBoardDialogFragment dialog = new EditBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, NO_BOARD_ID);
        Board b = new Board();
        dialog.setArguments(args);

        return dialog;
    }

    private void initColorChooser() {
        String[] colors = getResources().getStringArray(R.array.board_default_colors);

        // TODO refactor color chooser as own View Component
        for (final String color : colors) {
            ImageView image = new ImageView(getContext());
            image.setOnClickListener((imageView) -> {
                if (previouslySelectedImageView != null) { // null when first selection
                    previouslySelectedImageView.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, previouslySelectedColor));
                }
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_check_36dp, color));
                selectedColor = color;
                this.previouslySelectedColor = color;
                this.previouslySelectedImageView = image;
            });
            if(this.fullBoard != null && color.equals("#" + this.fullBoard.board.getColor())) {
                this.selectedColor = color;
                this.previouslySelectedColor = color;
                this.previouslySelectedImageView = image;
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_alpha_check_36dp, color));
            } else {
                image.setImageDrawable(ViewUtil.getTintedImageView(this.context, R.drawable.circle_grey600_36dp, color));
            }
            colorPicker.addView(image);
        }
    }
}