package it.niedermann.nextcloud.deck.javafx;

import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;

public class Launcher {

    static void main(String[] args) {

        final var appComponent = DaggerAppComponent.factory().create();

        if (args.length == 1 && "--purge".equals(args[0])) {
            // TODO Provide Purge-Button in ExceptionDialog
            appComponent.getPurgeService().purge();
        }

        JavaFxApplication.inject(appComponent.getFxComponentFactory());
        JavaFxApplication.launch(JavaFxApplication.class, args);
    }
}
