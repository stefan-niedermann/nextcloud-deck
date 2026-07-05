package it.niedermann.nextcloud.deck.javafx.di.fx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FxScope {
}
