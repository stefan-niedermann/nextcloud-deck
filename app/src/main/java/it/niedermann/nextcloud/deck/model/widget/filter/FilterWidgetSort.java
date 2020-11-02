package it.niedermann.nextcloud.deck.model.widget.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterWidgetSort {

    private Long id;
    private Long filterBoardId;
    private boolean direction;
    private int criteria;
    private int ruleOrder;
}
