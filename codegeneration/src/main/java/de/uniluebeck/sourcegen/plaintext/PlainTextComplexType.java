/** 03.11.2012 19:29 */
package de.uniluebeck.sourcegen.plaintext;

/**
 * Base interface for all complex types within a
 * plain text file (e.g. source files).
 *
 * @author seidel
 */
public interface PlainTextComplexType extends PlainTextElement
{
  public String getName();
}
