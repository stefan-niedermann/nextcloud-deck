package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import com.dlsc.gemsfx.PopOver;

import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Stream;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.GetSyncStatusUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.ListUsersUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.main.MainService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.AvatarProgressView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;

public class HeaderFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(HeaderFeature.class.getName());

    @FXML
    VBox root;
    @FXML
    MenuBar menuBar;
    @FXML
    Menu switchAccountMenu;
    @FXML
    MenuItem syncMenuItem;
    @FXML
    MenuItem addAccountMenuItem;
    @FXML
    MenuItem removeAccountMenuItem;
    @FXML
    MenuItem filterMenuItem;
    @FXML
    MenuItem accountSwitcherMenuItem;
    @FXML
    MenuItem editBoardMenuItem;
    @FXML
    Menu cardMenu;
    @FXML
    MenuItem openCardInNewWindowMenuItem;
    @FXML
    MenuItem assignMenuItem;
    @FXML
    MenuItem unassignMenuItem;
    @FXML
    MenuItem moveMenuItem;
    @FXML
    MenuItem copyMenuItem;
    @FXML
    MenuItem deleteMenuItem;
    @FXML
    MenuItem kanbanViewMenuItem;
    @FXML
    MenuItem ganttViewMenuItem;
    @FXML
    CheckMenuItem headerToggleMenuItem;
    @FXML
    CheckMenuItem menuBarToggleMenuItem;

    @FXML
    HBox headerHBox;
    @FXML
    Circle circle;
    @FXML
    Label boardTitle;
    @FXML
    Button editBoardBtn;
    @FXML
    Button filterBtn;
    @FXML
    SplitMenuButton viewModeBtn;
    @FXML
    MenuItem kanbanMenuItem;
    @FXML
    MenuItem ganttMenuItem;
    @FXML
    Button preferencesBtn;
    @FXML
    Button scheduleSyncBtn;
    @FXML
    AvatarProgressView avatar;
    @FXML
    Button removeAccountBtn;

    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final GetSyncStatusUseCase getSyncStatusUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;
    private final FilterFeature.Factory filterFeatureFactory;
    private final ListLabelsUseCase listLabelsUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ThemeService themeService;
    private final AccountSwitcherFeature.Factory accountSwitcherFactory;
    private final ViewModel viewModel;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    private PopOver filterPopOver;

    @AssistedInject
    public HeaderFeature(
            Inflater inflater,
            GetAccountUseCase getAccountUseCase,
            GetAccountsUseCase getAccountsUseCase,
            GetSyncStatusUseCase getSyncStatusUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase,
            FilterFeature.Factory filterFeatureFactory,
            ListLabelsUseCase listLabelsUseCase,
            ListUsersUseCase listUsersUseCase,
            ThemeService themeService,
            AccountSwitcherFeature.Factory accountSwitcherFactory,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.getSyncStatusUseCase = getSyncStatusUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
        this.filterFeatureFactory = filterFeatureFactory;
        this.listLabelsUseCase = listLabelsUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.themeService = themeService;
        this.accountSwitcherFactory = accountSwitcherFactory;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        HeaderFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var headerVariantDisposable = viewModel.getHeaderVariant()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(variant -> {
                    final boolean isHBox = variant == MainService.HeaderVariant.DIRECT_BUTTONS;
                    final boolean isMenuBar = variant == MainService.HeaderVariant.MENU_BAR;

                    headerHBox.setVisible(isHBox);
                    headerHBox.setManaged(isHBox);
                    headerToggleMenuItem.setSelected(isHBox);

                    menuBar.setVisible(isMenuBar);
                    menuBar.setManaged(isMenuBar);
                    menuBarToggleMenuItem.setSelected(isMenuBar);
                });

        addDisposable(headerVariantDisposable);

        final var accountsDisposable = Flowable.fromPublisher(getAccountsUseCase.execute())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accounts -> {
                    switchAccountMenu.getItems().clear();
                    for (var account : accounts) {
                        final var item = new MenuItem(account.username());
                        item.setOnAction(_ -> viewModel.onAccountSelected(account.id()));
                        switchAccountMenu.getItems().add(item);
                    }
                });

        addDisposable(accountsDisposable);

        final var currentBoardForMenuDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    final boolean boardPresent = board != null;
                    filterMenuItem.setDisable(!boardPresent);
                    editBoardMenuItem.setDisable(!boardPresent);
                });

        addDisposable(currentBoardForMenuDisposable);

        final var cardSelectionDisposable = viewModel.getCardId()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cardId -> cardMenu.setDisable(cardId.isEmpty()));

        addDisposable(cardSelectionDisposable);

        syncMenuItem.setOnAction(_ -> scheduleSyncBtn.fire());
        addAccountMenuItem.setOnAction(_ -> viewModel.onLaunchPreferences(null));
        accountSwitcherMenuItem.setOnAction(_ -> showAccountSwitcher(menuBar));
        removeAccountMenuItem.setOnAction(_ -> removeAccountBtn.fire());
        filterMenuItem.setOnAction(_ -> showFilter(menuBar));
        editBoardMenuItem.setOnAction(_ -> editBoardBtn.fire());

        kanbanViewMenuItem.setOnAction(_ -> viewModel.onViewModeSelected(MainService.ViewMode.KANBAN));
        ganttViewMenuItem.setOnAction(_ -> viewModel.onViewModeSelected(MainService.ViewMode.GANTT));
        headerToggleMenuItem.setOnAction(_ -> viewModel.onToggleHeaderVariant());
        menuBarToggleMenuItem.setOnAction(_ -> viewModel.onToggleHeaderVariant());

        openCardInNewWindowMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(viewModel::onOpenCardInNewWindow)));
        assignMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(viewModel::onAssignCard)));
        unassignMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(viewModel::onUnassignCard)));
        moveMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(id -> viewModel.onMoveCard(id, menuBar))));
        copyMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(id -> viewModel.onCopyCard(id, menuBar))));
        deleteMenuItem.setOnAction(_ -> viewModel.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(viewModel::onDeleteCard)));

        final var currentAccount = viewModel.getAccountId()
                .observeOn(Schedulers.virtual())
                .switchMap(getAccountUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(avatar::setAvatar);

        addDisposable(currentAccount);

        final var syncStatusDisposable = viewModel.getAccountId()
                .switchMap(getSyncStatusUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(optionalSyncStatus -> avatar.setSyncStatus(optionalSyncStatus.orElse(null)));

        addDisposable(syncStatusDisposable);

        final var currentBoardDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    final boolean boardPresent = board != null;
                    boardTitle.setText(boardPresent ? board.title() : "");
                    editBoardBtn.setVisible(boardPresent);
                    editBoardBtn.setManaged(boardPresent);
                    filterBtn.setVisible(boardPresent);
                    filterBtn.setManaged(boardPresent);
                    if (boardPresent) {
                        final String lastEdited = board.lastModified() != null
                                ? board.lastModified().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                                : "unknown";
                        boardTitle.setTooltip(new Tooltip(java.text.MessageFormat.format(resources.getString("header.tooltip.last-edited"),
                                lastEdited,
                                "John Doe")));
                        circle.setFill(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
                    }
                });

        addDisposable(currentBoardDisposable);

        final var viewModeDisposable = viewModel.getViewMode()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(viewMode -> {
                    switch (viewMode) {
                        case KANBAN -> {
                            viewModeBtn.setText(resources.getString("main.view.kanban"));
                            viewModeBtn.setGraphic(new FontIcon("fltfal-list-20"));
                        }
                        case GANTT -> {
                            viewModeBtn.setText(resources.getString("main.view.gantt"));
                            viewModeBtn.setGraphic(new FontIcon("fltfal-clock-20"));
                        }
                    }
                });

        addDisposable(viewModeDisposable);

        kanbanMenuItem.setText(resources.getString("main.view.kanban"));
        ganttMenuItem.setText(resources.getString("main.view.gantt"));
        kanbanMenuItem.setOnAction(_ -> viewModel.onViewModeSelected(MainService.ViewMode.KANBAN));
        ganttMenuItem.setOnAction(_ -> viewModel.onViewModeSelected(MainService.ViewMode.GANTT));
        viewModeBtn.setOnAction(_ -> {
            // Toggle
            final var current = viewModel.getViewMode().blockingFirst();
            viewModel.onViewModeSelected(current == MainService.ViewMode.KANBAN ? MainService.ViewMode.GANTT : MainService.ViewMode.KANBAN);
        });

        editBoardBtn.setOnAction(_ -> {
            var disposable = viewModel.getBoard()
                    .firstElement()
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(viewModel::onEditBoard);
            addDisposable(disposable);
        });

        filterBtn.setOnAction(_ -> showFilter(filterBtn));

        preferencesBtn.setOnAction(_ -> {
            var disposable = viewModel.getAccountId()
                    .firstElement()
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(
                            viewModel::onLaunchPreferences,
                            _ -> viewModel.onLaunchPreferences(null),
                            () -> viewModel.onLaunchPreferences(null)
                    );
            addDisposable(disposable);
        });

        scheduleSyncBtn.setOnAction(_ -> {
            final var disposable = viewModel.getAccountId().firstElement()
                    .flatMapPublisher(accountId -> Flowable.fromPublisher(this.scheduleSyncUseCase.execute(accountId)))
                    .subscribe();

            addDisposable(disposable);
        });
        removeAccountBtn.setOnAction(_ -> this.removeAccount());

        avatar.setOnMouseClicked(_ -> showAccountSwitcher(avatar));
    }

    private void showFilter(javafx.scene.Node anchor) {
        if (filterPopOver != null) {
            filterPopOver.hide();
        }

        final var filterDisposable = Flowable.combineLatest(
                        viewModel.getAccountId().firstOrError().toFlowable(),
                        viewModel.getBoardId().firstOrError().toFlowable(),
                        (accountId, boardId) -> new Object[]{accountId, boardId}
                )
                .flatMap(ids -> {
                    final var accountId = (Account.ID) ids[0];
                    final var boardId = (Board.ID) ids[1];
                    return Flowable.combineLatest(
                            Flowable.fromPublisher(listLabelsUseCase.execute(boardId)).firstOrError().map(Set::stream).map(Stream::toList).toFlowable(),
                            Flowable.fromPublisher(listUsersUseCase.execute(accountId)).firstOrError().toFlowable(),
                            (labels, users) -> {
                                final var initialFilter = viewModel.getFilter().blockingFirst();
                                return filterFeatureFactory.create(initialFilter, labels, users, filter -> {
                                    viewModel.setFilter(filter);
                                    filterPopOver.hide();
                                });
                            }
                    );
                })
                .observeOn(JavaFxScheduler.platform())
                .subscribe(feature -> {
                    filterPopOver = new PopOver(feature.getRoot());
                    filterPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
                    themeService.bind(filterPopOver.getScene());
                    filterPopOver.show(anchor);
                });
        addDisposable(filterDisposable);
    }

    private void showAccountSwitcher(javafx.scene.Node anchor) {
        final var accountSwitcher = accountSwitcherFactory.create();
        final var popover = new PopOver(accountSwitcher.getRoot());
        popover.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        popover.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
        popover.show(anchor);
    }

    public void removeAccount() {
        var disposable = viewModel.getAccountId()
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accountId -> {
                    this.removeAccountUseCase.execute(accountId);
                    this.viewModel.onAccountRemoved();
                });

        addDisposable(disposable);
    }

    public interface ViewModel {
        void onAccountSelected(Account.ID accountId);

        Flowable<Account.ID> getAccountId();

        Flowable<Board.ID> getBoardId();

        Flowable<Board> getBoard();

        Flowable<Optional<it.niedermann.nextcloud.deck.domain.model.Card.ID>> getCardId();

        Flowable<FilterInformation> getFilter();

        void setFilter(FilterInformation filter);

        void onEditBoard(Board board);

        void onLaunchPreferences(Account.ID accountId);

        void onAccountRemoved();

        Flowable<MainService.ViewMode> getViewMode();

        void onViewModeSelected(MainService.ViewMode viewMode);

        Flowable<MainService.HeaderVariant> getHeaderVariant();

        void onToggleHeaderVariant();

        void onOpenCardInNewWindow(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId);

        void onAssignCard(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId);

        void onUnassignCard(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId);

        void onMoveCard(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId, javafx.scene.Node anchor);

        void onCopyCard(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId, javafx.scene.Node anchor);

        void onDeleteCard(it.niedermann.nextcloud.deck.domain.model.Card.ID cardId);
    }
}
