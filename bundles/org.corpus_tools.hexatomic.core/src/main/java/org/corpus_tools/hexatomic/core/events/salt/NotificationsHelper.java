package org.corpus_tools.hexatomic.core.events.salt;

class NotificationsHelper {

  /**
   * Delegated elements might have an actual owning element. This functions returns the actual
   * owning element or the element itself. If there is a chain of owners, the last element in this
   * chain will be returned.
   * 
   * @param element The element to get the owner for
   * @return The actual implementation object
   */
  static Object resolveDelegation(NotifyingElement<?> element) {
    if (element == null) {
      return null;
    }
    Object currentElement = element;
    while (currentElement instanceof NotifyingElement<?>
        && ((NotifyingElement<?>) currentElement).getOwner() != null) {
      currentElement = ((NotifyingElement<?>) currentElement).getOwner();
    }
    return currentElement;
  }
}
