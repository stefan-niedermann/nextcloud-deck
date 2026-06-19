package it.niedermann.nextcloud.deck.javafx.router;

public record RouteConfig<T>(Class<T> controllerClass,
                             RouteContext routeContext,
                             RouteTarget routeTarget) {

    public RouteConfig(Class<T> controllerClass) {
        this(controllerClass, RouteContext.EMPTY);
    }

    public RouteConfig(Class<T> controllerClass, RouteContext routeContext) {
        this(controllerClass, routeContext, RouteTarget.HOST);
    }

    public enum RouteTarget {
        HOST,
        NEW_WINDOW,
        DIALOG
    }
}