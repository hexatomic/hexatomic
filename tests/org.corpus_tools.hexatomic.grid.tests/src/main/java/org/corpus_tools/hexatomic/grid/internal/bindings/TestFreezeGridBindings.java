/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.freeze.action.FreezeGridAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestFreezeGridBindings {

  private static FreezeGridBindings fixture = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new FreezeGridBindings();
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.bindings.FreezeGridBindings#configureUiBindings(org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry)}.
   */
  @Test
  void testConfigureUiBindings() {
    // UiBindingRegistry registry = mock(UiBindingRegistry.class);
    UiBindingRegistry registry = new UiBindingRegistry(mock(NatTable.class));
    fixture.configureUiBindings(registry);
    Event event = new Event();
    event.stateMask = SWT.ALT | SWT.MOD2;
    event.widget = mock(NatTable.class);
    event.keyCode = 'f';
    KeyEvent keyEvent = new KeyEvent(event);
    Object action = registry.getKeyEventAction(keyEvent);
    assertEquals(FreezeGridAction.class, action.getClass());
  }



}
