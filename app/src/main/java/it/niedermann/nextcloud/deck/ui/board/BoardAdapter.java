package it.niedermann.nextcloud.deck.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    @NonNull
    private List<Board> boardsList;
    @Nullable
    private Context context;

    public BoardAdapter(@Nullable Context context, @NonNull List<Board> boardsList) {
        super();
        this.boardsList = boardsList;
        this.context = context;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board board = boardsList.get(position);
        holder.boardName.setText(board.getTitle());
        if(context != null) {
            holder.boardName.setCompoundDrawables(ViewUtil.getTintedImageView(context, R.drawable.circle_grey600_36dp, "#" + board.getColor()), null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return boardsList.size();
    }

    static class BoardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.boardName)
        TextView boardName;

        private BoardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}