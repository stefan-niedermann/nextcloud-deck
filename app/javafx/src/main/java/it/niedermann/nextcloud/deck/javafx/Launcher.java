package it.niedermann.nextcloud.deck.javafx;

import java.util.Arrays;

import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;

public class Launcher {

    static void main(String[] args) {

        final var verbose = Arrays.asList(args).contains("-v");
        final var appComponent = DaggerAppComponent.factory().create(verbose);

        if (args.length == 1 && "--purge".equals(args[0])) {
            // TODO Provide Purge-Button in ExceptionDialog
            appComponent.getPurgeService().purge();
            System.exit(0);
        }

        JavaFxApplication.inject(appComponent.getFxComponentFactory());
        JavaFxApplication.launch(JavaFxApplication.class, args);
    }
}
