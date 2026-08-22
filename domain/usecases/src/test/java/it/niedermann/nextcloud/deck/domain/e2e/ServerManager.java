package it.niedermann.nextcloud.deck.domain.e2e;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@SuppressWarnings("CallToPrintStackTrace")
public class ServerManager implements AutoCloseable {

    private final OkHttpClient httpClient;
    private final RandomUtil randomUtil;
    private final URL adminUrl;
    private final String adminUsername;
    private final String adminPassword;
    private final String e2eRunRandom;
    private String e2eTestCaseRandom;

    private final Map<String, RemoteAccount> remoteAccounts = new HashMap<>();

    public ServerManager(
            OkHttpClient httpClient,
            RandomUtil randomUtil,
            URL adminUrl,
            String adminUsername,
            String adminPassword) {
        this.httpClient = httpClient;
        this.randomUtil = randomUtil;
        this.adminUrl = adminUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.e2eRunRandom = randomUtil.randomString(3);
    }

    public void setup() {
        this.e2eTestCaseRandom = randomUtil.randomString(3);
    }

    @Override
    public void close() {
        for (var remoteAccount : remoteAccounts.values()) {
            deleteRemoteAccount(remoteAccount);
        }
    }

    public RemoteAccount getOrCreateRemoteAccount(String _username) {
        return remoteAccounts.computeIfAbsent(_username, k -> {
            final var username = "E2E_" + _username + "_" + e2eRunRandom + "_" + e2eTestCaseRandom;
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
                if (!response.isSuccessful()) {
                    final var body = response.body() != null ? response.body().string() : "no body";
                    throw new RuntimeException("Failed to create user: " + response.code() + " " + body);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
        });
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
