package it.niedermann.nextcloud.deck.exceptions;

public class UploadAttachmentFailedException extends Exception {

    public UploadAttachmentFailedException(String message) {
        super(message);
    }

    public UploadAttachmentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
