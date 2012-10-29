/** 29.10.2012 16:41 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;

import fabric.module.api.FItemHandlerBase;
import fabric.module.api.FModuleBase;

import de.uniluebeck.sourcegen.Workspace;

/**
 * TODO: Implement class and add comment
 *
 * TODO: Add validation of properties
 *
 * @author seidel
 */
public class MidGen4JWebSocketsModule implements FModuleBase
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JWebSocketsModule.class);

  /** Key for package name in properties object */
  public static final String PACKAGE_NAME_KEY = "midgen4j.websockets.package_name";

  /** Properties object for module configuration */
  private Properties properties;

  /**
   * Constructor initializes the internal properties object.
   *
   * @param properties Properties object with module options
   */
  public MidGen4JWebSocketsModule(Properties properties)
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
    return "midgen4j-websockets";
  }

  /**
   * Helper method to return module description.
   *
   * @return Module description
   */
  @Override
  public String getDescription()
  {
    return "MidGen4J extension to create WebSockets interface.";
  }

  /**
   * This method returns a list with all Fabric handler objects of
   * the MidGen4J WebSockets extension. The handlers on their part
   * will generate a WebSockets interface to access the central
   * service provider class that is created by the MidGen4J base
   * module. The item handlers are instantiated with the current
   * workspace and module options.
   *
   * @param workspace Workspace object for generation of WebSockets
   * interface
   *
   * @return List with all Fabric handler objects
   *
   * @throws Exception Error during handler instantiation
   */
  @Override
  public ArrayList<FItemHandlerBase> getHandlers(Workspace workspace) throws Exception
  {
    this.validateProperties();

    ArrayList<FItemHandlerBase> handlers = new ArrayList<FItemHandlerBase>();
    handlers.add(new AtmosphereJQueryGenerator(workspace, this.properties));
    handlers.add(new JSONMarshallerGenerator(workspace, this.properties));

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

    // Check properties
    this.checkPackageName();

    // Print MidGen4J-WebSockets module properties for debug purposes
    for (String key: this.properties.stringPropertyNames())
    {
      if (key.startsWith("midgen4j.websockets."))
      {
        LOGGER.debug(String.format("Property '%s' has value '%s'.", key, this.properties.getProperty(key)));
      }
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
}
