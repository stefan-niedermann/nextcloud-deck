package it.niedermann.nextcloud.deck.model.widget.filter;

import it.niedermann.nextcloud.deck.ui.widget.upcoming.UpcomingWidget;

public enum EWidgetType {
    UPCOMING_CARDS_WIDGET(1, UpcomingWidget.class);


    private int id;
    private Class<?> widgetClass;

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
        throw new IllegalArgumentException("unknown EWidgetType key");
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
