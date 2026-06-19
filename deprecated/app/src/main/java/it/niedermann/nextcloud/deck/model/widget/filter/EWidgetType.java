package it.niedermann.nextcloud.deck.model.widget.filter;

import it.niedermann.nextcloud.deck.ui.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.ui.widget.stack.StackWidget;
import it.niedermann.nextcloud.deck.ui.widget.upcoming.UpcomingWidget;

public enum EWidgetType {
    FILTER_WIDGET(1, FilterWidget.class),
    UPCOMING_WIDGET(2, UpcomingWidget.class),
    STACK_WIDGET(3, StackWidget.class);

    private final int id;
    private final Class<?> widgetClass;

    EWidgetType(int id, Class<?> widgetClass) {
        this.id = id;
        this.widgetClass = widgetClass;
    }

    public static EWidgetType findById(int id) {
        for (EWidgetType s : EWidgetType.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown " + EWidgetType.class.getSimpleName() + " key: " + id);
    }

    public static EWidgetType findByClass(Class<?> clazz) {
        for (EWidgetType s : EWidgetType.values()) {
            if (s.getWidgetClass() == clazz) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown EWidgetType class");
    }

    public int getId() {
        return id;
    }

    public Class<?> getWidgetClass() {
        return widgetClass;
    }
}
