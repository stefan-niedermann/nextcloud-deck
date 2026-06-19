package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.disposables.CompositeDisposable;
import io.reactivex.rxjava4.disposables.Disposable;
import javafx.fxml.Initializable;

public abstract class DisposableController implements Initializable, Disposable {

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
    }

    protected void addDisposable(Disposable... disposables) {
        for (var disposable : disposables) {
            this.disposables.add(disposable);
        }
    }

    @Override
    public void dispose() {
        disposables.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposables.isDisposed();
    }
}
