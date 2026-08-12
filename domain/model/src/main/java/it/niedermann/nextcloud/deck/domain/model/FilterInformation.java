package it.niedermann.nextcloud.deck.domain.model;

import java.util.Collections;
import java.util.Set;

public record FilterInformation(
        Set<Label.ID> labelIds,
        Set<User.ID> assigneeIds,
        DoneState doneState,
        DueDateFilter dueDateFilter
) {
    public static final FilterInformation EMPTY = new FilterInformation(
            Collections.emptySet(),
            Collections.emptySet(),
            DoneState.ALL,
            DueDateFilter.ALL
    );

    public enum DoneState {
        ALL, DONE, NOT_DONE
    }

    public enum DueDateFilter {
        ALL, OVERDUE, TODAY, NEXT_7_DAYS, NEXT_30_DAYS, NO_DUE_DATE
    }
}
