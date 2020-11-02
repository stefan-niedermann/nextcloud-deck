package it.niedermann.nextcloud.deck.model.widget.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterWidgetBoard {

    private Long id;
    private Long filterAccountId;
    private Long boardId;
}
