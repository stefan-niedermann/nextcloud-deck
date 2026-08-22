package it.niedermann.nextcloud.deck.domain.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface VirtualDeviceScope {
}
