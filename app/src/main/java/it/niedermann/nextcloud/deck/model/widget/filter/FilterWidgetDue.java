package it.niedermann.nextcloud.deck.model.widget.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterWidgetDue {

    private Long id;
    private Long filterBoardId;
    private int dueType;
}
