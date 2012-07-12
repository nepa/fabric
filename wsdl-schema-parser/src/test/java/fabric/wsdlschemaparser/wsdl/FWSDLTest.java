/** 12.07.2012 00:43 */
package fabric.wsdlschemaparser.wsdl;

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
 * Unit test for FWSDL class. It will load and process a
 * couple of WSDL files in batch mode.
 *
 * @author seidel
 */
public class FWSDLTest
{
  /**
   * Filter for WSDL files.
   */
  private static class WSDLFileFilter implements FilenameFilter
  {
    @Override
    public boolean accept(File dir, String name)
    {
      return name.endsWith(".wsdl");
    }
  }

  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(FWSDLTest.class);

  /** List of WSDL files */
  private static List<File> wsdlFiles;

  /**
   * Load all WSDL files once before tests run.
   */
  @BeforeClass
  public static void loadWSDLFiles()
  {
    FWSDLTest.wsdlFiles = new ArrayList<File>();

    // Directories with WSDL files
    String directories[] = new String[]
    {
      "src/test/resources/wsdls",
      "src/test/resources/wsdls/xmethods"
    };

    // Load all files
    for (String dir: directories)
    {
      File directory = new File(dir);
      assertTrue("Source of WSDL files must be a directory.", directory.isDirectory());

      // Add files to list
      FWSDLTest.wsdlFiles.addAll(Arrays.asList(directory.listFiles(new WSDLFileFilter())));
    }
  }

  /**
   * Test creation of FWSDL objects.
   */
  @Test
  @Ignore // Exclude test from Maven build; remove line to run test manually!
  public void testWSDLFileLoading()
  {
    for (File file: FWSDLTest.wsdlFiles)
    {
      // Test for expected exception
      Exception exception = null;
      try
      {
        LOGGER.info("--------------------------------------------------------------");
        LOGGER.info(String.format("Loading WSDL file '%s'...", file.getName()));
        LOGGER.info("--------------------------------------------------------------");

        FWSDL wsdl = new FWSDL(file);
        assertNotNull("FWSDL object must not be null.", wsdl);
      }
      catch (Exception e)
      {
        exception = e;

        LOGGER.error(String.format("Exception '%s' was thrown: '%s'", e.getClass().getSimpleName(), e.getMessage()));
      }

      // Only some (invalid) WSDL files throw exceptions
      if (null != exception)
      {
        // IllegalArgumentException in FBindingOperationImpl:
        //   "Type of webservice operation 'foobar' is invalid."
        assertTrue("FWSDL must throw 'IllegalArgumentException'.", exception instanceof IllegalArgumentException);
      }
    }
  }
}
