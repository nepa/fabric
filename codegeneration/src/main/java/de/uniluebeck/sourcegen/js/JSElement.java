/** 01.08.2012 21:52 */
package de.uniluebeck.sourcegen.js;

import de.uniluebeck.sourcegen.WorkspaceElement;

/**
 * Base interface for all JavaScript elements in
 * the Code Generation Framework.
 *
 * @author seidel
 */
public interface JSElement extends WorkspaceElement
{
  @Override
  public boolean equals(Object object);

  @Override
  public int hashCode();
}
