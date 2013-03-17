/** 17.03.2013 01:47 */
package de.uniluebeck.sourcegen.js;

import java.util.LinkedList;

import de.uniluebeck.sourcegen.SourceFile;
import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Interface for a single JavaScript source file. It
 * defines all method signatures for JSSourceFileImpl.
 *
 * @author seidel
 */
public interface JSSourceFile extends JSComplexType, SourceFile
{
  public JSSourceFile setPath(final String path);
  public String getPath();

  public JSSourceFile setFileName(final String fileName);

  public JSSourceFile add(final JSField... fields) throws JSDuplicateException;
  public JSSourceFile add(final JSClass... classes) throws JSDuplicateException;
  public JSSourceFile add(final JSFunction... functions) throws JSDuplicateException;

  public LinkedList<JSField> getFields();
  public LinkedList<JSClass> getClasses();
  public LinkedList<JSFunction> getFunctions();

  public boolean contains(final JSField fieldObject);
  public boolean contains(final JSClass classObject);
  public boolean contains(final JSFunction functionObject);

  public JSCodeBlock getCodeBeforeFields();
  public JSCodeBlock getCodeAfterFields(); // Alias for getCodeBeforeClasses()
  public JSCodeBlock getCodeBeforeClasses();
  public JSCodeBlock getCodeAfterClasses(); // Alias for getCodeBeforeFunctions()
  public JSCodeBlock getCodeBeforeFunctions();
  public JSCodeBlock getCodeAfterFunctions();

  public JSSourceFile setComment(final JSComment comment);
  public JSSourceFile setComment(final String comment);
}
