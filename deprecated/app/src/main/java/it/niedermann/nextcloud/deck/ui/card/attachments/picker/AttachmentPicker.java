package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AttachmentPicker<I, O> implements DefaultLifecycleObserver {

    @StringRes
    public final int label;
    @DrawableRes
    public final int icon;
    @NonNull
    private final Collection<String> permissions = new HashSet<>();
    @NonNull
    private final ActivityResultRegistry registry;
    @NonNull
    private final ActivityResultContract<I, O> contract;
    @Nullable
    private final I input;
    @NonNull
    private final Function<O, List<Uri>> outputMapper;
    @Nullable
    private ActivityResultLauncher<I> launcher = null;
    @Nullable
    private ActivityResultLauncher<String[]> permissionLauncher = null;
    @NonNull
    private CompletableFuture<O> future = new CompletableFuture<>();

    protected AttachmentPicker(@NonNull ActivityResultRegistry registry,
                               @StringRes int label,
                               @DrawableRes int icon,
                               @NonNull ActivityResultContract<I, O> contract,
                               @NonNull Collection<String> permissions,
                               @Nullable I input,
                               @NonNull Function<O, List<Uri>> outputMapper) {
        this.registry = registry;
        this.label = label;
        this.icon = icon;
        this.contract = contract;
        this.permissions.addAll(sanitizePermissions(permissions));
        this.input = input;
        this.outputMapper = outputMapper;

        this.future.cancel(true);
    }

    private Collection<String> sanitizePermissions(@NonNull Collection<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return permissions;
        } else {
            return permissions
                    .stream()
                    .filter(permission -> !Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission))
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        launcher = registry.register(getClass().getCanonicalName() + "_launcher_" + this, owner, contract, this::handleLauncherResult);
        permissionLauncher = registry.register(getClass().getCanonicalName() + "_permissions_" + this, owner, new ActivityResultContracts.RequestMultiplePermissions(), this::handlePermissionResult);
    }

    public CompletableFuture<List<Uri>> ensurePermissionsAndLaunchPicker(@NonNull Context context) {
        // TODO Thread safety?
        if (future.isDone()) {
            future = new CompletableFuture<>();

            if (hasPermissions(context)) {
                launchPicker();
            } else if (permissionLauncher == null) {
                future.completeExceptionally(new IllegalStateException("permissionLauncher is null"));
            } else {
                permissionLauncher.launch(permissions.toArray(new String[0]));
            }
        }

        return future.thenApply(outputMapper);
    }

    public boolean targetAppExists(@NonNull Context context) {
        if (launcher == null) {
            throw new IllegalStateException("This method must be called after onCreate");
        }

        return context.getPackageManager().resolveActivity(launcher.getContract().createIntent(context, input), 0) != null;
    }

    private void launchPicker() {
        if (launcher == null) {
            future.completeExceptionally(new IllegalStateException("This method must be called after onCreate"));
        } else {
            try {
                launcher.launch(this.input);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }
    }

    private void handleLauncherResult(@Nullable O output) {
        if (future.isDone()) {
            throw new IllegalStateException("Expected future not to be done yet.");
        }

        try {
            future.complete(output);
        } catch (UnsupportedOperationException e) {
            future.completeExceptionally(e);
        }
    }

    private void handlePermissionResult(@NonNull Map<String, Boolean> permissionMap) {
        final var missingPermissions = permissionMap
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (missingPermissions.isEmpty()) {
            launchPicker();
        } else {
            future.completeExceptionally(new SecurityException("Missing permissions: " + String.join(", ", missingPermissions)));
        }
    }

    private boolean hasPermissions(@NonNull Context context) {
        return this.permissions.stream().allMatch(permission -> hasPermission(context, permission));
    }

    private boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        return context.checkSelfPermission(permission) == PERMISSION_GRANTED;
    }

    public static abstract class Builder<I, O> {
        @NonNull
        protected ActivityResultRegistry registry;
        @StringRes
        protected int label;
        @DrawableRes
        protected int icon;
        @NonNull
        protected ActivityResultContract<I, O> contract;
        @Nullable
        protected Collection<String> permissions;
        @Nullable
        protected I input;
        @Nullable
        protected Function<O, O> resultMapper;

        Builder(
                @NonNull ActivityResultRegistry registry,
                @StringRes int label,
                @DrawableRes int icon,
                @NonNull ActivityResultContract<I, O> contract
        ) {
            this.registry = registry;
            this.label = label;
            this.icon = icon;
            this.contract = contract;
        }

        public abstract AttachmentPicker<I, O> build();

        public Builder<I, O> setPermissions(@NonNull Collection<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder<I, O> setPermissions(@NonNull String permission) {
            return setPermissions(Collections.singleton(permission));
        }

        public Builder<I, O> setInput(@Nullable I input) {
            this.input = input;
            return this;
        }

        public Builder<I, O> setResultMapper(@Nullable Function<O, O> resultMapper) {
            this.resultMapper = resultMapper;
            return this;
        }

        public Builder<I, O> setResultMapper(@Nullable Consumer<O> resultMapper) {
            return setResultMapper(resultMapper == null ? null : output -> {
                resultMapper.accept(output);
                return output;
            });
        }

        @NonNull
        protected Collection<String> getPermissions() {
            return permissions == null ? Collections.emptyList() : new HashSet<>(permissions);
        }

        @NonNull
        protected abstract Function<O, List<Uri>> getResultTypeMapper();

        @NonNull
        protected Function<O, List<Uri>> getOutputMapper() {
            return resultMapper == null
                    ? getResultTypeMapper()
                    : getResultTypeMapper().compose(resultMapper);
        }
    }

    public static final class SingleBuilder<I> extends Builder<I, Uri> {

        public SingleBuilder(
                @NonNull ActivityResultRegistry registry,
                @StringRes int label,
                @DrawableRes int icon,
                @NonNull ActivityResultContract<I, Uri> contract
        ) {
            super(registry, label, icon, contract);
        }

        @Override
        public AttachmentPicker<I, Uri> build() {
            return new AttachmentPicker<>(registry,
                    label,
                    icon,
                    contract,
                    getPermissions(),
                    input,
                    getOutputMapper());
        }

        @NonNull
        @Override
        protected Function<Uri, List<Uri>> getResultTypeMapper() {
            return output -> output == null ? Collections.emptyList() : Collections.singletonList(output);
        }
    }

    public static final class MultiBuilder<I> extends Builder<I, List<Uri>> {

        public MultiBuilder(
                @NonNull ActivityResultRegistry registry,
                @StringRes int label,
                @DrawableRes int icon,
                @NonNull ActivityResultContract<I, List<Uri>> contract
        ) {
            super(registry, label, icon, contract);
        }

        @Override
        public AttachmentPicker<I, List<Uri>> build() {
            return new AttachmentPicker<>(registry,
                    label,
                    icon,
                    contract,
                    getPermissions(),
                    input,
                    getOutputMapper());
        }

        @NonNull
        @Override
        protected Function<List<Uri>, List<Uri>> getResultTypeMapper() {
            return output -> output == null ? Collections.emptyList() : new ArrayList<>(output);
        }
    }
}