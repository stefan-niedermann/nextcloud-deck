package it.niedermann.nextcloud.deck.javafx.ui.controller;

import io.reactivex.rxjava4.core.Flowable;

public interface TitleReportable {
    Flowable<String> getTitle();
}
