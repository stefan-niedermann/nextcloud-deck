package it.niedermann.nextcloud.deck.app.shared.args;

import java.util.concurrent.Flow;

public interface ArgsResolver<TArgs, TParsedArgs> {

    Flow.Publisher<TParsedArgs> resolve(TArgs args);

}