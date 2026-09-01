package it.niedermann.nextcloud.deck.domain.e2e;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@SuppressWarnings("CallToPrintStackTrace")
public class ServerManager implements AutoCloseable {

    private static final String USER_PREFIX = "testuser_";
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final URL adminUrl;
    private final String adminUsername;
    private final String adminPassword;

    private final Map<String, RemoteAccount> remoteAccounts = new ConcurrentHashMap<>();

    public ServerManager(
            OkHttpClient httpClient,
            Gson gson,
            URL adminUrl,
            String adminUsername,
            String adminPassword) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.adminUrl = adminUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;

        cleanupIfFirstRun();
    }

    private void cleanupIfFirstRun() {
        final var lockFile = Path.of("build", "e2e_cleanup.lock");

        try {
            Files.createDirectories(lockFile.getParent());
            Files.createFile(lockFile);
            cleanupStaleUsers();

        } catch (IOException e) {
            // Ignore error, just skip cleanup if we can't create the lock file
            System.err.println("Could not create lock file for E2E cleanup: " + e.getMessage());
        }
    }

    private void cleanupStaleUsers() {
        System.out.println("Cleaning up stale E2E users...");
        final var request = new Request.Builder()
                .url(adminUrl.toString() + "/ocs/v2.php/cloud/users?search=" + USER_PREFIX + "&format=json")
                .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                .header("OCS-APIRequest", "true")
                .get()
                .build();

        try (var response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                final var json = gson.fromJson(response.body().string(), JsonObject.class);
                final var users = json.getAsJsonObject("ocs")
                        .getAsJsonObject("data")
                        .getAsJsonArray("users");
                if (users != null) {
                    for (var userElement : users) {
                        final String username = userElement.getAsString();
                        if (username.startsWith(USER_PREFIX)) {
                            deleteUser(username);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to cleanup stale users: " + e.getMessage());
        }
    }

    private void deleteUser(String username) {
        final var request = new Request.Builder()
                .url(adminUrl.toString() + "/ocs/v2.php/cloud/users/" + username + "?format=json")
                .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                .header("OCS-APIRequest", "true")
                .delete()
                .build();
        try (var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to delete user " + username + ": " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        for (var remoteAccount : remoteAccounts.values()) {
            deleteRemoteAccount(remoteAccount);
        }
    }

    public RemoteAccount getOrCreateRemoteAccount(String _username) {
        return remoteAccounts.computeIfAbsent(_username, k -> {
            for (int i = 0; i < 2; i++) {
                final var uuid = UUID.randomUUID().toString().replace("-", "");
                final var username = USER_PREFIX + _username + "_" + uuid;
                // Use a password that passes the strict password policy seen in capabilities
                final var password = "Password123!@#" + username;

                // Step 1: Create user with dummy email
                final var createForm = new FormBody.Builder()
                        .add("userid", username)
                        .add("email", username + "@example.com")
                        .build();

                final var createRequest = new Request.Builder()
                        .url(adminUrl.toString() + "/ocs/v2.php/cloud/users?format=json")
                        .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                        .header("OCS-APIRequest", "true")
                        .post(createForm)
                        .build();

                try (var response = httpClient.newCall(createRequest).execute()) {
                    if (response.isSuccessful()) {
                        return setPasswordAndReturn(username, password);
                    } else {
                        final var body = response.body() != null ? response.body().string() : "no body";
                        if (body.contains("\"statuscode\":101")) {
                            System.err.println("User already exists, retrying with new UUID: " + username);
                            continue;
                        }
                        throw new RuntimeException("Failed to create user \"" + username + "\": " + response.code() + " " + body);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException("Failed to create user after retries: " + _username);
        });
    }

    private RemoteAccount setPasswordAndReturn(String username, String password) {
        // Step 2: Set password explicitly
        final var passwordForm = new FormBody.Builder()
                .add("key", "password")
                .add("value", password)
                .build();

        final var passwordRequest = new Request.Builder()
                .url(adminUrl + "/ocs/v2.php/cloud/users/" + username + "?format=json")
                .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                .header("OCS-APIRequest", "true")
                .put(passwordForm)
                .build();

        try (var response = httpClient.newCall(passwordRequest).execute()) {
            if (!response.isSuccessful()) {
                final var body = response.body() != null ? response.body().string() : "no body";
                throw new RuntimeException("Failed to set user password: " + response.code() + " " + body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new RemoteAccount(adminUrl, username, password);
    }

    private void deleteRemoteAccount(RemoteAccount user) {
        final var request = new Request.Builder()
                .url(adminUrl.toString() + "/ocs/v2.php/cloud/users/" + user.username() + "?format=json")
                .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                .header("OCS-APIRequest", "true")
                .delete()
                .build();
        try (var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to delete user: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record RemoteAccount(URL url,
                                String username,
                                String password) {
    }
}
