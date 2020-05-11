package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemArchivedBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;

public class ArchivedBoardsAdapter extends RecyclerView.Adapter<ArchivedBoardViewHolder> {

    private final boolean isSupportedVersion;
    @NonNull
    private final Consumer<Board> onDearchiveListener;
    @NonNull
    private final FragmentManager fragmentManager;
    @NonNull
    private List<Board> boards = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    public ArchivedBoardsAdapter(boolean isSupportedVersion, @NonNull FragmentManager fragmentManager, @NonNull Consumer<Board> onDearchiveListener) {
        this.isSupportedVersion = isSupportedVersion;
        this.fragmentManager = fragmentManager;
        this.onDearchiveListener = onDearchiveListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ArchivedBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArchivedBoardViewHolder(ItemArchivedBoardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public long getItemId(int position) {
        return boards.get(position).getLocalId();
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedBoardViewHolder holder, int position) {
        holder.bind(isSupportedVersion, boards.get(position), fragmentManager, onDearchiveListener);
    }

    @Override
    public int getItemCount() {
        return boards.size();
    }

    public void setBoards(@NonNull List<Board> boards) {
        this.boards.clear();
        this.boards.addAll(boards);
        notifyDataSetChanged();
    }
}
