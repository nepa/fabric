/** 30.09.2012 06:50 */
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
 * @author seidel
 */
public class MidGen4JWebSocketsModule implements FModuleBase
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JWebSocketsModule.class);

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
   * This method returns a Fabric handler object for the MidGen4J
   * WebSockets extension. The handler on its part will generate
   * a WebSockets interface to access the central service provider
   * class that is created by the MidGen4J base module. The item
   * handler is instantiated with the current workspace and module
   * options.
   *
   * @param workspace Workspace object for generation of WebSockets
   * interface
   *
   * @return List with one MidGen4JWebSocketsHandler object
   *
   * @throws Exception Error during handler instantiation
   */
  @Override
  public ArrayList<FItemHandlerBase> getHandlers(Workspace workspace) throws Exception
  {
    ArrayList<FItemHandlerBase> handlers = new ArrayList<FItemHandlerBase>();
    handlers.add(new MidGen4JWebSocketsHandler(workspace, this.properties));

    return handlers;
  }
}
