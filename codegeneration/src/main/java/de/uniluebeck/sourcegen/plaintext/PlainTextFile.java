/** 03.11.2012 19:37 */
package de.uniluebeck.sourcegen.plaintext;

import de.uniluebeck.sourcegen.SourceFile;

/**
 * Interface for a single plain text file. It defines
 * all method signatures for PlainTextFileImpl.
 *
 * @author seidel
 */
public interface PlainTextFile extends PlainTextComplexType, SourceFile
{
  public PlainTextFile setPath(final String path);
  public String getPath();

  public PlainTextFile setFileName(final String fileName);

  public PlainTextFile setExtension(final String extension);
  public String getExtension();

  public PlainTextContent getContent();
}
