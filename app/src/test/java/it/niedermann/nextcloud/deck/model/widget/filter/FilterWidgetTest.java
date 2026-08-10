package it.niedermann.nextcloud.deck.model.widget.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FilterWidgetTest {

    @Test
    public void testAppearanceFieldsDefaultToNull() {
        final var widget = new FilterWidget(1, EWidgetType.UPCOMING_WIDGET);

        assertNull(widget.getTitleColor());
        assertNull(widget.getWidgetBackgroundColor());
        assertNull(widget.getSectionTextColor());
        assertNull(widget.getListBackgroundColor());
        assertNull(widget.getEntryTextColor());
    }

    @Test
    public void testAppearanceFieldsGetterSetter() {
        final var widget = new FilterWidget(1, EWidgetType.UPCOMING_WIDGET);

        widget.setTitle("My Deck");
        widget.setTitleColor(0xFF112233);
        widget.setWidgetBackgroundColor(0x00FFFFFF);
        widget.setSectionTextColor(0xFF445566);
        widget.setListBackgroundColor(0x00000000);
        widget.setEntryTextColor(0xFF778899);

        assertEquals("My Deck", widget.getTitle());
        assertEquals(Integer.valueOf(0xFF112233), widget.getTitleColor());
        assertEquals(Integer.valueOf(0x00FFFFFF), widget.getWidgetBackgroundColor());
        assertEquals(Integer.valueOf(0xFF445566), widget.getSectionTextColor());
        assertEquals(Integer.valueOf(0x00000000), widget.getListBackgroundColor());
        assertEquals(Integer.valueOf(0xFF778899), widget.getEntryTextColor());
    }

    @Test
    public void testEqualsAndHashCodeConsiderAppearanceFields() {
        final var widgetA = new FilterWidget(1, EWidgetType.UPCOMING_WIDGET);
        final var widgetB = new FilterWidget(1, EWidgetType.UPCOMING_WIDGET);

        assertEquals(widgetA, widgetB);
        assertEquals(widgetA.hashCode(), widgetB.hashCode());

        widgetB.setTitleColor(0xFF112233);

        assertNotEquals(widgetA, widgetB);

        widgetA.setTitleColor(0xFF112233);

        assertEquals(widgetA, widgetB);
        assertEquals(widgetA.hashCode(), widgetB.hashCode());
    }
}
