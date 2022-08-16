package it.niedermann.nextcloud.deck.ui.pickstack;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentStackId;

import android.content.Context;
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
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.FragmentPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;
import it.niedermann.nextcloud.deck.ui.preparecreate.AccountAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.BoardAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.SelectedListener;
import it.niedermann.nextcloud.deck.ui.preparecreate.StackAdapter;

public class PickStackFragment extends Fragment {

    private FragmentPickStackBinding binding;
    private PickStackViewModel viewModel;

    private static final String KEY_SHOW_BOARDS_WITHOUT_EDIT_PERMISSION = "show_boards_without_edit_permission";

    private PickStackListener pickStackListener;

    private boolean showBoardsWithoutEditPermission = false;
    private long lastAccountId;
    private long lastBoardId;
    private long lastStackId;

    private ArrayAdapter<Account> accountAdapter;
    private ArrayAdapter<Board> boardAdapter;
    private ArrayAdapter<Stack> stackAdapter;

    @Nullable
    private LiveData<List<Board>> boardsLiveData;
    @NonNull
    private final Observer<List<Board>> boardsObserver = (boards) -> {
        boardAdapter.clear();
        boardAdapter.addAll(boards);
        binding.boardSelect.setEnabled(true);

        if (boards.size() > 0) {
            binding.boardSelect.setEnabled(true);

            Board boardToSelect = null;
            for (final var board : boards) {
                if (board.getLocalId() == lastBoardId) {
                    boardToSelect = board;
                    break;
                }
            }
            if (boardToSelect == null) {
                boardToSelect = boards.get(0);
            }
            binding.boardSelect.setSelection(boardAdapter.getPosition(boardToSelect));
        } else {
            binding.boardSelect.setEnabled(false);
            pickStackListener.onStackPicked((Account) binding.accountSelect.getSelectedItem(), null, null);
        }
    };

    @Nullable
    private LiveData<List<Stack>> stacksLiveData;
    @NonNull
    private final Observer<List<Stack>> stacksObserver = (stacks) -> {
        stackAdapter.clear();
        stackAdapter.addAll(stacks);

        if (stacks.size() > 0) {
            binding.stackSelect.setEnabled(true);

            Stack stackToSelect = null;
            for (final var stack : stacks) {
                if (stack.getLocalId() == lastStackId) {
                    stackToSelect = stack;
                    break;
                }
            }
            if (stackToSelect == null) {
                stackToSelect = stacks.get(0);
            }
            binding.stackSelect.setSelection(stackAdapter.getPosition(stackToSelect));
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
        final var args = getArguments();
        if (args != null) {
            this.showBoardsWithoutEditPermission = args.getBoolean(KEY_SHOW_BOARDS_WITHOUT_EDIT_PERMISSION, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentPickStackBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(PickStackViewModel.class);

        accountAdapter = new AccountAdapter(requireContext());
        binding.accountSelect.setAdapter(accountAdapter);
        binding.accountSelect.setEnabled(false);
        boardAdapter = new BoardAdapter(requireContext());
        binding.boardSelect.setAdapter(boardAdapter);
        binding.stackSelect.setEnabled(false);
        stackAdapter = new StackAdapter(requireContext());
        binding.stackSelect.setAdapter(stackAdapter);
        binding.stackSelect.setEnabled(false);

        switchMap(viewModel.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return viewModel.readAccounts();
            } else {
                // TODO After successfully importing the account, the creation will throw a TokenMissMatchException - Recreate SyncManager?
                startActivity(ImportAccountActivity.createIntent(requireContext()));
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

        binding.accountSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) ->
                updateLiveDataSource(boardsLiveData, boardsObserver, showBoardsWithoutEditPermission
                        ? viewModel.getBoards(parent.getSelectedItemId())
                        : viewModel.getBoardsWithEditPermission(parent.getSelectedItemId())));

        binding.boardSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) ->
                updateLiveDataSource(stacksLiveData, stacksObserver, viewModel.getStacksForBoard(binding.accountSelect.getSelectedItemId(), parent.getSelectedItemId())));

        binding.stackSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) ->
                pickStackListener.onStackPicked((Account) binding.accountSelect.getSelectedItem(), (Board) binding.boardSelect.getSelectedItem(), (Stack) parent.getSelectedItem()));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
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

    public static PickStackFragment newInstance(boolean showBoardsWithoutEditPermission) {
        final var fragment = new PickStackFragment();
        final var args = new Bundle();
        args.putBoolean(KEY_SHOW_BOARDS_WITHOUT_EDIT_PERMISSION, showBoardsWithoutEditPermission);
        fragment.setArguments(args);
        return fragment;
    }
}