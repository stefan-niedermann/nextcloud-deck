package it.niedermann.nextcloud.deck.ui.pickstack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;
import it.niedermann.nextcloud.deck.ui.preparecreate.AccountAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.BoardAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.SelectedListener;
import it.niedermann.nextcloud.deck.ui.preparecreate.StackAdapter;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentStackId;

public class PickStackFragment extends Fragment {

    private FragmentPickStackBinding binding;

    private SyncManager syncManager;

    private PickStackListener pickStackListener;

    private long lastAccountId;
    private long lastBoardId;
    private long lastStackId;

    private ArrayAdapter<Account> accountAdapter;
    private ArrayAdapter<Board> boardAdapter;
    private ArrayAdapter<FullStack> stackAdapter;

    @Nullable
    private LiveData<List<Board>> boardsLiveData;
    @NonNull
    private Observer<List<Board>> boardsObserver = (boards) -> {
        boardAdapter.clear();
        boardAdapter.addAll(boards);
        binding.boardSelect.setEnabled(true);

        if (boards.size() > 0) {
            binding.boardSelect.setEnabled(true);

            Board boardToSelect = null;
            for (Board board : boards) {
                if (board.getLocalId() == lastBoardId) {
                    boardToSelect = board;
                    break;
                }
            }
            if(boardToSelect == null) {
                boardToSelect = boards.get(0);
            }
            binding.boardSelect.setSelection(boardAdapter.getPosition(boardToSelect));
        } else {
            binding.boardSelect.setEnabled(false);
            pickStackListener.onStackPicked((Account) binding.accountSelect.getSelectedItem(), null, null);
        }
    };

    @Nullable
    private LiveData<List<FullStack>> stacksLiveData;
    @NonNull
    private Observer<List<FullStack>> stacksObserver = (fullStacks) -> {
        stackAdapter.clear();
        stackAdapter.addAll(fullStacks);

        if (fullStacks.size() > 0) {
            binding.stackSelect.setEnabled(true);

            FullStack fullStackToSelect = null;
            for (FullStack fullStack : fullStacks) {
                if (fullStack.getLocalId() == lastStackId) {
                    fullStackToSelect = fullStack;
                    break;
                }
            }
            if (fullStackToSelect == null) {
                fullStackToSelect = fullStacks.get(0);
            }
            binding.stackSelect.setSelection(stackAdapter.getPosition(fullStackToSelect));
        } else {
            binding.stackSelect.setEnabled(false);
            pickStackListener.onStackPicked((Account) binding.accountSelect.getSelectedItem(), (Board) binding.boardSelect.getSelectedItem(), null);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof PickStackListener) {
            this.pickStackListener = (PickStackListener) getParentFragment();
        } else if (context instanceof PickStackListener) {
            this.pickStackListener = (PickStackListener) context;
        } else {
            throw new IllegalArgumentException("Caller must implement " + PickStackListener.class.getSimpleName());
        }
        DeckLog.error("PICKSTACK: onAttach successful");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentPickStackBinding.inflate(getLayoutInflater());

        accountAdapter = new AccountAdapter(requireContext());
        binding.accountSelect.setAdapter(accountAdapter);
        binding.accountSelect.setEnabled(false);
        boardAdapter = new BoardAdapter(requireContext());
        binding.boardSelect.setAdapter(boardAdapter);
        binding.stackSelect.setEnabled(false);
        stackAdapter = new StackAdapter(requireContext());
        binding.stackSelect.setAdapter(stackAdapter);
        binding.stackSelect.setEnabled(false);

        syncManager = new SyncManager(requireContext());

        switchMap(syncManager.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return syncManager.readAccounts();
            } else {
                startActivityForResult(new Intent(requireActivity(), ImportAccountActivity.class), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(getViewLifecycleOwner(), (List<Account> accounts) -> {
            if (accounts == null || accounts.size() == 0) {
                throw new IllegalStateException("hasAccounts() returns true, but readAccounts() returns null or has no entry");
            }

            lastAccountId = readCurrentAccountId(requireContext());
            lastBoardId = readCurrentBoardId(requireContext(), lastAccountId);
            lastStackId = readCurrentStackId(requireContext(), lastAccountId, lastBoardId);

            accountAdapter.clear();
            accountAdapter.addAll(accounts);
            binding.accountSelect.setEnabled(true);

            for (Account account : accounts) {
                if (account.getId() == lastAccountId) {
                    binding.accountSelect.setSelection(accountAdapter.getPosition(account));
                    break;
                }
            }
        });

        binding.accountSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            updateLiveDataSource(boardsLiveData, boardsObserver, syncManager.getBoardsWithEditPermission(parent.getSelectedItemId()));
        });

        binding.boardSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            updateLiveDataSource(stacksLiveData, stacksObserver, syncManager.getStacksForBoard(binding.accountSelect.getSelectedItemId(), parent.getSelectedItemId()));
        });

        binding.stackSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            pickStackListener.onStackPicked((Account) binding.accountSelect.getSelectedItem(), (Board) binding.boardSelect.getSelectedItem(), (FullStack) parent.getSelectedItem());
        });

        return binding.getRoot();
    }

    /**
     * Updates the source of the given liveData and de- and reregisters the given observer.
     */
    private <T> void updateLiveDataSource(@Nullable LiveData<T> liveData, Observer<T> observer, LiveData<T> newSource) {
        if (liveData != null) {
            liveData.removeObserver(observer);
        }
        liveData = newSource;
        liveData.observe(getViewLifecycleOwner(), observer);
    }

    public static PickStackFragment newInstance() {
        return new PickStackFragment();
    }
}