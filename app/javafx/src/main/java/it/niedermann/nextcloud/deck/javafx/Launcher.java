package it.niedermann.nextcloud.deck.javafx;

import com.flexganttfx.core.FlexGanttFX;

import java.util.Arrays;

import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;

public class Launcher {

    static void main(String[] args) {
        FlexGanttFX.setLicenseKey("LIC=DSTASingapore;VEN=DLSC;VER=12;PRO=STANDARD;RUN=yes;CTR=1;SignCode=3F;Signature=302D02150093B5A59029DC2AB77BFE161715AB2C1130E14F4F0214188D8B82FA4AAEA547BF867DE9DAEFCF540140CF");

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
