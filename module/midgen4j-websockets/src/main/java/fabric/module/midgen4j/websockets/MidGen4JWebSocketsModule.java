/** 02.03.2013 02:25 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import fabric.module.api.FItemHandlerBase;
import fabric.module.api.FModuleBase;

import de.uniluebeck.sourcegen.Workspace;

/**
 * Fabric extension module for the Java Middleware Generator. This class
 * is part of the MidGen4J distribution and can be used to generate a
 * WebSockets interface for the ServiceProvider class that the MidGen4J
 * base module creates.
 *
 * The generated code uses the Atmosphere Framework to uniformly support
 * WebSockets and other Comet technologies on various servlet containers
 * and across various browsers on the client-side.
 *
 * Also see:
 *   https://github.com/Atmosphere/atmosphere
 *
 * For proper JSON serialization of the JavaBeans that are generated by
 * Fabric's type generator module, you need to choose JAXB as preferred
 * XML framework in the TypeGen Module.
 *
 * @author seidel
 */
public class MidGen4JWebSocketsModule implements FModuleBase
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JWebSocketsModule.class);

  /** Key for interface class name in properties object */
  public static final String INTERFACE_CLASS_NAME_KEY = "midgen4j.websockets.interface_class_name";

  /** Key for package name in properties object */
  public static final String PACKAGE_NAME_KEY = "midgen4j.websockets.package_name";

  /** Alternative key for package name */
  public static final String PACKAGE_NAME_ALT_KEY = "midgen4j.package_name";

  /** Key for path to project in properties object */
  public static final String PROJECT_PATH_KEY = "midgen4j.websockets.project_path";

  /** Key for Maven group id of project in properties object */
  public static final String MAVEN_GROUP_ID_KEY = "midgen4j.websockets.maven_group_id";

  /** Key for Maven artifact id of project in properties object */
  public static final String MAVEN_ARTIFACT_ID_KEY = "midgen4j.websockets.maven_artifact_id";

  /** Key for JavaScript application name in properties object */
  public static final String JS_APPLICATION_NAME_KEY = "midgen4j.websockets.js_application_name";

  /** Key for name of broadcast channel in properties object */
  public static final String CHANNEL_NAME_KEY = "midgen4j.websockets.channel_name";

  /** Key for transport technology in properties object */
  public static final String TRANSPORT_KEY = "midgen4j.websockets.transport";

  /** Key for fallback transport technology in properties object */
  public static final String FALLBACK_TRANSPORT_KEY = "midgen4j.websockets.fallback_transport";

  /** Key for service provider class name in properties object */
  public static final String SERVICE_PROVIDER_CLASS_NAME_KEY = "midgen4j.websockets.service_provider_class_name";

  /** Alternative key for service provider class name */
  public static final String SERVICE_PROVIDER_CLASS_NAME_ALT_KEY = "midgen4j.service_provider_class_name";

  /** Key for service provider package name in properties object */
  public static final String SERVICE_PROVIDER_PACKAGE_NAME_KEY = "midgen4j.websockets.service_provider_package_name";

  /** Alternative key for service provider package name */
  public static final String SERVICE_PROVIDER_PACKAGE_NAME_ALT_KEY = "midgen4j.package_name";

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
    return String.format("MidGen4J extension to create WebSockets interface. " +
            "Valid options are '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s' and '%s'. " +
            "Alternatively '%s', '%s' and '%s' can be used.",
            INTERFACE_CLASS_NAME_KEY, PACKAGE_NAME_KEY, JS_APPLICATION_NAME_KEY,
            PROJECT_PATH_KEY, MAVEN_GROUP_ID_KEY, MAVEN_ARTIFACT_ID_KEY,
            CHANNEL_NAME_KEY, TRANSPORT_KEY, FALLBACK_TRANSPORT_KEY,
            SERVICE_PROVIDER_CLASS_NAME_KEY, SERVICE_PROVIDER_PACKAGE_NAME_KEY,
            PACKAGE_NAME_ALT_KEY, SERVICE_PROVIDER_CLASS_NAME_ALT_KEY,
            SERVICE_PROVIDER_PACKAGE_NAME_ALT_KEY);
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
  public ArrayList<FItemHandlerBase> getHandlers(final Workspace workspace) throws Exception
  {
    this.validateProperties();

    ArrayList<FItemHandlerBase> handlers = new ArrayList<FItemHandlerBase>();
    handlers.add(new ProjectFileGenerator(workspace, this.properties));
    handlers.add(new AtmosphereJQueryGenerator(workspace, this.properties));
    handlers.add(new CORSFilterGenerator(workspace, this.properties));
    handlers.add(new JSONMarshallerGenerator(workspace, this.properties));
    handlers.add(new WorkerThreadGenerator(workspace, this.properties));

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
    this.checkInterfaceClassName();
    this.checkPackageName();
    this.checkProjectPath();
    this.checkMavenGroupId();
    this.checkMavenArtifactId();
    this.checkJSApplicationName();
    this.checkChannelName();
    this.checkTransport();
    this.checkFallbackTransport();
    this.checkServiceProviderClassName();
    this.checkServiceProviderPackageName();

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
   * Private helper method to determine which properties should be
   * used. The WebSockets extension module can copy properties from
   * the MidGen4J base module, if appropriate values are set.
   */
  private void copyAlternativeProperties()
  {
    if (!isSet(PACKAGE_NAME_KEY) && isSet(PACKAGE_NAME_ALT_KEY))
    {
      copyProperty(PACKAGE_NAME_ALT_KEY, PACKAGE_NAME_KEY);
    }

    if (!isSet(SERVICE_PROVIDER_PACKAGE_NAME_KEY) && isSet(SERVICE_PROVIDER_PACKAGE_NAME_ALT_KEY))
    {
      copyProperty(SERVICE_PROVIDER_PACKAGE_NAME_ALT_KEY, SERVICE_PROVIDER_PACKAGE_NAME_KEY);
    }

    if (!isSet(SERVICE_PROVIDER_CLASS_NAME_KEY) && isSet(SERVICE_PROVIDER_CLASS_NAME_ALT_KEY))
    {
      copyProperty(SERVICE_PROVIDER_CLASS_NAME_ALT_KEY, SERVICE_PROVIDER_CLASS_NAME_KEY);
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
   * Check parameter for the WebSockets interface class name. This property
   * is optional. However, it is strongly recommended to provide a value,
   * because otherwise "WebSocketsInterface" is used as default.
   */
  private void checkInterfaceClassName()
  {
    String className = this.properties.getProperty(INTERFACE_CLASS_NAME_KEY, "WebSocketsInterface");

    // Capitalize first letter of class name
    if (null != className)
    {
      this.properties.setProperty(INTERFACE_CLASS_NAME_KEY,
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
   * Check parameter for desired path of generated project. This
   * property is optional. However, it is strongly recommended
   * to provide a value, because otherwise "websockets-project"
   * is used as default.
   */
  private void checkProjectPath()
  {
    String projectPath = this.properties.getProperty(PROJECT_PATH_KEY, "websockets-project");

    // Replace UNIX and Windows separators
    if (null != projectPath)
    {
      projectPath = projectPath.replace('/', File.separatorChar);
      projectPath = projectPath.replace('\\', File.separatorChar);

      this.properties.setProperty(PROJECT_PATH_KEY, projectPath);
    }
  }

  /**
   * Check parameter for the Maven group id. This property is optional.
   * However, it is strongly recommended to provide a value, because
   * otherwise "de.nptech.fabric" is used as default.
   *
   * Also see:
   *   http://maven.apache.org/pom.html#Maven_Coordinates
   */
  private void checkMavenGroupId()
  {
    String groupId = this.properties.getProperty(MAVEN_GROUP_ID_KEY, "de.nptech.fabric");

    // Convert group id to lower case
    if (null != groupId)
    {
      this.properties.setProperty(MAVEN_GROUP_ID_KEY, groupId.toLowerCase());
    }
  }

  /**
   * Check parameter for the Maven artifact id. This property is
   * optional. However, it is strongly recommended to provide a
   * value, because otherwise "WebSocketsInterface" is used as
   * default.
   *
   * Also see:
   *   http://maven.apache.org/pom.html#Maven_Coordinates
   */
  private void checkMavenArtifactId()
  {
    String artifactId = this.properties.getProperty(MAVEN_ARTIFACT_ID_KEY, "WebSocketsInterface");

    // Capitalize first letter of artifact id
    if (null != artifactId)
    {
      this.properties.setProperty(MAVEN_ARTIFACT_ID_KEY,
              artifactId.substring(0, 1).toUpperCase() + artifactId.substring(1, artifactId.length()));
    }
  }

  /**
   * Check parameter for the JavaScript application name, i.e. the
   * client that eventually runs in the user's webbrowser. This
   * property is optional. However, it is strongly recommended to
   * provide a value, because otherwise "application" is used as
   * default.
   */
  private void checkJSApplicationName()
  {
    String applicationName = this.properties.getProperty(JS_APPLICATION_NAME_KEY);

    // Set default application name, if none is provided
    if (null == applicationName)
    {
      this.properties.setProperty(JS_APPLICATION_NAME_KEY, "application");
    }
  }

  /**
   * Check parameter for name of the broadcasting channel. This property
   * is optional. However, it is strongly recommended to provide a value,
   * because otherwise "midgen4j" is used as default.
   */
  private void checkChannelName()
  {
    String channelName = this.properties.getProperty(CHANNEL_NAME_KEY);

    // Set default channel name, if none is provided
    if (null == channelName)
    {
      this.properties.setProperty(CHANNEL_NAME_KEY, "midgen4j");
    }
  }

  /**
   * Check parameter for favored transport technology. This property is
   * optional. However, it is strongly recommended to provide a value,
   * because otherwise "websocket" is used as default. For valid options
   * see documentation of isValidTransport() method.
   */
  private void checkTransport()
  {
    String transportMethod = this.properties.getProperty(TRANSPORT_KEY);

    // Transport method is valid
    if (this.isValidTransport(transportMethod))
    {
      this.properties.setProperty(TRANSPORT_KEY, transportMethod.toLowerCase());
    }
    // Use default transport method otherwise
    else
    {
      this.properties.setProperty(TRANSPORT_KEY, "websocket");
    }
  }

  /**
   * Check parameter for secondary transport technology. This property is
   * optional. However, it is strongly recommended to provide a value,
   * because otherwise "streaming" is used as default. For valid options
   * see documentation of isValidTransport() method.
   */
  private void checkFallbackTransport()
  {
    String transportMethod = this.properties.getProperty(FALLBACK_TRANSPORT_KEY);

    // Fallback transport method is valid
    if (this.isValidTransport(transportMethod))
    {
      this.properties.setProperty(FALLBACK_TRANSPORT_KEY, transportMethod.toLowerCase());
    }
    // Use default fallback transport method otherwise
    else
    {
      this.properties.setProperty(FALLBACK_TRANSPORT_KEY, "streaming");
    }
  }

  /**
   * Private helper method to validate transport technologies that are
   * supported by the Atmosphere Framework. Valid options are "websocket",
   * "streaming", "polling", "long-polling", "jsonp" and "sse".
   *
   * Also see "transport" and "fallbackTransport" here:
   *   https://github.com/Atmosphere/atmosphere/wiki/jQuery.atmosphere.js-API
   *
   * @param transportMethod Transport method to validate
   *
   * @return True if transport method is valid, false otherwise
   */
  private boolean isValidTransport(final String transportMethod)
  {
    return (null != transportMethod &&
            (transportMethod.toLowerCase().equals("websocket") ||
             transportMethod.toLowerCase().equals("streaming") ||
             transportMethod.toLowerCase().equals("polling") ||
             transportMethod.toLowerCase().equals("long-polling") ||
             transportMethod.toLowerCase().equals("jsonp") ||
             transportMethod.toLowerCase().equals("sse")));
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
   * Check parameter for package name of the service provider class. This
   * property is optional. However, it is strongly recommended to provide
   * a value, because otherwise "de.nptech.fabric" is used as default.
   */
  private void checkServiceProviderPackageName()
  {
    String packageName = this.properties.getProperty(SERVICE_PROVIDER_PACKAGE_NAME_KEY, "de.nptech.fabric");

    // Convert package name to lower case
    if (null != packageName)
    {
      this.properties.setProperty(SERVICE_PROVIDER_PACKAGE_NAME_KEY, packageName.toLowerCase());
    }
  }
}
