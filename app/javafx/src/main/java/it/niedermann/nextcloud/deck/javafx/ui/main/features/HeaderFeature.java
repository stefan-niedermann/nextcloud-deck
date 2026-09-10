package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import com.dlsc.gemsfx.PopOver;

import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.export.ExportBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.export.ExportCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.GetSyncStatusUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.ListUsersUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.main.MainService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.AvatarProgressView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;

public class HeaderFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(HeaderFeature.class.getName());

    @FXML
    MenuBar menuBar;
    @FXML
    Menu accountMenu;
    @FXML
    Menu switchAccountMenu;
    @FXML
    MenuItem addAccountMenuItem;
    @FXML
    MenuItem filterMenuItem;
    @FXML
    MenuItem accountSwitcherMenuItem;
    @FXML
    MenuItem editBoardMenuItem;
    @FXML
    Menu exportBoardMenu;
    @FXML
    MenuItem exportBoardCsvMenuItem;
    @FXML
    MenuItem exportBoardMermaidMenuItem;
    @FXML
    MenuItem exportBoardOdtMenuItem;
    @FXML
    MenuItem exportBoardPdfMenuItem;
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
    Menu exportCardMenu;
    @FXML
    MenuItem exportCardPdfMenuItem;
    @FXML
    MenuItem exportCardOdtMenuItem;
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
    Label accountDisplayName;
    @FXML
    Button editBoardBtn;
    @FXML
    Button filterBtn;
    @FXML
    MenuButton exportBtn;
    @FXML
    CustomMenuItem exportBoardHeader;
    @FXML
    MenuItem exportCsvBtn;
    @FXML
    MenuItem exportMermaidBtn;
    @FXML
    MenuItem exportOdtBtn;
    @FXML
    MenuItem exportPdfBtn;
    @FXML
    SeparatorMenuItem exportSeparator;
    @FXML
    CustomMenuItem exportCardHeader;
    @FXML
    MenuItem exportCardPdfBtn;
    @FXML
    MenuItem exportCardOdtBtn;
    @FXML
    SplitMenuButton viewModeBtn;
    @FXML
    MenuItem kanbanMenuItem;
    @FXML
    MenuItem ganttMenuItem;
    @FXML
    Button preferencesBtn;
    @FXML
    AvatarProgressView avatar;

    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final GetSyncStatusUseCase getSyncStatusUseCase;
    private final ExportBoardUseCase exportBoardUseCase;
    private final ExportCardUseCase exportCardUseCase;
    private final FilterFeature.Factory filterFeatureFactory;
    private final ListLabelsUseCase listLabelsUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ThemeService themeService;
    private final AccountSwitcherFeature.Factory accountSwitcherFactory;
    private final MainService mainService;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    private PopOver filterPopOver;

    @AssistedInject
    public HeaderFeature(
            Inflater inflater,
            GetAccountUseCase getAccountUseCase,
            GetAccountsUseCase getAccountsUseCase,
            GetSyncStatusUseCase getSyncStatusUseCase,
            ExportBoardUseCase exportBoardUseCase,
            ExportCardUseCase exportCardUseCase,
            FilterFeature.Factory filterFeatureFactory,
            ListLabelsUseCase listLabelsUseCase,
            ListUsersUseCase listUsersUseCase,
            ThemeService themeService,
            AccountSwitcherFeature.Factory accountSwitcherFactory,
            @Assisted MainService mainService
    ) {
        super(inflater);

        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.getSyncStatusUseCase = getSyncStatusUseCase;
        this.exportBoardUseCase = exportBoardUseCase;
        this.exportCardUseCase = exportCardUseCase;
        this.filterFeatureFactory = filterFeatureFactory;
        this.listLabelsUseCase = listLabelsUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.themeService = themeService;
        this.accountSwitcherFactory = accountSwitcherFactory;
        this.mainService = mainService;
    }

    @AssistedFactory
    public interface Factory {
        HeaderFeature create(MainService mainService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var headerVariantDisposable = mainService.getHeaderVariant()
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
                        final var item = new MenuItem(account.displayName());
                        item.setOnAction(_ -> mainService.onAccountSelected(account.id()));
                        switchAccountMenu.getItems().add(item);
                    }
                });

        addDisposable(accountsDisposable);

        final var currentBoardForMenuDisposable = mainService.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    final boolean boardPresent = board != null;
                    filterMenuItem.setDisable(!boardPresent);
                    editBoardMenuItem.setDisable(!boardPresent);
                });

        addDisposable(currentBoardForMenuDisposable);

        final var cardSelectionDisposable = mainService.getCardId()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cardId -> {
                    final boolean cardPresent = cardId.isPresent();
                    cardMenu.setDisable(!cardPresent);
                    updateExportButtonItems(cardPresent);
                });

        addDisposable(cardSelectionDisposable);

        addAccountMenuItem.setOnAction(_ -> mainService.onLaunchPreferences(null));
        accountSwitcherMenuItem.setOnAction(_ -> showAccountSwitcher(menuBar));
        filterMenuItem.setOnAction(_ -> showFilter(menuBar));
        editBoardMenuItem.setOnAction(_ -> editBoardBtn.fire());

        kanbanViewMenuItem.setOnAction(_ -> mainService.onViewModeSelected(MainService.ViewMode.KANBAN));
        ganttViewMenuItem.setOnAction(_ -> mainService.onViewModeSelected(MainService.ViewMode.GANTT));
        headerToggleMenuItem.setOnAction(_ -> mainService.onToggleHeaderVariant());
        menuBarToggleMenuItem.setOnAction(_ -> mainService.onToggleHeaderVariant());

        openCardInNewWindowMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(mainService::onOpenCardInNewWindow))));
        assignMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(mainService::onAssignCard))));
        unassignMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(mainService::onUnassignCard))));
        moveMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(id -> mainService.onMoveCard(id, menuBar)))));
        copyMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(id -> mainService.onCopyCard(id, menuBar)))));
        deleteMenuItem.setOnAction(_ -> addDisposable(mainService.getCardId().firstElement().subscribe(cardId -> cardId.ifPresent(mainService::onDeleteCard))));

        final var currentAccount = mainService.getAccountId()
                .observeOn(Schedulers.virtual())
                .switchMap(getAccountUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(account -> {
                    avatar.setAvatar(account);
                    accountDisplayName.setText(account.displayName());
                    accountMenu.setText(account.displayName());
                });

        addDisposable(currentAccount);

        final var syncStatusDisposable = mainService.getAccountId()
                .switchMap(getSyncStatusUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(optionalSyncStatus -> avatar.setSyncStatus(optionalSyncStatus.orElse(null)));

        addDisposable(syncStatusDisposable);

        final var currentBoardDisposable = mainService.getOptionalBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(optionalBoard -> {
                    final boolean boardPresent = optionalBoard.isPresent();
                    final var board = optionalBoard.orElse(null);
                    boardTitle.setText(boardPresent ? board.title() : "");
                    editBoardBtn.setVisible(boardPresent);
                    editBoardBtn.setManaged(boardPresent);
                    filterBtn.setVisible(boardPresent);
                    filterBtn.setManaged(boardPresent);
                    exportBtn.setVisible(boardPresent);
                    exportBtn.setManaged(boardPresent);
                    exportBoardMenu.setDisable(!boardPresent);
                    if (boardPresent) {
                        final String lastEdited = board.lastModified() != null
                                ? board.lastModified().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                                : "unknown";
                        boardTitle.setTooltip(new Tooltip(MessageFormat.format(resources.getString("header.tooltip.last-edited"),
                                lastEdited,
                                "John Doe")));
                        circle.setFill(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
                        circle.setVisible(true);
                        circle.setManaged(true);
                    } else {
                        circle.setVisible(false);
                        circle.setManaged(false);
                    }
                });

        addDisposable(currentBoardDisposable);

        exportBoardCsvMenuItem.setOnAction(_ -> exportBoardAsCsv());
        exportBoardMermaidMenuItem.setOnAction(_ -> exportBoardAsMermaid());
        exportBoardOdtMenuItem.setOnAction(_ -> exportBoardAsOdt());
        exportBoardPdfMenuItem.setOnAction(_ -> exportBoardAsPdf());
        exportCsvBtn.setOnAction(_ -> exportBoardAsCsv());
        exportMermaidBtn.setOnAction(_ -> exportBoardAsMermaid());
        exportOdtBtn.setOnAction(_ -> exportBoardAsOdt());
        exportPdfBtn.setOnAction(_ -> exportBoardAsPdf());

        exportCardPdfMenuItem.setOnAction(_ -> exportCardAsPdf());
        exportCardOdtMenuItem.setOnAction(_ -> exportCardAsOdt());
        exportCardPdfBtn.setOnAction(_ -> exportCardAsPdf());
        exportCardOdtBtn.setOnAction(_ -> exportCardAsOdt());

        final var viewModeDisposable = mainService.getViewMode()
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
        kanbanMenuItem.setOnAction(_ -> mainService.onViewModeSelected(MainService.ViewMode.KANBAN));
        ganttMenuItem.setOnAction(_ -> mainService.onViewModeSelected(MainService.ViewMode.GANTT));
        viewModeBtn.setOnAction(_ -> {
            // Toggle
            final var current = mainService.getViewMode().blockingFirst();
            mainService.onViewModeSelected(current == MainService.ViewMode.KANBAN ? MainService.ViewMode.GANTT : MainService.ViewMode.KANBAN);
        });

        editBoardBtn.setOnAction(_ -> {
            var disposable = mainService.getBoard()
                    .firstElement()
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(mainService::onEditBoard);
            addDisposable(disposable);
        });

        filterBtn.setOnAction(_ -> showFilter(filterBtn));

        preferencesBtn.setOnAction(_ -> {
            var disposable = mainService.getAccountId()
                    .firstElement()
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(
                            mainService::onLaunchPreferences,
                            _ -> mainService.onLaunchPreferences(null),
                            () -> mainService.onLaunchPreferences(null)
                    );
            addDisposable(disposable);
        });

        avatar.setFocusTraversable(true);
        avatar.setOnMouseClicked(_ -> showAccountSwitcher(avatar));
        avatar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                showAccountSwitcher(avatar);
                event.consume();
            }
        });
        accountDisplayName.setOnMouseClicked(avatar::fireEvent);

        exportBoardHeader.setHideOnClick(false);
        exportCardHeader.setHideOnClick(false);
        exportBoardHeader.setDisable(true);
        exportCardHeader.setDisable(true);
    }

    private void showFilter(Node anchor) {
        if (filterPopOver != null) {
            filterPopOver.hide();
        }

        final var filterDisposable = Flowable.combineLatest(
                        mainService.getAccountId().firstOrError().toFlowable(),
                        mainService.getBoardId().firstOrError().toFlowable(),
                        (accountId, boardId) -> new Object[]{accountId, boardId}
                )
                .flatMap(ids -> {
                    final var accountId = (Account.ID) ids[0];
                    final var boardId = (Board.ID) ids[1];
                    return Flowable.combineLatest(
                            Flowable.fromPublisher(listLabelsUseCase.execute(boardId)).firstOrError().map(Set::stream).map(Stream::toList).toFlowable(),
                            Flowable.fromPublisher(listUsersUseCase.execute(accountId)).firstOrError().toFlowable(),
                            (labels, users) -> {
                                final var initialFilter = mainService.getFilter().blockingFirst();
                                return filterFeatureFactory.create(initialFilter, labels, users, filter -> {
                                    mainService.setFilter(filter);
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

    private void showAccountSwitcher(Node anchor) {
        final var accountSwitcher = accountSwitcherFactory.create(mainService);
        final var popover = new PopOver(accountSwitcher.getRoot());
        popover.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        popover.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
        popover.show(anchor);
    }

    private void updateExportButtonItems(boolean cardPresent) {
        exportBtn.getItems().clear();
        exportBtn.getItems().add(exportBoardHeader);
        exportBtn.getItems().addAll(exportCsvBtn, exportMermaidBtn, exportOdtBtn, exportPdfBtn);
        if (cardPresent) {
            exportBtn.getItems().add(exportSeparator);
            exportBtn.getItems().add(exportCardHeader);
            exportBtn.getItems().addAll(exportCardPdfBtn, exportCardOdtBtn);
        }
    }

    private void exportBoardAsCsv() {
        addDisposable(mainService.getBoardId().firstElement().subscribe(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.board"), "board.csv", new FileChooser.ExtensionFilter("CSV", "*.csv"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportBoardUseCase.toCsv(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export board to CSV", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        }));
    }

    private void exportBoardAsMermaid() {
        addDisposable(mainService.getBoardId().firstElement().subscribe(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.board"), "board.mmd", new FileChooser.ExtensionFilter("Mermaid", "*.mmd"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportBoardUseCase.toMermaid(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export board to Mermaid", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        }));
    }

    private void exportBoardAsOdt() {
        addDisposable(mainService.getBoardId().firstElement().subscribe(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.board"), "board.fodt", new FileChooser.ExtensionFilter("OpenDocument Text (Flat XML)", "*.fodt"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportBoardUseCase.toOdt(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export board to ODT", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        }));
    }

    private void exportBoardAsPdf() {
        addDisposable(mainService.getBoardId().firstElement().subscribe(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.board"), "board.pdf", new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportBoardUseCase.toPdf(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.write(file.toPath(), content);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export board to PDF", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        }));
    }

    private void exportCardAsPdf() {
        addDisposable(mainService.getCardId().firstElement().subscribe(optionalId -> optionalId.ifPresent(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.card"), "card.pdf", new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportCardUseCase.toPdf(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.write(file.toPath(), content);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export card to PDF", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        })));
    }

    private void exportCardAsOdt() {
        addDisposable(mainService.getCardId().firstElement().subscribe(optionalId -> optionalId.ifPresent(id -> {
            final File file = showFileChooser(resources.getString("export.chooser.card"), "card.fodt", new FileChooser.ExtensionFilter("OpenDocument Text (Flat XML)", "*.fodt"));
            if (file != null) {
                addDisposable(Flowable.fromPublisher(exportCardUseCase.toOdt(id))
                        .observeOn(Schedulers.virtual())
                        .subscribe(content -> {
                            try {
                                Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Failed to export card to ODT", e);
                            }
                        }, e -> logger.log(Level.SEVERE, "Export failed", e)));
            }
        })));
    }

    private File showFileChooser(String title, String initialFileName, FileChooser.ExtensionFilter filter) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(initialFileName);
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser.showSaveDialog(root.getScene().getWindow());
    }
}
