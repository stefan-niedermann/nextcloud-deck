package it.niedermann.nextcloud.deck.ui.pickstack;

import static java.util.Collections.emptyList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.preparecreate.AccountAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.BoardAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.PickStackAdapter;
import it.niedermann.nextcloud.deck.ui.preparecreate.SelectedListener;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class PickStackFragment extends Fragment implements Themed, PickStackListener {

    private FragmentPickStackBinding binding;
    private PickStackViewModel viewModel;

    private static final String KEY_SHOW_BOARDS_WITHOUT_EDIT_PERMISSION = "show_boards_without_edit_permission";

    private PickStackListener pickStackListener;

    private boolean showBoardsWithoutEditPermission = false;

    private ArrayAdapter<Account> pickAccountAdapter;
    private ArrayAdapter<Board> pickBoardAdapter;
    private PickStackAdapter pickStackAdapter;

    private final ReactiveLiveData<Void> selectionChanged$ = new ReactiveLiveData<>();
    private final AtomicReference<Long> selectedAccount = new AtomicReference<>();
    private final Map<Long, Long> selectedBoard = new HashMap<>();
    private final Map<Pair<Long, Long>, Long> selectedStack = new HashMap<>();

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

        pickAccountAdapter = new AccountAdapter(requireContext());
        binding.accountSelect.setAdapter(pickAccountAdapter);
        binding.accountSelect.setEnabled(false);
        binding.accountSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            selectedAccount.set(parent.getSelectedItemId());
            selectionChanged$.setValue(null);
        });

        pickBoardAdapter = new BoardAdapter(requireContext());
        binding.boardSelect.setAdapter(pickBoardAdapter);
        binding.boardSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            selectedBoard.put(binding.accountSelect.getSelectedItemId(), parent.getSelectedItemId());
            selectionChanged$.setValue(null);
        });

        pickStackAdapter = new PickStackAdapter(stack -> {
            selectedStack.put(new Pair<>(binding.accountSelect.getSelectedItemId(), binding.boardSelect.getSelectedItemId()), stack.getLocalId());
            selectionChanged$.setValue(null);
        });
        binding.stackSelect.setAdapter(pickStackAdapter);

        selectionChanged$
                .flatMap(() -> viewModel.readAccounts())
                .flatMap(accounts -> {
                    binding.accountSelect.setEnabled(false);
                    binding.boardSelect.setEnabled(false);
                    setAccounts(accounts);

                    return getSelectedAccount()
                            .map(accountIdToSelect -> selectAccount(accounts, accountIdToSelect))
                            .flatMap(accountId -> getBoards(accountId, showBoardsWithoutEditPermission)
                                    .flatMap(boards -> {
                                        binding.boardSelect.setEnabled(false);
                                        setBoards(boards);

                                        return getSelectedBoard(accountId)
                                                .map(boardId -> selectBoard(boards, boardId))
                                                .flatMap(boardId -> getStacks(accountId, boardId)
                                                        .flatMap(stacks -> {
                                                            setStacks(stacks);

                                                            return getSelectedStack(accountId, boardId)
                                                                    .map(stackId -> selectStack(stacks, stackId));
                                                        }));
                                    }));

                }).observe(this);

        selectionChanged$.setValue(null);

        return binding.getRoot();
    }

    private ReactiveLiveData<Long> getSelectedAccount() {
        if (selectedAccount.get() == null) {
            return new ReactiveLiveData<>(viewModel.getCurrentAccountId$());
        } else {
            return new ReactiveLiveData<>(selectedAccount.get());
        }
    }

    private void setAccounts(@NonNull Collection<Account> accounts) {
        pickAccountAdapter.clear();
        pickAccountAdapter.addAll(accounts);

        if (accounts.size() > 1) {
            binding.accountSelect.setVisibility(View.VISIBLE);
            binding.accountSelect.setEnabled(true);
        } else {
            binding.accountSelect.setVisibility(View.GONE);
        }
    }


    @Nullable
    private Long selectAccount(@NonNull Collection<Account> accounts, @Nullable Long accountIdToSelect) {
        final var matchingAccount = accounts
                .stream()
                .filter(account -> Objects.equals(account.getId(), accountIdToSelect))
                .findAny()
                .or(() -> accounts.stream().findAny());

        if (matchingAccount.isPresent()) {
            binding.accountSelect.setSelection(pickAccountAdapter.getPosition(matchingAccount.get()));
            return matchingAccount.get().getId();
        } else {
            return null;
        }
    }

    private ReactiveLiveData<Long> getSelectedBoard(@Nullable Long accountId) {
        if (accountId == null) {
            return new ReactiveLiveData<>(null);
        } else if (selectedBoard.containsKey(accountId)) {
            return new ReactiveLiveData<>(Objects.requireNonNull(selectedBoard.get(accountId)));
        } else {
            return new ReactiveLiveData<>(viewModel.getCurrentBoardId$(accountId));
        }
    }

    private ReactiveLiveData<List<Board>> getBoards(@Nullable Long accountId, boolean showBoardsWithoutEditPermission) {
        if (accountId == null) {
            return new ReactiveLiveData<>(emptyList());
        } else if (showBoardsWithoutEditPermission) {
            return new ReactiveLiveData<>(viewModel.getNotArchivedBoards(accountId));
        } else {
            return new ReactiveLiveData<>(viewModel.getBoardsWithEditPermission(accountId));
        }
    }

    private void setBoards(@NonNull Collection<Board> boards) {
        pickBoardAdapter.clear();
        pickBoardAdapter.addAll(boards);

        if (boards.size() > 1) {
            binding.boardSelect.setVisibility(View.VISIBLE);
            binding.boardSelect.setEnabled(true);
        } else {
            binding.boardSelect.setVisibility(View.GONE);
        }
    }

    @Nullable
    private Long selectBoard(@NonNull Collection<Board> boards, @Nullable Long boardIdToSelect) {
        final var matchingBoard = boards
                .stream()
                .filter(board -> Objects.equals(board.getLocalId(), boardIdToSelect))
                .findAny()
                .or(() -> boards.stream().findAny());

        if (matchingBoard.isPresent()) {
            binding.boardSelect.setSelection(pickBoardAdapter.getPosition(matchingBoard.get()));
            applyTheme(matchingBoard.get().getColor());
            return matchingBoard.get().getLocalId();
        } else {
            onStackPicked((Account) binding.accountSelect.getSelectedItem(), null, null);
            return null;
        }
    }

    private ReactiveLiveData<List<Stack>> getStacks(@Nullable Long accountId, @Nullable Long boardId) {
        if (accountId == null || boardId == null) {
            return new ReactiveLiveData<>(emptyList());
        } else {
            return new ReactiveLiveData<>(viewModel.getStacksForBoard(accountId, boardId));
        }
    }

    private void setStacks(@NonNull Collection<Stack> stacks) {
        pickStackAdapter.setStacks(stacks);
    }

    private ReactiveLiveData<Long> getSelectedStack(@Nullable Long accountId, @Nullable Long boardId) {
        if (selectedStack.containsKey(new Pair<>(accountId, boardId))) {
            return new ReactiveLiveData<>(Objects.requireNonNull(selectedStack.get(new Pair<>(accountId, boardId))));
        } else if (accountId == null || boardId == null) {
            return new ReactiveLiveData<>(null);
        } else {
            return new ReactiveLiveData<>(viewModel.getCurrentStackId$(accountId, boardId));
        }
    }

    private Long selectStack(@NonNull Collection<Stack> stacks, @Nullable Long stackIdToSelect) {
        final var matchingStack = stacks
                .stream()
                .filter(stack -> Objects.equals(stack.getLocalId(), stackIdToSelect))
                .findAny()
                .or(() -> stacks.stream().findAny());

        if (matchingStack.isPresent()) {
            pickStackAdapter.setSelection(matchingStack.get());
            onStackPicked((Account) binding.accountSelect.getSelectedItem(), (Board) binding.boardSelect.getSelectedItem(), matchingStack.get());
            return matchingStack.get().getLocalId();
        } else {
            onStackPicked((Account) binding.accountSelect.getSelectedItem(), (Board) binding.boardSelect.getSelectedItem(), null);
            return null;
        }
    }

    @Override
    public void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable Stack stack) {
        DeckLog.verbose("Picked account", account.getName());
        DeckLog.verbose("Picked board", board == null ? "null" : board.getTitle());
        DeckLog.verbose("Picked stack", stack == null ? "null" : stack.getTitle());
        pickStackListener.onStackPicked(account, board, stack);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        pickStackAdapter.applyTheme(color);
    }

    public static PickStackFragment newInstance(boolean showBoardsWithoutEditPermission) {
        final var fragment = new PickStackFragment();
        final var args = new Bundle();
        args.putBoolean(KEY_SHOW_BOARDS_WITHOUT_EDIT_PERMISSION, showBoardsWithoutEditPermission);
        fragment.setArguments(args);
        return fragment;
    }
}