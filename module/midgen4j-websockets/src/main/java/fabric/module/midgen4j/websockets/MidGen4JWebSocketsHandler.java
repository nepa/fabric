/** 30.09.2012 06:52 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import fabric.module.api.FDefaultWSDLHandler;

/**
 * TODO: Implement class and add comment
 *
 * @author seidel
 */
public class MidGen4JWebSocketsHandler extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JWebSocketsHandler.class);

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /**
   * Constructor initializes the MidGen4J WebSockets handler,
   * which can create a WebSockets service interface.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public MidGen4JWebSocketsHandler(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;
  }
}
