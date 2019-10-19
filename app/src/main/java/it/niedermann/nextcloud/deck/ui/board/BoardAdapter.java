package it.niedermann.nextcloud.deck.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    @NonNull
    private List<Board> boardsList;
    private Context context;

    public BoardAdapter(@NonNull List<Board> boardsList) {
        super();
        this.boardsList = boardsList;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        holder.boardName.setText(boardsList.get(position).getTitle());
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