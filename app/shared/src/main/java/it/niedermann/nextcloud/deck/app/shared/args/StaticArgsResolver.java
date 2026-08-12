package it.niedermann.nextcloud.deck.app.shared.args;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;

public class StaticArgsResolver<TArgs> implements ArgsResolver<TArgs, TArgs> {
    @Override
    public Flow.Publisher<TArgs> resolve(TArgs args) {
        return FlowAdapters.toFlowPublisher(Flowable.just(args));
    }
}
