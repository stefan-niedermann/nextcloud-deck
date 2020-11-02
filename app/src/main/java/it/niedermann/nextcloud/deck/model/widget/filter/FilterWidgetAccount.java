package it.niedermann.nextcloud.deck.model.widget.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterWidgetAccount {

    private Long id;
    private Long filterWidgetId;
    private Long accountId;
}
