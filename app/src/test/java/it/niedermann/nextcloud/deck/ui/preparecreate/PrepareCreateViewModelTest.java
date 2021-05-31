package it.niedermann.nextcloud.deck.ui.preparecreate;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ConstantConditions")
@RunWith(RobolectricTestRunner.class)
public class PrepareCreateViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PrepareCreateViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new PrepareCreateViewModel(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void createFullCardByContent() {
        final Version v = Version.minimumSupported();
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, ""));

        Card card;

        card = viewModel.createFullCard(v, "User entered text").getCard();
        assertEquals("User entered text", card.getTitle());
        assertNull(card.getDescription());

        card = viewModel.createFullCard(v, "This is a very long content which will not fit into the card and should be split into title and desc ription. Whitespace should be trimmed.").getCard();
        assertEquals("This is a very long content which will not fit into the card and should be split into title and desc", card.getTitle());
        assertEquals("ription. Whitespace should be trimmed.", card.getDescription());
    }

    @Test
    public void createFullCardByTitleAndDescription() {
        final Version v = Version.minimumSupported();
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", ""));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, ""));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", null));

        Card card;

        // content fits into title field

        card = viewModel.createFullCard(v, "User entered text", null).getCard();
        assertEquals("User entered text", card.getTitle());
        assertNull(card.getDescription());

        card = viewModel.createFullCard(v, null, "Fancy description").getCard();
        assertEquals("Fancy description", card.getTitle());
        assertNull(card.getDescription());

        card = viewModel.createFullCard(v, "User entered text", "My description").getCard();
        assertEquals("User entered text", card.getTitle());
        assertEquals("My description", card.getDescription());

        // content does not fit into title field

        card = viewModel.createFullCard(v, "This is a very long content which will not fit into the card and should be split into title and desc ription. Whitespace should be trimmed.", null).getCard();
        assertEquals("This is a very long content which will not fit into the card and should be split into title and desc", card.getTitle());
        assertEquals("ription. Whitespace should be trimmed.", card.getDescription());

        card = viewModel.createFullCard(v, null, "This is a very long content which will not fit into the card and should be split into title and desc ription. Whitespace should be trimmed.").getCard();
        assertEquals("This is a very long content which will not fit into the card and should be split into title and desc", card.getTitle());
        assertEquals("ription. Whitespace should be trimmed.", card.getDescription());

        card = viewModel.createFullCard(v, "User entered text", "This is a very long content which will not fit into the card and should be split into title and description.").getCard();
        assertEquals("User entered text", card.getTitle());
        assertEquals("This is a very long content which will not fit into the card and should be split into title and description.", card.getDescription());

        card = viewModel.createFullCard(v, "This is a very long content which will not fit into the card and should be split into title and description.", "My description").getCard();
        assertEquals("This is a very long content which will not fit into the card and should be split into title and desc", card.getTitle());
        assertEquals("ription.\n\nMy description", card.getDescription());

        card = viewModel.createFullCard(v, "This is a very long content which will not fit into the card and should be split into title and description.", "This is a very long description which also will not fit into the card and should be split into title and description.").getCard();
        assertEquals("This is a very long content which will not fit into the card and should be split into title and desc", card.getTitle());
        assertEquals("ription.\n\nThis is a very long description which also will not fit into the card and should be split into title and description.", card.getDescription());
    }

    @Test
    public void createFullCard() {
        final PrepareCreateViewModel viewModel = spy(this.viewModel);
        doReturn(new FullCard()).when(viewModel).createFullCard(any(), anyString());
        doReturn(new FullCard()).when(viewModel).createFullCard(any(), anyString(), anyString());

        final Version v = Version.minimumSupported();

        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", null, null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, "", null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, null, ""));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", "", null));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", null,""));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, null, "",""));
        assertThrows(IllegalArgumentException.class, () -> viewModel.createFullCard(v, "", "",""));

        viewModel.createFullCard(v, "Foo", null, null);
        verify(viewModel).createFullCard(any(), eq("Foo"));
        reset(viewModel);

        viewModel.createFullCard(v, null, "Bar", null);
        verify(viewModel).createFullCard(any(), eq("Bar"));
        reset(viewModel);

        viewModel.createFullCard(v, null, null, "Baz");
        verify(viewModel).createFullCard(any(), eq("Baz"));
        reset(viewModel);

        viewModel.createFullCard(v, "Foo", "Bar", null);
        verify(viewModel).createFullCard(any(), eq("Foo"), eq("Bar"));
        reset(viewModel);

        viewModel.createFullCard(v, "Foo", null, "Baz");
        verify(viewModel).createFullCard(any(), eq("Foo"), eq("Baz"));
        reset(viewModel);

        viewModel.createFullCard(v, null, "Bar", "Baz");
        verify(viewModel).createFullCard(any(), eq("Bar"), eq("Baz"));
        reset(viewModel);

        viewModel.createFullCard(v, "Foo", "Bar", "Baz");
        verify(viewModel).createFullCard(any(), eq("Foo"), eq("Bar\n\nBaz"));
        reset(viewModel);
    }
}
