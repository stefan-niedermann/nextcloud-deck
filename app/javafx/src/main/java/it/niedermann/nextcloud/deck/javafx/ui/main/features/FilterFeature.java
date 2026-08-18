package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class FilterFeature extends AbstractScene {

    @FXML
    private ListView<Label> labelList;
    @FXML
    private ListView<User> userList;
    @FXML
    private ToggleGroup doneGroup;
    @FXML
    private RadioButton doneAll;
    @FXML
    private RadioButton doneDone;
    @FXML
    private RadioButton doneNotDone;
    @FXML
    private ToggleGroup dueGroup;
    @FXML
    private RadioButton dueAll;
    @FXML
    private RadioButton dueOverdue;
    @FXML
    private RadioButton dueToday;
    @FXML
    private RadioButton dueNext7;
    @FXML
    private RadioButton dueNext30;
    @FXML
    private RadioButton dueNone;
    @FXML
    private Button reset;
    @FXML
    private Button apply;

    private final FilterInformation initialFilter;
    private final List<Label> labels;
    private final List<User> users;
    private final Consumer<FilterInformation> onApply;

    private final Set<Label.ID> selectedLabelIds = new HashSet<>();
    private final Set<User.ID> selectedUserIds = new HashSet<>();

    @AssistedInject
    public FilterFeature(
            @Assisted FilterInformation initialFilter,
            @Assisted List<Label> labels,
            @Assisted List<User> users,
            @Assisted Consumer<FilterInformation> onApply
    ) {
        this.initialFilter = initialFilter;
        this.labels = labels;
        this.users = users;
        this.onApply = onApply;
    }

    @AssistedFactory
    public interface Factory {
        FilterFeature create(FilterInformation initialFilter, List<Label> labels, List<User> users, Consumer<FilterInformation> onApply);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        selectedLabelIds.addAll(initialFilter.labelIds());
        selectedUserIds.addAll(initialFilter.assigneeIds());

        labelList.setCellFactory(_ -> new CheckBoxListCell<>(selectedLabelIds, Label::id, Label::title));
        labelList.getItems().setAll(labels);

        userList.setCellFactory(_ -> new CheckBoxListCell<>(selectedUserIds, User::id, User::displayName));
        userList.getItems().setAll(users);

        setDoneState(initialFilter.doneState());
        setDueDateFilter(initialFilter.dueDateFilter());

        apply.setOnAction(_ -> {
            onApply.accept(new FilterInformation(
                    Set.copyOf(selectedLabelIds),
                    Set.copyOf(selectedUserIds),
                    getDoneState(),
                    getDueDateFilter()
            ));
        });

        reset.setOnAction(_ -> {
            onApply.accept(FilterInformation.EMPTY);
        });
    }

    private void setDoneState(FilterInformation.DoneState doneState) {
        switch (doneState) {
            case ALL -> doneAll.setSelected(true);
            case DONE -> doneDone.setSelected(true);
            case NOT_DONE -> doneNotDone.setSelected(true);
        }
    }

    private FilterInformation.DoneState getDoneState() {
        if (doneDone.isSelected()) return FilterInformation.DoneState.DONE;
        if (doneNotDone.isSelected()) return FilterInformation.DoneState.NOT_DONE;
        return FilterInformation.DoneState.ALL;
    }

    private void setDueDateFilter(FilterInformation.DueDateFilter dueDateFilter) {
        switch (dueDateFilter) {
            case ALL -> dueAll.setSelected(true);
            case OVERDUE -> dueOverdue.setSelected(true);
            case TODAY -> dueToday.setSelected(true);
            case NEXT_7_DAYS -> dueNext7.setSelected(true);
            case NEXT_30_DAYS -> dueNext30.setSelected(true);
            case NO_DUE_DATE -> dueNone.setSelected(true);
        }
    }

    private FilterInformation.DueDateFilter getDueDateFilter() {
        if (dueOverdue.isSelected()) return FilterInformation.DueDateFilter.OVERDUE;
        if (dueToday.isSelected()) return FilterInformation.DueDateFilter.TODAY;
        if (dueNext7.isSelected()) return FilterInformation.DueDateFilter.NEXT_7_DAYS;
        if (dueNext30.isSelected()) return FilterInformation.DueDateFilter.NEXT_30_DAYS;
        if (dueNone.isSelected()) return FilterInformation.DueDateFilter.NO_DUE_DATE;
        return FilterInformation.DueDateFilter.ALL;
    }

    private static class CheckBoxListCell<T, ID> extends ListCell<T> {
        private final CheckBox checkBox = new CheckBox();
        private final Set<ID> selectedIds;
        private final java.util.function.Function<T, ID> idExtractor;
        private final java.util.function.Function<T, String> nameExtractor;

        public CheckBoxListCell(Set<ID> selectedIds, java.util.function.Function<T, ID> idExtractor, java.util.function.Function<T, String> nameExtractor) {
            this.selectedIds = selectedIds;
            this.idExtractor = idExtractor;
            this.nameExtractor = nameExtractor;
            checkBox.setOnAction(_ -> {
                T item = getItem();
                if (item != null) {
                    ID id = idExtractor.apply(item);
                    if (checkBox.isSelected()) {
                        selectedIds.add(id);
                    } else {
                        selectedIds.remove(id);
                    }
                }
            });
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                checkBox.setSelected(selectedIds.contains(idExtractor.apply(item)));
                setText(nameExtractor.apply(item));
                setGraphic(checkBox);
            }
        }
    }
}
