/** 01.08.2012 22:12 */
package de.uniluebeck.sourcegen.js;

import de.uniluebeck.sourcegen.ElemImpl;

/**
 * Base class of all JavaScript elements in the
 * Code Generation Framework.
 *
 * A JSElement object is a single item, whereas
 * a JSComplexType object is a composition of
 * multiple items.
 *
 * @author seidel
 */
public abstract class JSElementImpl extends ElemImpl implements JSElement
{
  /**
   * Compare JavaScript language element with another
   * object of the same type.
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
