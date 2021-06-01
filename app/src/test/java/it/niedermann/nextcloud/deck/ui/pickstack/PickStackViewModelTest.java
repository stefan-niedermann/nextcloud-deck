package it.niedermann.nextcloud.deck.ui.pickstack;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class PickStackViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PickStackViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new PickStackViewModel(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void submitButtonEnabled() throws InterruptedException {
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setContentIsSatisfied(true);
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSelected(mock(Account.class), null, null);
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSelected(mock(Account.class), mock(Board.class), null);
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSelected(mock(Account.class), null, mock(Stack.class));
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSelected(mock(Account.class), mock(Board.class), mock(Stack.class));
        assertTrue(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSubmitInProgress(true);
        assertFalse(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));

        viewModel.setSubmitInProgress(false);
        assertTrue(TestUtil.getOrAwaitValue(viewModel.submitButtonEnabled()));
    }
}