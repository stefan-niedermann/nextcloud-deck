package it.niedermann.nextcloud.deck;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyncManagerTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Context context;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private DataBaseAdapter dataBaseAdapter;

    @InjectMocks
    private SyncManager syncManager;

    @Test
    public void testCreate() throws InterruptedException {
        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(true));
        final LiveData<Boolean> hasAccountsPositive = syncManager.hasAccounts();
        assertEquals(Boolean.TRUE, TestUtil.getOrAwaitValue(hasAccountsPositive));

        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(false));
        final LiveData<Boolean> hasAccountsNegative = syncManager.hasAccounts();
        assertEquals(Boolean.TRUE, TestUtil.getOrAwaitValue(hasAccountsNegative));

        verify(dataBaseAdapter, times(2)).hasAccounts();
    }
}
