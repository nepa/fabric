/** 23.10.2012 18:39 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.js.JSCommentImpl;
import de.uniluebeck.sourcegen.js.JSField;
import de.uniluebeck.sourcegen.js.JSFunction;
import de.uniluebeck.sourcegen.js.JSSourceFile;
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

  /**
   * Create class with RESTful service interface before processing
   * any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeBeforeProcessing() throws Exception
  {
    JSSourceFile jssf = this.workspace.getJavaScript().getJSSourceFile("application"); // TODO: Use property to set application file name
    
    // Add code before fields
    jssf.getCodeBeforeFields().setCode("\'use strict\';");
    
    // Add global fields
    jssf.add(JSField.factory.create("socket").setComment(new JSCommentImpl("Reference to Atmosphere\'s main client object.")));
    jssf.add(JSField.factory.create("request").setComment(new JSCommentImpl("Object for sending requests to server.")));
    jssf.add(JSField.factory.create("subSocket").setComment(new JSCommentImpl("Subscribed socket with actual connection to server.")));
    
    // Add global function to open connection
    JSFunction openConnection = JSFunction.factory.create("openConnection");
    openConnection.setComment(new JSCommentImpl("Open connection to server."));
    String methodBody = "subSocket = socket.subscribe(request);";
    openConnection.getBody().setCode(methodBody);
    jssf.add(openConnection);

    // Add global function to close connection
    JSFunction closeConnection = JSFunction.factory.create("closeConnection");
    closeConnection.setComment(new JSCommentImpl("Close connection to server."));
    methodBody = "socket.unsubscribe();";
    closeConnection.getBody().setCode(methodBody);
    jssf.add(closeConnection);

    // Add global function to send message
    JSFunction sendMessage = JSFunction.factory.create("sendMessage", "message");
    sendMessage.setComment(new JSCommentImpl("Send message to server."));
    methodBody = "subSocket.push(message);";
    sendMessage.getBody().setCode(methodBody);
    jssf.add(sendMessage);

    // Add code to end of file
    jssf.getCodeAfterFunctions().setCode(this.createAtmosphereJQueryCode());
  }

  private String createAtmosphereJQueryCode()
  {
    String codeBlock = "";

    codeBlock = String.format(
            "/**\n" +
            " * Establish connection to server and define callbacks.\n" +
            " */\n" +
            "$(function()\n" +
            "{\n" +
            "\tsocket = $.atmosphere;\n\n" +

            "\trequest = new $.atmosphere.AtmosphereRequest();\n" +
            "\trequest.url = document.location.toString() + \'%s\';\n" +
            "\trequest.contentType = \'application/json\';\n" +
            "\trequest.transport = \'%s\';\n" +
            "\trequest.fallbackTransport = \'%s\';\n" +
            "\trequest.shared = true;\n" +
            "\trequest.enableXDR = true;\n" +
            "\trequest.headers = { \'Access-Control-Allow-Origin\': \'*\' };\n\n" + // TODO: Do we really need this?

            "\trequest.onOpen = function(response) {\n" +
            "\t\talert(\'Connection opened.\');\n" +
            "\t}\n\n" +

            "\trequest.onClose = function(response) {\n" +
            "\t\talert(\'Connection closed.\');\n" +
            "\t}\n\n" +

            "\trequest.onTransportFailure = function(request) {\n" +
            "\t\talert(\'This browser or the remote server does not support WebSockets.\');\n" +
            "\t}\n\n" +

            "\trequest.onMessage = function(response) {\n" +
            "\t\tvar message = response.responseBody;\n\n" +
            "\t\talert(\'Message received: \' + message);\n" +
            "\t}\n" +

            "});",
            "channelName", "websocket", "streaming"); // TODO: Add real arguments

    return codeBlock;
  }
}
