/** 02.08.2012 00:22 */
package de.uniluebeck.sourcegen.js;

/**
 * Base class for all complex types within a
 * JavaScript source code (e.g. functions and
 * classes).
 *
 * A JSComplexType object is a composition of
 * multiple items, whereas a JSElement object
 * is just a single item.
 *
 * @author seidel
 */
public abstract class JSComplexTypeImpl extends JSElementImpl implements JSComplexType
{
  /**
   * Get name of complex type object.
   *
   * @return Name of type object
   */
  @Override
  public abstract String getName();
}
