/** 26.10.2012 03:12 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.js.JSClass;
import de.uniluebeck.sourcegen.js.JSCommentImpl;
import de.uniluebeck.sourcegen.js.JSField;
import de.uniluebeck.sourcegen.js.JSFunction;
import de.uniluebeck.sourcegen.js.JSMethod;
import de.uniluebeck.sourcegen.js.JSSourceFile;
import fabric.module.api.FDefaultWSDLHandler;
import fabric.wsdlschemaparser.wsdl.FOperation;
import fabric.wsdlschemaparser.wsdl.FPortType;

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
    jssf.add(JSField.factory.create("dispatcher").setComment(new JSCommentImpl("Manager for tracked RPC method calls.")));

    // Add GUID and dispatcher class
    jssf.add(this.createGUIDClass());
    jssf.add(this.createDispatcherClass());

    // Add global function to open connection
    JSFunction openConnection = JSFunction.factory.create("openConnection");
    openConnection.setComment(new JSCommentImpl("Open connection to server."));
    String methodBody =
            "subSocket = socket.subscribe(request);\n\n" +
            "dispatcher = new Dispatcher(subSocket);";
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
    sendMessage.setComment(new JSCommentImpl("Send untracked message to server."));
    methodBody =
            "// This bypasses the tracking mechanism!\n" +
            "subSocket.push(message);";
    sendMessage.getBody().setCode(methodBody);
    jssf.add(sendMessage);

    // Add code to end of file
    jssf.getCodeAfterFunctions().setCode(this.createAtmosphereJQueryCode());
  }

  /**
   * Process port types (WSDL 2.0: interfaces) that are defined in WSDL document.
   *
   * @param portTypes Port types of WSDL document
   *
   * @throws Exception Error during processing
   */
  @Override
  public void processPortTypes(final HashSet<FPortType> portTypes) throws Exception
  {
    JSSourceFile jssf = this.workspace.getJavaScript().getJSSourceFile("application"); // TODO: Use property to set application file name

    // Process all service operations
    for (FPortType portType: portTypes)
    {
      for (FOperation operation: portType.getOperations())
      {
        String name = operation.getOperationName();

        JSFunction function = JSFunction.factory.create(name);
        function.setComment(new JSCommentImpl(String.format("Call the '%s' remote method.", name)));
        String methodBody = String.format(
                "if (dispatcher != undefined) {\n" +
                "\tdispatcher.trackedCall(\'%s\', JSON.stringify({ /* TODO: Add your JSON code here */ }),\n" + // TODO: Only send JSON if operation has input
                "\t\tfunction(response) {\n" +
                "\t\t\tconsole.log('Got response to %s() request: ' + JSON.parse(response).result);\n" +
                "\t\t});\n" +
                "\t}\n" +
                "}",
                name, name);
        function.getBody().setCode(methodBody);
        jssf.add(function);
      }
    }
  }

  // TODO: Add comment
  private JSClass createGUIDClass() throws Exception
  {
    JSClass guidClass = JSClass.factory.create("GUID");
    guidClass.setComment(new JSCommentImpl("Helper class to generate unique IDs."));

    JSMethod s4 = JSMethod.factory.create("S4");
    s4.setComment(new JSCommentImpl("Create a block of four random characters."));
    String methodBody =
            "return Math.floor(\n" +
            "\tMath.random() * 0x10000 /* 65536 */\n" +
            ").toString(16);";
    s4.getBody().setCode(methodBody);
    guidClass.add(s4);

    JSMethod create = JSMethod.factory.create("create");
    create.setComment(new JSCommentImpl("Return a new GUID."));
    methodBody =
            "return (\n" +
            "\tthis.S4() + this.S4() + \'-\' +\n" +
            "\tthis.S4() + \'-\' +\n" +
            "\tthis.S4() + \'-\' +\n" +
            "\tthis.S4() + \'-\' +\n" +
            "\tthis.S4() + this.S4() + this.S4()\n" +
            ");";
    create.getBody().setCode(methodBody);
    guidClass.add(create);

    return guidClass;
  }

  // TODO: Add comment
  private JSClass createDispatcherClass() throws Exception
  {
    JSClass dispatcherClass = JSClass.factory.create("Dispatcher", "subSocket");
    dispatcherClass.setComment(new JSCommentImpl("Dispatcher class to handle RPC methods and track messages."));

    JSField delimiter = JSField.factory.create("delimiter", "\'$\'");
    delimiter.setComment(new JSCommentImpl("Delimiter for protocol parts."));
    dispatcherClass.add(delimiter);

    JSField socket = JSField.factory.create("socket", "subSocket");
    socket.setComment(new JSCommentImpl("Connection to server."));
    dispatcherClass.add(socket);

    JSField pendingMessages = JSField.factory.create("pendingMessages", "new Object()");
    pendingMessages.setComment(new JSCommentImpl("List of pending messages."));
    dispatcherClass.add(pendingMessages);

    JSMethod trackedCall = JSMethod.factory.create("trackedCall", "method", "payload", "callback");
    trackedCall.setComment(new JSCommentImpl("Execute tracked RPC method call."));
    String methodBody =
            "var uuid = new GUID().create();\n\n" +
            "// Add call to pending messages\n" +
            "this.pendingMessages[uuid] = callback;\n\n" +
            "console.log(\'Now tracking call of method \' + method + \' with GUID \' + uuid + \'.\');\n\n" +
            "this.socket.push(uuid + this.delimiter + method + this.delimiter + payload);";
    trackedCall.getBody().setCode(methodBody);
    dispatcherClass.add(trackedCall);

    JSMethod dispatch = JSMethod.factory.create("dispatch", "response");
    dispatch.setComment(new JSCommentImpl("Dispatch response to a tracked message."));
    methodBody =
            "var data = response.split(this.delimiter);\n\n" +

            "if (data.length != 3) {\n" +
            "\tconsole.log(\'Invalid response format.\');\n" +
            "}\n" +
            "else {\n" +
            "\tvar uuid = data[0];\n" +
            "\tvar method = data[1];\n" +
            "\tvar payload = data[2];\n\n" +

            "\t// UUID lookup\n" +
            "\tif (!(uuid in this.pendingMessages)) {\n" +
            "\t\tconsole.log(\'Response with untracked message ID.\');\n" +
            "\t}\n" +
            "\telse {\n" +
            "\t\t// Pass payload to registered callback\n" +
            "\t\tif (this.pendingMessages[uuid] != undefined && typeof this.pendingMessages[uuid] == 'function') {\n" +
            "\t\t\tconsole.log(\'Now dispatching result of method \' + method + \' with UUID \' + uuid + \'.\');\n\n" +

            "\t\t\t// Execute callback method\n" +
            "\t\t\tthis.pendingMessages[uuid](payload);\n\n" +

            "\t\t\t// Remove call from pending list\n" +
            "\t\t\tdelete this.pendingMessages[uuid];\n" +
            "\t\t}\n" +
            "\t\telse {\n" +
            "\t\t\tconsole.log(\'Could not execute callback.\');\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";
    dispatch.getBody().setCode(methodBody);
    dispatcherClass.add(dispatch);

    return dispatcherClass;
  }

  private String createAtmosphereJQueryCode()
  {
    String codeBlock = "";

    codeBlock = String.format(
            "/**\n" +
            " * Establish connection to server and define callbacks.\n" +
            " *\n" +
            " * Will be called by jQuery, when DOM is ready.\n" +
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
            "\t\tconsole.log(\'Connection opened.\');\n" +
            "\t}\n\n" +

            "\trequest.onClose = function(response) {\n" +
            "\t\tconsole.log(\'Connection closed.\');\n" +
            "\t}\n\n" +

            "\trequest.onTransportFailure = function(request) {\n" +
            "\t\tconsole.log(\'This browser or the remote server does not support WebSockets.\');\n" +
            "\t}\n\n" +

            "\trequest.onMessage = function(response) {\n" +
            "\t\tvar message = response.responseBody;\n\n" +
            "\t\tconsole.log(\'Message received: \' + message);\n" +
            "\t\tdispatcher.dispatch(message);\n" +
            "\t}\n" +

            "});",
            "channelName", "websocket", "streaming"); // TODO: Add real arguments

    return codeBlock;
  }
}
