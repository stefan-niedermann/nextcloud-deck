package it.niedermann.nextcloud.deck.remote.api;

import static org.junit.Assert.assertEquals;

import com.nextcloud.android.sso.exceptions.UnknownErrorException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.exceptions.OfflineException;

@RunWith(RobolectricTestRunner.class)
public class ServerCommunicationErrorHandlerTest {

    @Test
    public void shouldMap_UnknownErrorExceptions_To_OfflineExceptions_ForConnectionTimeout() {
        final var result = ServerCommunicationErrorHandler.translateError(new UnknownErrorException("failed to connect to myhome.example.com/2222:A:B:C:D (port 443) from /2020:A:B:C:D (port 42803) after 60000ms: isConnected failed: ECONNREFUSED (Connection refused)"));
        assertEquals(OfflineException.class, result.getClass());
        assertEquals(OfflineException.Reason.CONNECTION_REFUSED, ((OfflineException) result).getReason());
    }

    @Test
    public void shouldMap_UnknownErrorExceptions_To_OfflineExceptions_ForHostUnreachable() {
        final var result = ServerCommunicationErrorHandler.translateError(new UnknownErrorException("Unable to resolve host \"nextcloud-prod.fritz.box\": No address associated with hostname"));
        assertEquals(OfflineException.class, result.getClass());
        assertEquals(OfflineException.Reason.CONNECTION_REFUSED, ((OfflineException) result).getReason());
    }

    @Test
    public void shouldMap_ClassNotFoundExceptions_To_OfflineExceptions_ForConnectionRefused() {
        final var result = ServerCommunicationErrorHandler.translateError(new ClassNotFoundException("java.lang.ClassNotFoundException: org.apache.commons.httpclient.ConnectTimeoutException"));
        assertEquals(OfflineException.class, result.getClass());
        assertEquals(OfflineException.Reason.CONNECTION_TIMEOUT, ((OfflineException) result).getReason());
    }

    @Test
    public void shouldSkip_UnknownErrorExceptions_ForOtherCases() {
        final var result = ServerCommunicationErrorHandler.translateError(new UnknownErrorException("Foo Bar"));
        assertEquals(UnknownErrorException.class, result.getClass());
        assertEquals("Foo Bar", result.getMessage());
    }

    @Test
    public void shouldSkip_ClassNotFoundExceptions_ForOtherCases() {
        final var result = ServerCommunicationErrorHandler.translateError(new ClassNotFoundException("Foo Bar"));
        assertEquals(ClassNotFoundException.class, result.getClass());
        assertEquals("Foo Bar", result.getMessage());
    }

    @Test
    public void shouldSkip_KnownExceptions_WithNullMessages() {
        assertEquals(ClassNotFoundException.class, ServerCommunicationErrorHandler.translateError(new ClassNotFoundException()).getClass());
        assertEquals(UnknownErrorException.class, ServerCommunicationErrorHandler.translateError(new UnknownErrorException("Foo")).getClass());
    }
}
