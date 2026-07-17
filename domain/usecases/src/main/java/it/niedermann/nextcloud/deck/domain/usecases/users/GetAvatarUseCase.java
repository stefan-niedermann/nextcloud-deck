package it.niedermann.nextcloud.deck.domain.usecases.users;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class GetAvatarUseCase {

    private final ApiProvider.Factory apiProviderFactory;
    private final UserRepository userRepository;

    @Inject
    public GetAvatarUseCase(
            ApiProvider.Factory apiProviderFactory,
            UserRepository userRepository
    ) {
        this.apiProviderFactory = apiProviderFactory;
        this.userRepository = userRepository;
    }

    public CompletableFuture<InputStream> execute(User.ID userId, int sizeInPx) {
        return userRepository.getAccountIdByUserId(userId)
                .thenComposeAsync(accountId -> execute(accountId, userId, sizeInPx));
    }

    public CompletableFuture<InputStream> execute(Account.ID accountId, User.ID userId, int sizeInPx) {
        return apiProviderFactory.create(accountId)
                .thenApplyAsync(ApiProvider::getOcsApi)
                .thenComposeAsync(ocsApi -> {
                    final var result = new CompletableFuture<InputStream>();
                    final var call = ocsApi.getAvatar(userId.value(), sizeInPx);
                    call.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try (final var body = response.body()) {
                                    if (body != null) {
                                        try (final var inputStream = body.byteStream()) {
                                            result.complete(inputStream);
                                        } catch (IOException e) {
                                            result.completeExceptionally(e);
                                        }
                                    }
                                } catch (RuntimeException e) {
                                    result.completeExceptionally(e);
                                }
                            } else {
                                try (final var errorBody = response.errorBody()) {
                                    if (errorBody == null) {
                                        result.completeExceptionally(new IOException("Failed to load avatar: HTTP " + response.code()));
                                    } else {
                                        result.completeExceptionally(new IOException(errorBody.string()));
                                    }
                                } catch (IOException e) {
                                    result.completeExceptionally(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                            result.completeExceptionally(t);
                        }
                    });
                    return result;
                });
    }
}