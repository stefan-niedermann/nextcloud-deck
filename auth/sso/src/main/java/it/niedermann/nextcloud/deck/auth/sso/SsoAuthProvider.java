package it.niedermann.nextcloud.deck.auth.sso;

import android.content.Context;

import androidx.annotation.NonNull;

import java.net.URI;

public class SsoAuthProvider {

    private final Context context;
    private final AuthViewModel viewModel;

    public SsoAuthProvider(@NonNull Context context, @NonNull AuthViewModel viewModel) {
        this.context = context.getApplicationContext();
        this.viewModel = viewModel;
    }

    public String generateToken(URI uri, String username, String password) throws Exception {
        return null;
    }

    public void invalidateToken(URI uri, String username, String token) throws Exception {

    }
}
