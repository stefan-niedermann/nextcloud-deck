package it.niedermann.nextcloud.deck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import it.niedermann.nextcloud.deck.ui.board.BoardScreen
import it.niedermann.nextcloud.deck.ui.boards.BoardListScreen
import it.niedermann.nextcloud.deck.ui.boards.edit.EditBoardScreen
import it.niedermann.nextcloud.deck.ui.card.CardDetailsScreen
import it.niedermann.nextcloud.deck.ui.login.LoginScreen
import it.niedermann.nextcloud.deck.ui.navigation.BoardListRoute
import it.niedermann.nextcloud.deck.ui.navigation.BoardViewRoute
import it.niedermann.nextcloud.deck.ui.navigation.CardDetailsRoute
import it.niedermann.nextcloud.deck.ui.navigation.EditBoardRoute
import it.niedermann.nextcloud.deck.ui.navigation.LoginRoute
import it.niedermann.nextcloud.deck.ui.theme.NextcloudDeckTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isInitialized by viewModel.isInitialized.collectAsStateWithLifecycle()
            val currentAccountId by viewModel.currentAccountId.collectAsStateWithLifecycle()
            val currentBoardId by viewModel.currentBoardId.collectAsStateWithLifecycle()
            val hasAccounts by viewModel.hasAccounts.collectAsStateWithLifecycle()

            NextcloudDeckTheme {
                if (isInitialized) {
                    val startDestination = if (currentAccountId != null && hasAccounts) {
                        val boardId = currentBoardId
                        if (boardId != null) {
                            BoardViewRoute(boardId.value)
                        } else {
                            BoardListRoute
                        }
                    } else {
                        LoginRoute
                    }
                    AppNavigation(startDestination, viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    startDestination: Any,
    mainViewModel: MainActivityViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Redirect to login if all accounts are deleted
    val hasAccounts by mainViewModel.hasAccounts.collectAsStateWithLifecycle()
    LaunchedEffect(hasAccounts) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (!hasAccounts && currentRoute != LoginRoute::class.qualifiedName) {
            navController.navigate(LoginRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Refresh current account if it changes in the background
    val currentAccountId by mainViewModel.currentAccountId.collectAsStateWithLifecycle()
    LaunchedEffect(currentAccountId) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentAccountId != null && currentRoute == LoginRoute::class.qualifiedName) {
             navController.navigate(BoardListRoute) {
                popUpTo(LoginRoute) { inclusive = true }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<LoginRoute> {
                LoginScreen(onLoginSuccess = {
                    mainViewModel.refreshCurrentAccount()
                })
            }
            composable<BoardListRoute> {
                BoardListScreen(
                    onBoardClick = { boardId ->
                        navController.navigate(BoardViewRoute(boardId))
                    },
                    onEditBoardClick = { boardId ->
                        navController.navigate(EditBoardRoute(boardId))
                    },
                    onAddAccount = {
                        navController.navigate(LoginRoute)
                    },
                    onCardClick = { cardId ->
                        navController.navigate(CardDetailsRoute(cardId))
                    }
                )
            }
            composable<BoardViewRoute> { backStackEntry ->
                val route: BoardViewRoute = backStackEntry.toRoute()
                BoardScreen(
                    boardId = route.boardId,
                    onCardClick = { cardId ->
                        navController.navigate(CardDetailsRoute(cardId))
                    },
                    onAddAccount = {
                        navController.navigate(LoginRoute)
                    },
                    onGoToBoardList = {
                        navController.navigate(BoardListRoute) {
                            popUpTo(BoardListRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<EditBoardRoute> { backStackEntry ->
                val route: EditBoardRoute = backStackEntry.toRoute()
                EditBoardScreen(
                    boardId = route.boardId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<CardDetailsRoute> { backStackEntry ->
                val route: CardDetailsRoute = backStackEntry.toRoute()
                CardDetailsScreen(cardId = route.cardId, onBack = {
                    navController.popBackStack()
                })
            }
        }
    }
}
