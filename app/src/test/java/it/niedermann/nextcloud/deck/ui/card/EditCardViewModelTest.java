package it.niedermann.nextcloud.deck.ui.card;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class EditCardViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private EditCardViewModel viewModel;
    private Context context;
    private SharedPreferences sharedPrefs;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        context = ApplicationProvider.getApplicationContext();
        viewModel = new EditCardViewModel(ApplicationProvider.getApplicationContext());
        sharedPrefs = context.getSharedPreferences("TEMP_SHARED_PREFS", Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().commit();
        final Field sharedPreferencesField = EditCardViewModel.class.getDeclaredField("sharedPreferences");
        sharedPreferencesField.setAccessible(true);
        sharedPreferencesField.set(viewModel, sharedPrefs);
    }

    @Test
    public void getDescriptionMode_createMode() throws InterruptedException {
        assertThrows(IllegalStateException.class, () -> TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.setCreateMode(true);
        viewModel.initializeNewCard(1, 1, true);

        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), true).commit();
        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), false).commit();
        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));
    }

    @Test
    public void getDescriptionMode_editMode_preview() throws InterruptedException {
        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), true).commit();
        final FullCardWithProjects fullCardWithProjects = new FullCardWithProjects();
        fullCardWithProjects.setCard(new Card());

        assertThrows(IllegalStateException.class, () -> TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.setCreateMode(false);
        viewModel.initializeExistingCard(0, fullCardWithProjects, true);

        fullCardWithProjects.getCard().setDescription("Description");
        assertTrue(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        fullCardWithProjects.getCard().setDescription("");
        assertFalse("Should use edit mode, even if preview is explicitly set in case the description is empty.",
                TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        fullCardWithProjects.getCard().setDescription(null);
        assertFalse("Should use edit mode, even if preview is explicitly set in case the description is empty.",
                TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));
    }

    @Test
    public void getDescriptionMode_editMode_edit() throws InterruptedException {
        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), false).commit();
        final FullCardWithProjects fullCardWithProjects = new FullCardWithProjects();
        fullCardWithProjects.setCard(new Card());

        assertThrows(IllegalStateException.class, () -> TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.setCreateMode(false);
        viewModel.initializeExistingCard(0, fullCardWithProjects, true);

        fullCardWithProjects.getCard().setDescription("Description");
        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        fullCardWithProjects.getCard().setDescription("");
        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        fullCardWithProjects.getCard().setDescription(null);
        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));
    }

    @Test
    public void toggleDescriptionPreviewMode_create() throws InterruptedException {
        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), false).commit();

        assertThrows(IllegalStateException.class, () -> TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.setCreateMode(true);
        viewModel.initializeNewCard(1, 1, true);

        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.toggleDescriptionPreviewMode();
        assertTrue(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        assertFalse("Stored state must not be changed in create mode, even if the description mode is toggled",
                sharedPrefs.getBoolean(context.getString(R.string.shared_preference_description_preview), true));
    }

    @Test
    public void toggleDescriptionPreviewMode_edit() throws InterruptedException {
        sharedPrefs.edit().putBoolean(context.getString(R.string.shared_preference_description_preview), false).commit();
        final FullCardWithProjects fullCardWithProjects = new FullCardWithProjects();
        fullCardWithProjects.setCard(new Card("Title", "Description", 0));

        assertThrows(IllegalStateException.class, () -> TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.setCreateMode(false);
        viewModel.initializeExistingCard(0, fullCardWithProjects, true);

        assertFalse(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        viewModel.toggleDescriptionPreviewMode();
        assertTrue(TestUtil.getOrAwaitValue(viewModel.getDescriptionMode()));

        assertTrue("Stored state must be updated in edit mode when the description mode is toggled",
                sharedPrefs.getBoolean(context.getString(R.string.shared_preference_description_preview), true));
    }
}
