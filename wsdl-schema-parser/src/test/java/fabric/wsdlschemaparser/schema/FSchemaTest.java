/** 12.07.2012 00:45 */
package fabric.wsdlschemaparser.schema;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Unit test for FSchema class. It will load and process a
 * couple of XML Schema files in batch mode.
 *
 * @author seidel
 */
public class FSchemaTest
{
  /**
   * Filter for XML Schema files.
   */
  private static class XSDFileFilter implements FilenameFilter
  {
    @Override
    public boolean accept(File dir, String name)
    {
      return name.endsWith(".xsd");
    }
  }

  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(FSchemaTest.class);

  /** List of XML Schema files */
  private static List<File> schemaFiles;

  /**
   * Load all XML Schema files once before tests run.
   */
  @BeforeClass
  public static void loadXSDFiles()
  {
    FSchemaTest.schemaFiles = new ArrayList<File>();

    // Directories with XML Schema files
    String directories[] = new String[]
    {
      "src/test/resources/schemas"
    };

    // Load all files
    for (String dir: directories)
    {
      File directory = new File(dir);
      assertTrue("Source of XML Schema files must be a directory.", directory.isDirectory());

      // Add files to list
      FSchemaTest.schemaFiles.addAll(Arrays.asList(directory.listFiles(new XSDFileFilter())));
    }
  }

  /**
   * Test creation of FSchema objects.
   */
  @Test
  @Ignore // Exclude test from Maven build; remove line to run test manually!
  public void testXsdFileLoading() throws Exception
  {
    for (File file: FSchemaTest.schemaFiles)
    {
      LOGGER.info("--------------------------------------------------------------");
      LOGGER.info(String.format("Loading XML Schema file '%s'...", file.getName()));
      LOGGER.info("--------------------------------------------------------------");

      FSchema schema = new FSchema(file);
      assertNotNull("FSchema object must not be null.", schema);
    }
  }
}
