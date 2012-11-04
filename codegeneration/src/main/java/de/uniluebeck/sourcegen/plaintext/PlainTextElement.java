/** 02.11.2012 19:23 */
package de.uniluebeck.sourcegen.plaintext;

import de.uniluebeck.sourcegen.WorkspaceElement;

/**
 * Base interface for all plain text elements in
 * the Code Generation Framework.
 *
 * @author seidel
 */
public interface PlainTextElement extends WorkspaceElement
{
  @Override
  public boolean equals(Object object);

  @Override
  public int hashCode();
}
