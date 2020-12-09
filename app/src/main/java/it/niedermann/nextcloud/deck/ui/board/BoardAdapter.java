package it.niedermann.nextcloud.deck.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class BoardAdapter extends ArrayAdapter<Board> {

    @NonNull
    private Context context;

    public BoardAdapter(@NonNull Context context, @NonNull Board[] boardsList) {
        super(context, R.layout.item_board, boardsList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Board board = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board, parent, false);
        }
        TextView boardName = convertView.findViewById(R.id.boardName);
        if (board != null) {
            boardName.setText(board.getTitle());
            boardName.setCompoundDrawables(ViewUtil.getTintedImageView(context, R.drawable.circle_grey600_36dp, board.getColor()), null, null, null);
        } else {
            DeckLog.logError(new IllegalArgumentException("board at position " + position + "is null"));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}