package it.niedermann.nextcloud.deck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigationrail.NavigationRailView;

import it.niedermann.nextcloud.deck.databinding.FragmentMainBinding;
import it.niedermann.nextcloud.deck.feature.setup.BR;

public class MainFragment extends Fragment implements NavController.OnDestinationChangedListener {

    private MainViewModel vm;
    private FragmentMainBinding binding;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setVariable(BR.vm, vm);
        binding.setVariable(BR.fragment, this);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.drawerLayout.open();
        navController = NavHostFragment.findNavController(binding.navHostFragmentMain.getFragment());
        navController.addOnDestinationChangedListener(this);

        binding.navigationRail.setOnItemSelectedListener(v -> {
//            final var navController = Navigation.findNavController(view);
            // Static navigation entries
//            if (v.getItemId() != R.id.nav_view_board_group_Item) {
//            if (mainNavInfo.boardOverview().containsKey(v.getItemId())) {
            if (v.getGroupId() == 1337) { // TODO How to detect that this item is a synthetic one - by group id? Or by exclusion?
//                navController.navigate(v.getItemId());
//                NavDirections action = NavGraphMainDirections.navGraphViewBoard(boardInfo.id());
//                navController.navigate(action);
            } else {
//                navController.navigate(v.getItemId());
                NavigationUI.onNavDestinationSelected(v, navController);
            }
//            } else if (v.getGroupId() == R.id.nav_view_board_group) {
//                Navigation.findNavController(view).navigate(it.niedermann.nextcloud.deck.feature.view_board.R.id.nav_graph_view_board);
//            }
            return true;
        });
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        // TODO Mark menu item as checked depending on navDestination
    }

    @Override
    public void onDestroy() {
        vm = null;
        binding = null;
        super.onDestroy();
    }


    @BindingAdapter("mymenu")
    public static void menu(NavigationRailView view, @Nullable MainViewModel.MainNavInfo mainNavInfo) {

        if (mainNavInfo == null) {
            return;
        }

        final var context = view.getContext();
        final var menu = view.getMenu().findItem(R.id.nav_view_board_group_item).getSubMenu();

        if (menu == null) {
            throw new NullPointerException("Expected menu R.id.nav_view_board_group_item to have sub menu");
        }

        menu.clear();

        mainNavInfo.boardOverview().forEach((boardIndex, boardInfo) -> {
            final var menuItem = menu
                    .add(1337, boardIndex, Menu.NONE, boardInfo.title() + " Board " + boardIndex)
                    .setTitle(boardInfo.title() + " Board " + boardIndex)
                    .setTitleCondensed("Board " + boardIndex)
                    .setIcon(it.niedermann.nextcloud.deck.feature.setup.R.drawable.shape_circular)
                    .setOnMenuItemClickListener(item -> {
                        final var navController = Navigation.findNavController(view);
                        final var action = NavGraphMainDirections.navGraphViewBoard(boardInfo.id());
                        navController.navigate(action);
                        return true;
                    })
                    .setCheckable(true);

            if (boardInfo.permissionManage()) {
                final var contextMenu = getContextMenu(view, boardInfo);
                menuItem.setActionView(contextMenu);

            } else if (boardInfo.permissionShare()) {
                final var contextMenu = new AppCompatImageButton(context);
                contextMenu.setBackgroundDrawable(null);
//                contextMenu.setImageDrawable(context, it.niedermann.nextcloud.deck.feature.setup.R.drawable.shape_circular);
//                contextMenu.setOnClickListener((v) -> AccessControlDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName()));
                menuItem.setActionView(contextMenu);
            }

//            if (hasArchivedBoards) {
//                menu.add(Menu.NONE, MENU_ID_ARCHIVED_BOARDS, Menu.NONE, R.string.archived_boards).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_archive_24dp));
//            }

//            if (currentServerVersionIsSupported) {
//                menu.add(Menu.NONE, MENU_ID_ADD_BOARD, Menu.NONE, R.string.add_board).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_add_24dp));
//            }

//            menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, R.string.simple_settings).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_settings_24dp));
//            menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, R.string.about).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_info_outline_24dp));

        });
    }

    @NonNull
    private static AppCompatImageButton getContextMenu(NavigationRailView view, MainViewModel.MainNavInfo.BoardInfo boardInfo) {
        final var context = view.getContext();
        final var contextMenu = new AppCompatImageButton(context);
        contextMenu.setBackgroundDrawable(null);
//        contextMenu.setImageDrawable(context, it.niedermann.nextcloud.deck.feature.setup.R.drawable.shape_circular);
        contextMenu.setOnClickListener((v) -> {

            final var popup = new PopupMenu(context, contextMenu);
//            popup.getMenuInflater().inflate(R.menu.navigation_context_menu, popup.getMenu());

            final int SHARE_BOARD_ID = -1;

            if (boardInfo.permissionShare()) {
//                popup.getMenu().add(Menu.NONE, SHARE_BOARD_ID, 5, R.string.share_board);
            }

            popup.setOnMenuItemClickListener((MenuItem item) -> {
//                final String editBoard = context.getString(R.string.edit_board);
                NavDirections action = NavGraphMainDirections.navGraphViewBoard(boardInfo.id());
                Navigation.findNavController(view).navigate(action);
//                if (itemId == SHARE_BOARD_ID) {
//                            AccessControlDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName());
//                    return true;
//                } else if (itemId == R.id.edit_board) {
//                            EditBoardDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), editBoard);
//                    return true;
//                } else if (itemId == R.id.manage_labels) {
//                            ManageLabelsDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), editBoard);
//                    return true;
//                } else if (itemId == R.id.clone_board) {
//                            activity.onClone(account, fullBoard.getBoard());
//                    return true;
//                } else if (itemId == R.id.archive_board) {
//                            activity.onArchive(fullBoard.getBoard());
//                    return true;
//                } else if (itemId == R.id.delete_board) {
//                            DeleteBoardDialogFragment.newInstance(fullBoard.getBoard()).show(activity.getSupportFragmentManager(), DeleteBoardDialogFragment.class.getCanonicalName());
//                    return true;
//                }
                return false;
            });
            popup.show();
        });
        return contextMenu;
    }

}
