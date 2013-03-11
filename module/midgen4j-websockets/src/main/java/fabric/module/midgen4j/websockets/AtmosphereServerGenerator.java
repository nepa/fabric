/** 11.03.2013 21:03 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.exceptions.JDuplicateException;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassAnnotationImpl;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JConstructor;
import de.uniluebeck.sourcegen.java.JConstructorCommentImpl;
import de.uniluebeck.sourcegen.java.JField;
import de.uniluebeck.sourcegen.java.JFieldCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodAnnotationImpl;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;
import fabric.module.api.FDefaultWSDLHandler;

import fabric.wsdlschemaparser.wsdl.FOperation;
import fabric.wsdlschemaparser.wsdl.FPortType;

/**
 * Fabric handler class to extend the Java Middleware Generator. This
 * handler is part of the MidGen4J-WebSockets extension and generates
 * the server-side code to use the Atmosphere framework. Atmosphere
 * provides technical means to connect both, a Java server and a
 * JavaScript client, via WebSockets and a variety of other Comet
 * technologies (e.g. streaming and long-polling).
 *
 * On the server-side, Atmosphere uses its own AtmosphereServlet class
 * to handle incoming requests and send response messages accordingly.
 * However, the code that is generated by this Fabric handler, will
 * also create a dispatching mechanism that implements a higher-level
 * RPC protocol on top of WebSockets.
 *
 * For more information about the Atmoshere framework see:
 *   https://github.com/Atmosphere/atmosphere
 *
 * @author seidel
 */
