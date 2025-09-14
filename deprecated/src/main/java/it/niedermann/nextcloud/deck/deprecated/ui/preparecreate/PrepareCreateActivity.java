package it.niedermann.nextcloud.deck.deprecated.ui.preparecreate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.sso.api.EmptyResponse;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.deprecated.ui.PickStackActivity;
import it.niedermann.nextcloud.deck.deprecated.ui.card.EditActivity;
import okhttp3.Headers;

public class PrepareCreateActivity extends PickStackActivity {

    private PrepareCreateViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final var actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_card);
        }

        viewModel = new ViewModelProvider(this).get(PrepareCreateViewModel.class);
    }

    @Override
    protected void onSubmit(Account account, long boardId, long stackId, @NonNull IResponseCallback<EmptyResponse> callback) {
        Toast.makeText(this, R.string.saving_new_card, Toast.LENGTH_SHORT).show();
        final FullCard fullCard;
        if (requireContent()) {
            fullCard = viewModel.createFullCard(account.getServerDeckVersionAsObject(), getContent());
        } else {
            final var intent = getIntent();
            if (intent == null) {
                throw new IllegalStateException("Intent should not be null because title is required.");
            }
            fullCard = viewModel.createFullCard(
                    account.getServerDeckVersionAsObject(),
                    intent.getStringExtra(Intent.EXTRA_SUBJECT),
                    intent.getStringExtra(Intent.EXTRA_TITLE),
                    intent.getStringExtra(Intent.EXTRA_TEXT)
            );
        }

        viewModel.saveCard(account, boardId, stackId, fullCard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullCard response, Headers headers) {
                viewModel.saveCurrentAccount(account);
                viewModel.saveCurrentBoardId(account.getId(), boardId);
                viewModel.saveCurrentStackId(account.getId(), boardId, stackId);

                callback.onResponse(null, headers);
                startActivity(EditActivity.createEditCardIntent(PrepareCreateActivity.this, account, boardId, response.getLocalId()));
                finish();
            }

            @Override
            @SuppressLint("MissingSuperCall")
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return false;
    }

    @Override
    protected boolean requireContent() {
        final var intent = getIntent();
        return intent == null || (TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_SUBJECT)) &&
                TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_TITLE)) &&
                TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_TEXT)));
    }

}