package it.niedermann.nextcloud.deck.auth.sso;

import android.content.Context;

import androidx.annotation.NonNull;

import java.net.URI;

import it.niedermann.nextcloud.auth.AuthProvider;

public class SsoAuthProvider implements AuthProvider {

    private final Context context;
    private final AuthViewModel viewModel;

    public SsoAuthProvider(@NonNull Context context, @NonNull AuthViewModel viewModel) {
        this.context = context.getApplicationContext();
        this.viewModel = viewModel;
    }

    @Override
    public String generateToken(URI uri, String username, String password) throws Exception {
        return null;
    }

    @Override
    public void invalidateToken(URI uri, String username, String token) throws Exception {
        AuthProvider.super.invalidateToken(uri, username, token);
    }
}
