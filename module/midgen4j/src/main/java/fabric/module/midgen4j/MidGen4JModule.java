/** 04.09.2012 15:26 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;

import fabric.module.api.FItemHandlerBase;
import fabric.module.api.FModuleBase;

import de.uniluebeck.sourcegen.Workspace;

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

  /** Key for bean package name in properties object */
  public static final String BEAN_PACKAGE_NAME_KEY = "midgen4j.bean_package_name";

  /** Alternative key for bean package name */
  public static final String BEAN_PACKAGE_NAME_ALT_KEY = "typegen.java.package_name";

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
            "Valid options are '%s', '%s' and '%s'. " +
            "Alternatively '%s' can be used.",
            SERVICE_PROVIDER_CLASS_NAME_KEY, PACKAGE_NAME_KEY,
            BEAN_PACKAGE_NAME_KEY, BEAN_PACKAGE_NAME_ALT_KEY);
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
   * not be called in the module's constructor, but in the getHandler()
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
    this.checkBeanPackageName();

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
   * otherwise "fabric.package.default" is used as default.
   */
  private void checkPackageName()
  {
    String packageName = this.properties.getProperty(PACKAGE_NAME_KEY, "fabric.package.default");

    // Convert package name to lower case
    if (null != packageName)
    {
      this.properties.setProperty(PACKAGE_NAME_KEY, packageName.toLowerCase());
    }
  }

  /**
   * Check parameter for the package name of custom type classes (JavaBeans).
   * This property is optional. However, it is strongly recommended to provide
   * a value, because otherwise "fabric.package.default" is used as default.
   */
  private void checkBeanPackageName()
  {
    String beanPackageName = this.properties.getProperty(BEAN_PACKAGE_NAME_KEY, "fabric.package.default");

    // Convert package name to lower case
    if (null != beanPackageName)
    {
      this.properties.setProperty(BEAN_PACKAGE_NAME_KEY, beanPackageName.toLowerCase());
    }
  }
}
