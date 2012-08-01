/** 29.07.2012 21:13 */
package de.uniluebeck.sourcegen.js;

/**
 * Interface for a comment within JavaScript source code. This
 * file defines all method signatures for JSCommentImpl.
 *
 * @author seidel
 */
public interface JSComment extends JSElement
{
  public String getDescription();
}
