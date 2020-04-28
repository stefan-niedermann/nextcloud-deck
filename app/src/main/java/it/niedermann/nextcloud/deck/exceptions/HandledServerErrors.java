package it.niedermann.nextcloud.deck.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

public enum HandledServerErrors {
    UNKNOWN(1337, "hopefully won't occurr"),
    LABELS_TITLE_MUST_BE_UNIQUE(400, "title must be unique"),
    ATTACHMENTS_FILE_ALREADY_EXISTS(409, "File already exists."),
    ;

    private int status;
    private String message;

    HandledServerErrors(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static HandledServerErrors fromThrowable(Throwable throwable) {
        if (throwable instanceof NextcloudHttpRequestFailedException) {
            NextcloudHttpRequestFailedException requestFailedException = (NextcloudHttpRequestFailedException) throwable;
            if (requestFailedException.getCause() != null) {
                String errorString = requestFailedException.getCause().getMessage();
                try {
                    JsonElement jsonElement = JsonParser.parseString(errorString);
                    if (jsonElement.isJsonObject()){
                        ServerError error = new ServerError();
                        error.status = requestFailedException.getStatusCode();
                        JsonObject errorObj = jsonElement.getAsJsonObject();
                        if (errorObj.has("message")){
                            error.message = errorObj.get("message").getAsString();
                        }
                        return findByServerError(error);
                    }
                } catch (JsonSyntaxException e){
                    return HandledServerErrors.UNKNOWN;
                }
            }
        }
        return HandledServerErrors.UNKNOWN;
    }

    private static HandledServerErrors findByServerError(ServerError error) {
        for (HandledServerErrors value : HandledServerErrors.values()) {
            if (value.status == error.status && value.message.equals(error.message)){
                return value;
            }
        }
        return HandledServerErrors.UNKNOWN;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    private static class ServerError {
        private int status;
        private String message;
    }
}
