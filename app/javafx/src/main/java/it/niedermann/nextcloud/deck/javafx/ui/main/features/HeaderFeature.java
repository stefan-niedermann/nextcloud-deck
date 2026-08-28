package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import com.dlsc.gemsfx.PopOver;

import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;

public class HeaderFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(HeaderFeature.class.getName());

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
                        boardTitle.setTooltip(new Tooltip(String.format("Last edited at %1$s by %2$s",
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

        filterBtn.setOnAction(event -> {
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
                        filterPopOver.show(filterBtn);
                    });
            addDisposable(filterDisposable);
        });

        preferencesBtn.setOnAction(_ -> viewModel.onLaunchPreferences());

        scheduleSyncBtn.setOnAction(_ -> {
            final var disposable = viewModel.getAccountId().firstElement()
                    .flatMapPublisher(accountId -> Flowable.fromPublisher(this.scheduleSyncUseCase.execute(accountId)))
                    .subscribe();

            addDisposable(disposable);
        });
        removeAccountBtn.setOnAction(_ -> this.removeAccount());

        avatar.setOnMouseClicked(event -> {
            final var accountSwitcher = accountSwitcherFactory.create();
            final var popover = new PopOver(accountSwitcher.getRoot());
            popover.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
            popover.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
            popover.show(avatar);
            event.consume();
        });
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

        Flowable<FilterInformation> getFilter();

        void setFilter(FilterInformation filter);

        void onEditBoard(Board board);

        void onLaunchPreferences();

        void onAccountRemoved();

        Flowable<MainService.ViewMode> getViewMode();

        void onViewModeSelected(MainService.ViewMode viewMode);
    }
}
