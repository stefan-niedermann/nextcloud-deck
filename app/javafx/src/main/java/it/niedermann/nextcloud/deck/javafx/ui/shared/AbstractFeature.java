package it.niedermann.nextcloud.deck.javafx.ui.shared;

import java.net.URL;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.disposables.CompositeDisposable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

public abstract class AbstractFeature implements Initializable, Disposable {

    private final CompositeDisposable disposables = new CompositeDisposable();
    protected final Inflater inflater;
    protected Parent root;
    protected ResourceBundle resources;

    protected AbstractFeature(Inflater inflater) {
        this.inflater = inflater;
        this.resources = ResourceBundle.getBundle("i18n");
    }

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

    public Parent getRoot() {
        if (root == null) {
            root = inflater.inflate(this).view();
        }
        return this.root;
    }
}
