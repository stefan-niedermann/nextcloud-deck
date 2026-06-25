package it.niedermann.nextcloud.deck.javafx.di.stage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface StageScope {
}
