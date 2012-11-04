/** 04.11.2012 07:31 */
package de.uniluebeck.sourcegen.plaintext;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for PlainTextFile class.
 *
 * @author seidel
 */
public class PlainTextFileTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    PlainTextFile textFile = new PlainTextFileImpl("foobar", "txt");
    assertNotNull("PlainTextFile object must not be null.", textFile);
    assertEquals("Name of plain text file must match initial value.", "foobar", textFile.getFileName());
    assertEquals("Extension of plain text file must match initial value.", "txt", textFile.getExtension());
  }

  /**
   * Test content handling.
   */
  @Test(timeout = 1000)
  public void testContentHandling()
  {
    PlainTextFile textFile = new PlainTextFileImpl("foobar", "txt");

    String content = "Test";
    textFile.getContent().setCode(content);
    assertEquals("Content of plain text file must match initial value.",
            content, textFile.getContent().toString());
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    PlainTextFile firstFile = new PlainTextFileImpl("foo", "txt");
    PlainTextFile secondFile = new PlainTextFileImpl("foo", "txt");

    // Equality
    assertTrue("Text files must be equal.", firstFile.equals(secondFile));
    assertTrue("Text file must be equal to itself.", firstFile.equals(firstFile));

    // Unequality
    secondFile.setFileName("bar");
    assertFalse("Text files with different names must not be equal.", firstFile.equals(secondFile));

    secondFile.setFileName("foo");
    secondFile.setExtension("xml");
    assertFalse("Text files with different extensions must not be equal.", firstFile.equals(secondFile));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput() throws Exception
  {
    PlainTextFile textFile = new PlainTextFileImpl("glassfish-web", "xml");

    String content =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE glassfish-web-app PUBLIC \"-//GlassFish.org//DTD GlassFish Application Server " +
              "3.1 Servlet 3.0//EN\" \"http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd\">\n" +
            "<glassfish-web-app error-url=\"\">\n" +
            "\t<class-loader delegate=\"true\"/>\n" +
            "\t<jsp-config>\n" +
            "\t\t<property name=\"keepgenerated\" value=\"true\">\n" +
            "\t\t\t<description>Keep a copy of the generated servlet class' java code.</description>\n" +
            "\t\t</property>\n" +
            "\t</jsp-config>\n" +
            "</glassfish-web-app>";
    textFile.getContent().setCode(content);

    System.out.println(textFile);
  }
}
