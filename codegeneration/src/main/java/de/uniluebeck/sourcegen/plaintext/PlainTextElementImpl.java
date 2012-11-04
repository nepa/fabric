/** 03.11.2012 19:26 */
package de.uniluebeck.sourcegen.plaintext;

import de.uniluebeck.sourcegen.ElemImpl;

/**
 * Base class of all plain text elements in the
 * Code Generation Framework.
 *
 * A PlainTextElement object is a single item,
 * whereas a PlainTextComplexType object is a
 * composition of multiple items.
 *
 * @author seidel
 */
public abstract class PlainTextElementImpl extends ElemImpl implements PlainTextElement
{
  /**
   * Compare plain text element with another object of
   * the same type.
   *
   * @param object Other object to compare with
   *
   * @return True if objects are equal, false otherwise
   */
  @Override
  public abstract boolean equals(Object object);

  /**
   * Generate hash code for object comparison based on
   * some attributes of the current class.
   *
   * @return Hash code for current object
   */
  @Override
  public abstract int hashCode();
}