public class AtmosphereServerGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(AtmosphereServerGenerator.class);

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Names of service operations from WSDL file */
  private Set<String> operationNames;

  /** Name of WebSockets interface class */
  private String interfaceName;

  /** Java package name for WebSocket interface classes */
  private String packageName;

  /** Name of broadcast channel */
  private String channelName;

  /** Name of the service provider class */
  private String serviceProviderClassName;

  /**
   * Constructor initializes the AtmosphereServerGenerator, which
   * can create the server-side part of the WebSockets service
   * interface.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public AtmosphereServerGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    this.operationNames = new HashSet<String>();

    // Extract global properties
    this.interfaceName = this.properties.getProperty(MidGen4JWebSocketsModule.INTERFACE_CLASS_NAME_KEY);
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
    this.channelName = this.properties.getProperty(MidGen4JWebSocketsModule.CHANNEL_NAME_KEY);
    this.serviceProviderClassName = this.properties.getProperty(MidGen4JWebSocketsModule.SERVICE_PROVIDER_CLASS_NAME_KEY);
  }

  /**
   * Create a Java class that contains the WebSockets server,
   * before processing any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    this.createWebSocketsServerFile();
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
    // Process all service operations
    for (FPortType portType: portTypes)
    {
      for (FOperation operation: portType.getOperations())
      {
        // Collect names of all service operations
        this.operationNames.add(operation.getOperationName());
      }
    }
  }

  /**
   * Create a source file that contains the WebSockets server.
   * The method will generate the corresponding Java class and
   * add all necessary Java imports to the file.
   *
   * @throws Exception Error during code generation
   */
  private void createWebSocketsServerFile() throws Exception
  {
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, this.interfaceName);

    // Add WebSockets server class
    jsf.add(this.createWebSocketsServerClass());

    // Add required imports to source file
    this.addRequiredImport(jsf, "org.slf4j.Logger");
    this.addRequiredImport(jsf, "org.slf4j.LoggerFactory");
    this.addRequiredImport(jsf, "java.util.regex.Pattern");
    this.addRequiredImport(jsf, "org.atmosphere.config.service.WebSocketHandlerService");
    this.addRequiredImport(jsf, "org.atmosphere.cpr.BroadcasterFactory");
    this.addRequiredImport(jsf, "org.atmosphere.cpr.MetaBroadcaster");
    this.addRequiredImport(jsf, "org.atmosphere.websocket.WebSocket");
    this.addRequiredImport(jsf, "org.atmosphere.websocket.WebSocketHandlerAdapter");

    // Add imports for worker thread classes
    for (String operationName: this.operationNames)
    {
      String workerThreadClassName = AtmosphereServerGenerator.firstLetterCapital(operationName) + "Worker";

      jsf.addImport(String.format("%s.%s.%s", this.packageName, WorkerThreadGenerator.WORKER_THREAD_PACKAGE_SEGMENT, workerThreadClassName));
    }
  }

  /**
   * Create WebSockets server class and add all neccessary
   * fields and methods to it. The created class extends
   * the WebSocketHandlerAdapter, which defines most of
   * the callbacks that are needed to handle requests of
   * a remote client.
   *
   * @return JClass object with WebSockets server class
   *
   * @throws Exception Error during code generation
   */
  private JClass createWebSocketsServerClass() throws Exception
  {
    JClass serverClass = JClass.factory.create(JModifier.PUBLIC, this.interfaceName);
    serverClass.setExtends("WebSocketHandlerAdapter");
    serverClass.addAnnotation(new JClassAnnotationImpl(String.format(
            "WebSocketHandlerService(path = \"/%s\")", this.channelName)));
    serverClass.setComment(new JClassCommentImpl(String.format("The '%s' class.", this.interfaceName)));

    LOGGER.debug(String.format("Created '%s' class for WebSockets server.", this.interfaceName));

    // Add fields to class
    for (JField field: this.createFields())
    {
      serverClass.add(field);
    }

    // Add methods to class
    serverClass.add(this.createConstructor());
    serverClass.add(this.createOnOpenMethod());
    serverClass.add(this.createOnCloseMethod());
    serverClass.add(this.createOnTextMessage());
    serverClass.add(this.createBuildResponse());
    serverClass.add(this.createSendMessage());
    serverClass.add(this.createBroadcastMessage());

    return serverClass;
  }

  /**
   * Create all fields of the class and return them as an
   * ArrayList. The field list contains both constants and
   * member variables.
   *
   * @return List of JField objects for class
   *
   * @throws Exception Error during code generation
   */
  private ArrayList<JField> createFields() throws Exception
  {
    ArrayList<JField> fields = new ArrayList<JField>();

    // Create LOGGER constant
    JField logger = JField.factory.create(JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL,
            "Logger", "LOGGER", String.format("LoggerFactory.getLogger(%s.class)", this.interfaceName));
    logger.setComment(new JFieldCommentImpl("Logger object."));
    fields.add(logger);

    // Create DELIMITER constant
    JField delimiter = JField.factory.create(JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL,
            "char", "DELIMITER", "'$'");
    delimiter.setComment(new JFieldCommentImpl("Delimiting character for RCP protocol."));
    fields.add(delimiter);

    // Create ServiceProvider object
    JField serviceProvider = JField.factory.create(JModifier.PRIVATE, this.serviceProviderClassName, "serviceProvider");
    serviceProvider.setComment(new JFieldCommentImpl("Service provider object."));
    fields.add(serviceProvider);

    return fields;
  }

  /**
   * Create constructor for WebSockets server class.
   *
   * @return JConstructor object for server class
   *
   * @throws Exception Error during code generation
   */
  private JConstructor createConstructor() throws Exception
  {
    JConstructor constructor = JConstructor.factory.create(JModifier.PUBLIC, this.interfaceName);
    constructor.setComment(new JConstructorCommentImpl(String.format("Create new '%s' object.", this.interfaceName)));

    // Set method body
    String methodBody = String.format(
            "LOGGER.info(\"WebSockets server created.\");\n\n" +
            "this.serviceProvider = new %s();",
            this.serviceProviderClassName);
    constructor.getBody().setSource(methodBody);

    return constructor;
  }

  /**
   * Create onOpen() method for WebSockets server class.
   * This callback is executed, when a new connection is
   * opened.
   *
   * @return JMethod object for onOpen() callback
   *
   * @throws Exception Error during code generation
   */
  private JMethod createOnOpenMethod() throws Exception
  {
    JParameter webSocket = JParameter.factory.create("WebSocket", "webSocket");
    JMethodSignature jms = JMethodSignature.factory.create(webSocket);

    JMethod onOpen = JMethod.factory.create(JModifier.PUBLIC, "void", "onOpen", jms);
    onOpen.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onOpen.setComment(new JMethodCommentImpl("Callback for newly opened connection."));

    // Set method body
    String methodBody = String.format(
            "LOGGER.info(\"WebSocket opened.\");\n\n" +

            "// Subscribe current WebSocket to channel '/%s'\n" +
            "webSocket.resource().setBroadcaster(BroadcasterFactory.getDefault().lookup(\"/%s\", true));\n\n" +

            "// Send message\n" +
            "%s.send(webSocket, \"Server opened connection.\");",
            this.channelName, this.channelName, this.interfaceName);
    onOpen.getBody().setSource(methodBody);

    return onOpen;
  }

  /**
   * Create onClose() method for WebSockets server class.
   * This callback is executed, when an open connection
   * is closed.
   *
   * @return JMethod object for onClose() callback
   *
   * @throws Exception Error during code generation
   */
  private JMethod createOnCloseMethod() throws Exception
  {
    JParameter webSocket = JParameter.factory.create("WebSocket", "webSocket");
    JMethodSignature jms = JMethodSignature.factory.create(webSocket);

    JMethod onClose = JMethod.factory.create(JModifier.PUBLIC, "void", "onClode", jms);
    onClose.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onClose.setComment(new JMethodCommentImpl("Callback for recently closed connection."));

    // Set method body
    String methodBody =
            "LOGGER.info(\"WebSocket closed.\");\n\n" +

            "// Remove WebSocket from broadcasting channel\n" +
            "BroadcastFactory.getDefault().removeAllAtmosphereResource(webSocket.resource());";
    onClose.getBody().setSource(methodBody);
    
    return onClose;
  }

  /**
   * Create onTextMessage() method for WebSockets server
   * class. This callback is executed, when a new text
   * message is received.
   *
   * @return JMethod object for onTextMessage() callback
   *
   * @throws Exception Error during code generation
   */
  private JMethod createOnTextMessage() throws Exception
  {
    JParameter webSocket = JParameter.factory.create("WebSocket", "webSocket");
    JParameter message = JParameter.factory.create("String", "message");
    JMethodSignature jms = JMethodSignature.factory.create(webSocket, message);

    JMethod onTextMessage = JMethod.factory.create(JModifier.PUBLIC, "void", "onTextMessage", jms);
    onTextMessage.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onTextMessage.setComment(new JMethodCommentImpl("Callback for received message."));

    // Set method body
    String methodBody = String.format(
// TODO: Block begin
            "LOGGER.info(\"Received message: \" + message);\n\n" +

            "String data[] = message.split(Pattern.quote(String.valueOf(%s.DELIMITER)));\n" +
            "if (data.length != 3) {\n" +
            "\tString responseMessage = \"Invalid request structure. Use 'uuid$method$payload'. Part count was: \" + data.length;\n\n" +

            "\tLOGGER.info(responseMessage);\n" +
            "\t%s.sendMessage(webSocket, responseMessage);\n" +
            "}\n" +
            "else {\n" +
            "\tString uuid = data[0];\n" +
            "\tString method = data[1];\n" +
            "\tString payload = data[2];\n\n" +

            "\tLOGGER.info(String.format(\"Method '%%s' was called. UUID is '%%s'. Payload is '%%s'.\", method, uuid, payload));\n\n" +

            "\t// Dispatch request\n" +
            "try {\n" +
            // TODO: Create code to dispatch requests
            "}\n" +
            "catch (Exception e) {\n" +
            "\tSystem.out.println(\"Could not dispatch request: \" + e.getMessage());\n" +
            "}",
            this.interfaceName, this.interfaceName);
// TODO: Block end
    onTextMessage.getBody().setSource(methodBody);

    return onTextMessage;
  }

  /**
   * Create buildResponseMessage() method for WebSockets
   * server class. The generated method can be used to
   * build response messages for our own RPC protocol.
   * Basically, it will append the UUID, operation name
   * and payload, using a predefined delimiter.
   *
   * @return JMethod object to create response messages
   *
   * @throws Exception Error during code generation
   */
  private JMethod createBuildResponse() throws Exception
  {
    JParameter uuid = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter method = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JParameter payload = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    JMethodSignature jms = JMethodSignature.factory.create(uuid, method, payload);

    JMethod buildResponse = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC,
            "String", "buildResponseMessage", jms);
    buildResponse.setComment(new JMethodCommentImpl("Build a response message according to RPC protocol."));

    // Set method body
    String methodBody = String.format(
            "return uuid + %s.DELIMITER + method + %s.DELIMITER + payload;",
            this.interfaceName, this.interfaceName);
    buildResponse.getBody().setSource(methodBody);

    return buildResponse;
  }

  /**
   * Create sendMessage() method for WebSockets server class.
   * The generated method can be used to send a text message
   * to a single, dedicated client.
   *
   * @return JMethod object to unicast text messages
   *
   * @throws Exception Error during code generation
   */
  private JMethod createSendMessage() throws Exception
  {
    JParameter webSocket = JParameter.factory.create("WebSocket", "webSocket");
    JParameter message = JParameter.factory.create("String", "message");
    JMethodSignature jms = JMethodSignature.factory.create(webSocket, message);

    JMethod sendMessage = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC,
            "void", "sendMessage", jms);
    sendMessage.setComment(new JMethodCommentImpl("Send message to a single client."));

    // Set method body
    String methodBody =
            "try {\n" +
            "\twebSocket.write(message);\n" +
            "}\n" +
            "catch (Exception e) {\n" +
            "\tLOGGER.error(\"Error: \" + e.getMessage());\n" +
            "}";
    sendMessage.getBody().setSource(methodBody);

    return sendMessage;
  }

  /**
   * Create broadcastMessage() method for WebSockets server class.
   * The generated method can be used to send a text message to
   * multiple clients that are subscribed to a certain channel.
   *
   * @return JMethod object to broadcast text messages
   *
   * @throws Exception Error during code generation
   */
  private JMethod createBroadcastMessage() throws Exception
  {
    JParameter targetChannel = JParameter.factory.create(JModifier.FINAL, "String", "targetChannel");
    JParameter message = JParameter.factory.create(JModifier.FINAL, "String", "message");
    JMethodSignature jms = JMethodSignature.factory.create(targetChannel, message);

    JMethod broadcastMessage = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC,
            "void", "broadcastMessage", jms);
    broadcastMessage.setComment(new JMethodCommentImpl(
            "Broadcast message to all clients that are subscribed to a certain channel."));

    // Set method body
    String methodBody =
            "MetaBroadcaster.getDefault().broadcastTo(targetChannel, message);";
    broadcastMessage.getBody().setSource(methodBody);

    return broadcastMessage;
  }

  /**
   * Private helper method to add required Java imports to the
   * source file of the WebSockets server. The method will only
   * import a class, if it does not reside in the same package
   * as the WebSockets server class. Furthermore, the method
   * checks for duplicate imports automatically. It will only
   * import a class, if it is not present in the source file's
   * imports already.
   *
   * Multiple calls to this function with the same arguments will
   * result in a single import statement on source code write-out.
   *
   * @param requiredImport Name of required import
   */
  private void addRequiredImport(final JSourceFile sourceFile, final String requiredImport)
  {
    if (null != requiredImport)
    {
      // Extract package name from fully qualified class name
      String importPackageName = requiredImport.substring(0, requiredImport.lastIndexOf("."));

      // Do not import from same package!
      if (!this.packageName.equals(importPackageName))
      {
        // No duplicate imports!
        if (!sourceFile.containsImport(requiredImport))
        {
          try
          {
            sourceFile.addImport(requiredImport);
          }
          catch (JDuplicateException e)
          {
            // Exception ignored intentionally
          }
        }
      }
    }
  }

  /**
   * Private helper method to capitalize the first letter of a string.
   * Function will return null, if argument was null.
   *
   * @param text Text to process
   *
   * @return Text with first letter capitalized or null
   */
  private static String firstLetterCapital(final String text)
  {
    return (null == text ? null : text.substring(0, 1).toUpperCase() + text.substring(1, text.length()));
  }
}
