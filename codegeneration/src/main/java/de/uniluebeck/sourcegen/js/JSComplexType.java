/** 02.08.2012 00:22 */
package de.uniluebeck.sourcegen.js;

/**
 * Base interface for all complex types within a
 * JavaScript source code (e.g. functions and
 * classes).
 *
 * @author seidel
 */
public interface JSComplexType extends JSElement
{
  public String getName();
}
