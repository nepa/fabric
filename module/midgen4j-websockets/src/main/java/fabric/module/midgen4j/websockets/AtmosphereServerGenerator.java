/** 07.03.2013 16:31 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JSourceFile;
import fabric.module.api.FDefaultWSDLHandler;

/**
 * TODO: Implement class and add comment
 *
 * @author seidel
 */
public class AtmosphereServerGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(AtmosphereServerGenerator.class);

  // TODO: Add comment
  public static final String SERVER_CLASS_NAME = "Server";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  // TODO: Add comment
  public AtmosphereServerGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    // TODO: Block begin
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(
            this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY), SERVER_CLASS_NAME);

    JClass serverClass = JClass.factory.create(JModifier.PUBLIC, SERVER_CLASS_NAME);
    jsf.add(serverClass);
    // TODO: Block end
  }
}
