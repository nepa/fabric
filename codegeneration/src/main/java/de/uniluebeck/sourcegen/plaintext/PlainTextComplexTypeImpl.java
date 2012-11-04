/** 03.11.2012 19:34 */
package de.uniluebeck.sourcegen.plaintext;

/**
 * Base class for all complex types within a
 * plain text file (e.g. source files).
 *
 * A PlainTextComplexType object is a composition
 * of multiple items, whereas a PlainTextElement
 * object is just a single item.
 *
 * @author seidel
 */
public abstract class PlainTextComplexTypeImpl extends PlainTextElementImpl implements PlainTextComplexType
{
  /**
   * Get name of complex type object.
   *
   * @return Name of type object
   */
  @Override
  public abstract String getName();
}
