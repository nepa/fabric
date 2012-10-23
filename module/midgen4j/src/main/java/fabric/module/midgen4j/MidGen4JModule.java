/** 08.10.2012 23:29 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import fabric.module.api.FItemHandlerBase;
import fabric.module.api.FModuleBase;
import fabric.module.midgen4j.exceptions.MidGen4JException;

/**
 * Fabric base module for the Java Middleware Generator. This class is
 * capable of generating the method stubs for a ServiceProvider class.
 * Application developers can extend the generated code and implement
 * their own business logic in it.
 *
 * Subsequently, additional MidGen4J modules can be called, to provide
 * further code for network communication based on different dedicated
 * Java middleware technologies.
 *
 * @author seidel
 */
public class MidGen4JModule implements FModuleBase
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JModule.class);

  /** Key for service provider class name in properties object */
  public static final String SERVICE_PROVIDER_CLASS_NAME_KEY = "midgen4j.service_provider_class_name";

  /** Key for package name in properties object */
  public static final String PACKAGE_NAME_KEY = "midgen4j.package_name";

  /** Key for name of main bean class in properties object */
  public static final String BEAN_MAIN_CLASS_NAME_KEY = "midgen4j.bean_main_class_name";

  /** Alternative key for name of main bean class */
  public static final String BEAN_MAIN_CLASS_NAME_ALT_KEY = "typegen.main_class_name";

  /** Key for bean package name in properties object */
  public static final String BEAN_PACKAGE_NAME_KEY = "midgen4j.bean_package_name";

  /** Alternative key for bean package name */
  public static final String BEAN_PACKAGE_NAME_ALT_KEY = "typegen.java.package_name";

  /** Key for flag to skip creation of main bean class in properties object */
  public static final String SKIP_BEAN_MAIN_CLASS_KEY = "midgen4j.skip_bean_main_class";

  /** Properties object for module configuration */
  private Properties properties;

  /**
   * Constructor initializes the internal properties object.
   *
   * @param properties Properties object with module options
   */
  public MidGen4JModule(Properties properties)
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
    return "midgen4j";
  }

  /**
   * Helper method to return module description.
   *
   * @return Module description
   */
  @Override
  public String getDescription()
  {
    return String.format("Base module of the Java Middleware Generator. " +
            "Valid options are '%s', '%s', '%s', '%s' and '%s'. " +
            "Alternatively '%s' and '%s' can be used.",
            SERVICE_PROVIDER_CLASS_NAME_KEY, PACKAGE_NAME_KEY, BEAN_MAIN_CLASS_NAME_KEY,
            BEAN_PACKAGE_NAME_KEY, SKIP_BEAN_MAIN_CLASS_KEY,
            BEAN_MAIN_CLASS_NAME_ALT_KEY, BEAN_PACKAGE_NAME_ALT_KEY);
  }

  /**
   * This method returns a Fabric handler object for the Java Middleware
   * Generator. The handler on its part will generate a central service
   * provider class that can be extended by business logic individually
   * later. The item handler is instantiated with the current workspace
   * and module options.
   *
   * @param workspace Workspace object for service provider generation
   *
   * @return List with one MidGen4JHandler object
   *
   * @throws Exception Error during handler instantiation
   */
  @Override
  public ArrayList<FItemHandlerBase> getHandlers(Workspace workspace) throws Exception
  {
    this.validateProperties();

    ArrayList<FItemHandlerBase> handlers = new ArrayList<FItemHandlerBase>();
    handlers.add(new MidGen4JHandler(workspace, this.properties));

    return handlers;
  }

  /**
   * This method validates all module-specific options and translates
   * them where needed. The constructor of this class is called during
   * Fabric setup. At that time, however, the Java properties file from
   * the command line is not yet processed. This is why this method must
   * not be called in the module's constructor, but in the getHandlers()
   * method.
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
    this.checkServiceProviderClassName();
    this.checkPackageName();
    this.checkBeanMainClassName();
    this.checkBeanPackageName();
    this.checkSkipBeanMainClass();

    // Print MidGen4J module properties for debug purposes
    for (String key: this.properties.stringPropertyNames())
    {
      if (key.startsWith("midgen4j."))
      {
        LOGGER.debug(String.format("Property '%s' has value '%s'.", key, this.properties.getProperty(key)));
      }
    }
  }

  /**
   * Private helper method to determine which properties should be
   * used. The MidGen4J module can copy properties from TypeGen module,
   * if appropriate values are set.
   */
  private void copyAlternativeProperties()
  {
    if (!isSet(BEAN_MAIN_CLASS_NAME_KEY) && isSet(BEAN_MAIN_CLASS_NAME_ALT_KEY))
    {
      copyProperty(BEAN_MAIN_CLASS_NAME_ALT_KEY, BEAN_MAIN_CLASS_NAME_KEY);
    }

    if (!isSet(BEAN_PACKAGE_NAME_KEY) && isSet(BEAN_PACKAGE_NAME_ALT_KEY))
    {
      copyProperty(BEAN_PACKAGE_NAME_ALT_KEY, BEAN_PACKAGE_NAME_KEY);
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
   * Check parameter for the service provider class name. This property
   * is optional. However, it is strongly recommended to provide a value,
   * because otherwise "ServiceProvider" is used as default.
   */
  private void checkServiceProviderClassName()
  {
    String className = this.properties.getProperty(SERVICE_PROVIDER_CLASS_NAME_KEY, "ServiceProvider");

    // Capitalize first letter of class name
    if (null != className)
    {
      this.properties.setProperty(SERVICE_PROVIDER_CLASS_NAME_KEY,
              className.substring(0, 1).toUpperCase() + className.substring(1, className.length()));
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
   * Check parameter for the name of the main bean class. This property
   * is optional. However, it is strongly recommended to provide a value,
   * because otherwise "Main" is used as default.
   */
  private void checkBeanMainClassName()
  {
    String className = this.properties.getProperty(BEAN_MAIN_CLASS_NAME_KEY, "Main");

    // Capitalize first letter of class name
    if (null != className)
    {
      this.properties.setProperty(BEAN_MAIN_CLASS_NAME_KEY,
              className.substring(0, 1).toUpperCase() + className.substring(1, className.length()));
    }
  }

  /**
   * Check parameter for the package name of custom type classes (JavaBeans).
   * This property is optional. However, it is strongly recommended to provide
   * a value, because otherwise "de.nptech.fabric" is used as default.
   */
  private void checkBeanPackageName()
  {
    String beanPackageName = this.properties.getProperty(BEAN_PACKAGE_NAME_KEY, "de.nptech.fabric");

    // Convert package name to lower case
    if (null != beanPackageName)
    {
      this.properties.setProperty(BEAN_PACKAGE_NAME_KEY, beanPackageName.toLowerCase());
    }
  }

  /**
   * Check parameter that determines, whether the main bean class
   * that was created by the TypeGen module should be removed from
   * the Java workspace before write-out or not. This property is
   * optional. However, it is strongly recommended to provide a
   * value, because otherwise "false" is used as default.
   *
   * @throws Exception Invalid value passed for property
   */
  private void checkSkipBeanMainClass() throws Exception
  {
    String skipMain = this.properties.getProperty(SKIP_BEAN_MAIN_CLASS_KEY);

    // Keep main bean class on default or if desired
    if (null == skipMain || skipMain.toLowerCase().equals("false"))
    {
      this.properties.setProperty(SKIP_BEAN_MAIN_CLASS_KEY, "false");
    }
    // Remove file from workspace otherwise
    else if (skipMain.toLowerCase().equals("true"))
    {
      this.properties.setProperty(SKIP_BEAN_MAIN_CLASS_KEY, "true");
    }
    // Invalid value provided
    else
    {
      throw new MidGen4JException(String.format("Invalid value '%s' for flag to skip " +
              "creation of the main bean class. Use one of [true, false].", skipMain));
    }
  }
}
