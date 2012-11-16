/** 14.11.2012 18:46 */
package fabric.module.exi;

import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.sourcegen.Workspace;
import fabric.module.api.FModuleBase;
import fabric.module.api.FItemHandlerBase;
import fabric.module.exi.exceptions.FabricEXIException;
import fabric.module.typegen.FabricTypeGenModule;

/**
 * Fabric module for the generation of an XSD-specific EXI serializer
 * and deserializer class. This module uses the module options from the
 * properties file and the name of the XML schema document to create
 * a class, that allows the serialization and deserialization of the
 * corresponding type objects.
 *
 * @author seidel
 */
public class FabricEXIModule implements FModuleBase
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(FabricEXIModule.class);

  /** Key for target language in properties object */
  public static final String TARGET_LANGUAGE_KEY = "exi.target_language";

  /** Alternative key for target language */
  public static final String TARGET_LANGUAGE_ALT_KEY = "typegen.target_language";

  /** Key for main class name in properties object */
  public static final String MAIN_CLASS_NAME_KEY = "exi.main_class_name";

  /** Alternative key for main class name */
  public static final String MAIN_CLASS_NAME_ALT_KEY = "typegen.main_class_name";

  /** Key for application class name in properties object */
  public static final String APPLICATION_CLASS_NAME_KEY = "exi.application_class_name";

  /** Key for XML framework name in properties object */
  public static final String XML_FRAMEWORK_KEY = "exi.java.xml_framework";

  /** Alternative key for XML framework name */
  public static final String XML_FRAMEWORK_ALT_KEY = "typegen.java.xml_framework";

  /** Key for main package name in properties object */
  public static final String PACKAGE_NAME_KEY = "exi.java.package_name";

  /** Alternative key for main package name */
  public static final String PACKAGE_NAME_ALT_KEY = "typegen.java.package_name";

  /** Key for the EXI library name in properties object */
  public static final String EXI_LIBRARY_KEY = "exi.java.exi_library";

  /** Key for name of EXICodeGen class used in EXICodeGenFactory */
  public static final String EXICODEGEN_NAME_KEY = "exi.exicodegen_name";

  /** Key for name of XMLLibrary class used in XMLLibraryFactory */
  public static final String XMLLIBRARY_NAME_KEY = "exi.xmllibrary_name";

  /** Key for name of EXILibrary class used in EXILibraryFactory */
  public static final String EXILIBRARY_NAME_KEY = "exi.exilibrary_name";

  /** Key for XSD document path read from commandline */
  public static final String XSD_DOCUMENT_PATH_KEY = "fabric.xsd";

  /** Properties object for module configuration */
  private Properties properties;

  /**
   * Constructor initializes the internal properties object.
   *
   * @param properties Properties object with module options
   */
  public FabricEXIModule(Properties properties)
  {
    this.properties = properties;
  }

  /**
   * Helper method to return module name.
   *
   * @return Module name
   */
  @Override
  public String getName()
  {
    return "exi";
  }

  /**
   * Helper method to return module description.
   *
   * @return Module description
   */
  @Override
  public String getDescription()
  {
    return String.format("Module to generate EXI serializer and deserializer class. " +
            "Valid options are '%s', '%s', '%s', '%s', '%s' and '%s'. " +
            "Alternatively '%s', '%s', '%s' and '%s' can be used.",
            TARGET_LANGUAGE_KEY, MAIN_CLASS_NAME_KEY, XML_FRAMEWORK_KEY,
            PACKAGE_NAME_KEY, EXI_LIBRARY_KEY, EXICODEGEN_NAME_KEY,
            TARGET_LANGUAGE_ALT_KEY, MAIN_CLASS_NAME_ALT_KEY,
            XML_FRAMEWORK_ALT_KEY, PACKAGE_NAME_ALT_KEY);
  }

  /**
   * This method returns a Fabric handler object for the EXI generator
   * module. It is instantiated with the current workspace and module
   * options.
   *
   * @param workspace Workspace object for EXI class output
   *
   * @return List with one FabricEXIHandler object
   *
   * @throws Exception Error during handler instantiation
   */
  @Override
  public ArrayList<FItemHandlerBase> getHandlers(final Workspace workspace) throws Exception
  {
    this.validateProperties();

    ArrayList<FItemHandlerBase> handlers = new ArrayList<FItemHandlerBase>();
    handlers.add(new FabricEXIHandler(workspace, this.properties));

    return handlers;
  }

  /**
   * This method validates all module-specific options and translates
   * them where needed (e.g. class names for factory methods). The
   * constructor of this class is called during Fabric setup. At that
   * time, however, the Java properties file from the command line is
   * not yet processed. This is why this method must not be called in
   * the module's constructor, but in the getHandlers() method.
   *
   * @throws Exception Error during validation
   */
  private void validateProperties() throws Exception
  {
    // Early exit, if properties object is null
    if (null == this.properties)
    {
      throw new IllegalStateException("Properties object is null. Maybe it was not initialized properly?");
    }

    // Check if alternative keys have been used and copy values
    this.copyAlternativeProperties();

    // Check properties
    this.checkTargetLanguage();
    this.checkMainClassName();
    this.checkApplicationClassName();
    this.checkXMLFramework();
    this.checkPackageName();
    this.checkEXILibrary();

    // Print EXI module properties for debug purposes
    for (String key: this.properties.stringPropertyNames())
    {
      if (key.startsWith("exi."))
      {
        LOGGER.debug(String.format("Property '%s' has value '%s'.", key, this.properties.getProperty(key)));
      }
    }
  }

  /**
   * Private helper method to determine which properties should be
   * used. The EXI module can copy properties from TypeGen module,
   * if appropriate values are set.
   */
  private void copyAlternativeProperties()
  {
    if (!isSet(TARGET_LANGUAGE_KEY) && isSet(TARGET_LANGUAGE_ALT_KEY))
    {
      copyProperty(TARGET_LANGUAGE_ALT_KEY, TARGET_LANGUAGE_KEY);
    }

    if (!isSet(MAIN_CLASS_NAME_KEY) && isSet(MAIN_CLASS_NAME_ALT_KEY))
    {
      copyProperty(MAIN_CLASS_NAME_ALT_KEY, MAIN_CLASS_NAME_KEY);
    }

    if (!isSet(XML_FRAMEWORK_KEY) && isSet(XML_FRAMEWORK_ALT_KEY))
    {
      copyProperty(XML_FRAMEWORK_ALT_KEY, XML_FRAMEWORK_KEY);
    }

    if (!isSet(PACKAGE_NAME_KEY) && isSet(PACKAGE_NAME_ALT_KEY))
    {
      copyProperty(PACKAGE_NAME_ALT_KEY, PACKAGE_NAME_KEY);
    }
  }

  /**
   * Private helper method to check, whether a value is set for
   * a certain key in the properties object.
   *
   * @param key Key of property to check
   *
   * @return True if property is set, false otherwise
   */
  private boolean isSet(final String key)
  {
    return (this.properties.containsKey(key) && !("").equals(this.properties.getProperty(key)));
  }

  /**
   * Private helper method to copy property from field with key
   * 'from' to another field with key 'to'.
   *
   * @param from Key of source property
   * @param to Key of target property
   */
  private void copyProperty(final String from, final String to)
  {
    this.properties.setProperty(to, this.properties.getProperty(from));
  }

  /**
   * Check parameter for target language. This property is mandatory
   * and must either be "java" or "cpp". Further target languages
   * might be added later. In case of an illegal argument, an exception
   * is thrown.
   *
   * @throws Exception No or invalid properties passed to module
   */
  private void checkTargetLanguage() throws Exception
  {
    String targetLanguage = this.properties.getProperty(TARGET_LANGUAGE_KEY);

    // No target language defined
    if (null == targetLanguage)
    {
      throw new FabricEXIException("No target language specified. Please provide a valid option.");
    }
    // Use Java EXI code generator
    else if (targetLanguage.toLowerCase().equals("java"))
    {
      this.properties.setProperty(EXICODEGEN_NAME_KEY, "fabric.module.exi.java.JavaEXICodeGen");
      this.properties.setProperty(FabricTypeGenModule.MAPPER_CLASS_KEY, "fabric.module.typegen.java.JavaMapper");
    }
    // Use C++ EXI code generator
    else if (targetLanguage.toLowerCase().equals("cpp"))
    {
      this.properties.setProperty(EXICODEGEN_NAME_KEY, "fabric.module.exi.cpp.CppEXICodeGen");
      this.properties.setProperty(FabricTypeGenModule.MAPPER_CLASS_KEY, "fabric.module.typegen.cpp.CppMapper");
    }
    // Invalid target language provided
    else
    {
      throw new FabricEXIException(String.format("Invalid target language '%s'. Use one of [java, cpp].", targetLanguage));
    }
  }

  /**
   * Check parameter for the main class name. This property is optional.
   * However, it is strongly recommended to provide a value, because
   * otherwise "Main" is used as default.
   */
  private void checkMainClassName()
  {
    String className = this.properties.getProperty(MAIN_CLASS_NAME_KEY, "Main");

    // Capitalize first letter of class name
    if (null != className)
    {
      this.properties.setProperty(MAIN_CLASS_NAME_KEY,
              className.substring(0, 1).toUpperCase() + className.substring(1, className.length()));
    }
  }

  /**
   * Check parameter for the application class name. This property is
   * optional. However, it is strongly recommended to provide a value,
   * because otherwise "Application" is used as default.
   */
  private void checkApplicationClassName()
  {
    String applicationName = this.properties.getProperty(APPLICATION_CLASS_NAME_KEY, "Application");

    // Capitalize first letter of class name
    if (null != applicationName)
    {
      this.properties.setProperty(APPLICATION_CLASS_NAME_KEY,
              applicationName.substring(0, 1).toUpperCase() + applicationName.substring(1, applicationName.length()));
    }
  }

  /**
   * Check parameter for XML framework. This property is optional.
   * However, it is strongly recommended to provide a value, because
   * otherwise the Simple XML library is used as default.
   */
  private void checkXMLFramework()
  {
    String xmlFramework = this.properties.getProperty(XML_FRAMEWORK_KEY, "Simple");

    // Use Simple as default XML library
    if (null != xmlFramework && !xmlFramework.equals("Simple") && !xmlFramework.equals("XStream") && !xmlFramework.equals("JAXB"))
    {
      this.properties.setProperty(XML_FRAMEWORK_KEY, "Simple");
      xmlFramework = this.properties.getProperty(XML_FRAMEWORK_KEY);
    }

    // Use XStream as XML library
    if (xmlFramework.equals("XStream"))
    {
      this.properties.setProperty(XMLLIBRARY_NAME_KEY, "fabric.module.exi.java.lib.xml.XStream");
    }
    // Use JAXB as XML library
    else if (xmlFramework.equals("JAXB"))
    {
      this.properties.setProperty(XMLLIBRARY_NAME_KEY, "fabric.module.exi.java.lib.xml.JAXB");
    }
    // Use Simple as default XML library
    else
    {
      this.properties.setProperty(XMLLIBRARY_NAME_KEY, "fabric.module.exi.java.lib.xml.Simple");
    }
  }

  /**
   * Check parameter for the package name. This property is optional.
   * However, it is strongly recommended to provide a value, because
   * otherwise "de.nptech.fabric" is used as default.
   */
  private void checkPackageName()
  {
    String packageName = this.properties.getProperty(PACKAGE_NAME_KEY, "de.nptech.fabric");

    // Convert package name to lower case
    if (null != packageName)
    {
      this.properties.setProperty(PACKAGE_NAME_KEY, packageName.toLowerCase());
    }
  }

  /**
   * Check parameter for EXI library. This property is optional,
   * because it only applies to the EXI code generator for Java.
   * In case that no library name is provided, the EXIficient
   * framework is used as default.
   */
  private void checkEXILibrary()
  {
    String exiLibrary = this.properties.getProperty(EXI_LIBRARY_KEY, "EXIficient");

    // Use EXIficient as default EXI library
    if (null != exiLibrary && !exiLibrary.equals("EXIficient") && !exiLibrary.equals("OpenEXI"))
    {
      this.properties.setProperty(EXI_LIBRARY_KEY, "EXIficient");
      exiLibrary = this.properties.getProperty(EXI_LIBRARY_KEY);
    }

    // Use OpenEXI as EXI library
    if (exiLibrary.equals("OpenEXI"))
    {
      this.properties.setProperty(EXILIBRARY_NAME_KEY, "fabric.module.exi.java.lib.exi.OpenEXI");
    }
    // Use EXIficient as default EXI library
    else
    {
      this.properties.setProperty(EXILIBRARY_NAME_KEY, "fabric.module.exi.java.lib.exi.EXIficient");
    }
  }
}
