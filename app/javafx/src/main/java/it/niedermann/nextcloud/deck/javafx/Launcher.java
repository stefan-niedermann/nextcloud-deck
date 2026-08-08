package it.niedermann.nextcloud.deck.javafx;

import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;

public class Launcher {

    static void main(String[] args) {
        if (args.length == 1 && "--purge".equals(args[0])) {
            final var appComponent = DaggerAppComponent.factory().create();
            // TODO Provide Purge-Button in ExceptionDialog
            appComponent.getPurgeService().purge();
        }

        final var appComponent = DaggerAppComponent.factory().create();
        JavaFxApplication.inject(appComponent.getFxComponentFactory());
        JavaFxApplication.launch(JavaFxApplication.class, args);
    }
}
