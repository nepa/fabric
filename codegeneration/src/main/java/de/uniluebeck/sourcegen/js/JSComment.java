/** 02.08.2012 20:30 */
package de.uniluebeck.sourcegen.js;

/**
 * Interface for a comment within JavaScript source code. This
 * file defines all method signatures for JSCommentImpl.
 *
 * @author seidel
 */
public interface JSComment extends JSElement
{
  public JSComment setDescription(final String description);
  public String getDescription();
}
