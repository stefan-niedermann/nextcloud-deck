package it.niedermann.nextcloud.deck;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.model.Board;

class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Board> boardList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_board_item, viewGroup, false);
        return new BoardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Board board = boardList.get(position);
        ((BoardViewHolder) viewHolder).boardTitle.setText(board.getTitle());
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    void setBoardList(@NonNull List<Board> boardList) {
        this.boardList = boardList;
        notifyDataSetChanged();
    }



    static class BoardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.board_title)
        TextView boardTitle;

        private BoardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
