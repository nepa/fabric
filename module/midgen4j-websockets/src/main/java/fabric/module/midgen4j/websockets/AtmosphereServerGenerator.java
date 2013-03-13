/** 13.03.2013 01:02 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  /** Name of inner class for RPC protocol messages */
  public static final String MESSAGE_CLASS_NAME = "Message";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Name of WebSockets interface class */
  private String interfaceName;

  /** Full name of inner class for RPC protocol messages */
  private String messageClassFullName;

  /** Java package name for WebSocket interface classes */
  private String packageName;

  /** Name of broadcast channel */
  private String channelName;

  /** Java package name of service provider class */
  private String serviceProviderPackageName;

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

    // Extract global properties
    this.interfaceName = this.properties.getProperty(MidGen4JWebSocketsModule.INTERFACE_CLASS_NAME_KEY);
    this.messageClassFullName = this.interfaceName + "." + AtmosphereServerGenerator.MESSAGE_CLASS_NAME;
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
    this.channelName = this.properties.getProperty(MidGen4JWebSocketsModule.CHANNEL_NAME_KEY);
    this.serviceProviderPackageName = this.properties.getProperty(MidGen4JWebSocketsModule.SERVICE_PROVIDER_PACKAGE_NAME_KEY);
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

    this.addRequiredImport(jsf, this.serviceProviderPackageName + "." + this.serviceProviderClassName);
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
    serverClass.add(this.createSendMessage());
    serverClass.add(this.createBroadcastMessage());

    // Add inner class for request messages
    serverClass.add(this.createMessageInnerClass());

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
            "%s.sendMessage(webSocket, \"Server opened connection.\");",
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

    JMethod onClose = JMethod.factory.create(JModifier.PUBLIC, "void", "onClose", jms);
    onClose.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onClose.setComment(new JMethodCommentImpl("Callback for recently closed connection."));

    // Set method body
    String methodBody =
            "LOGGER.info(\"WebSocket closed.\");\n\n" +

            "// Remove WebSocket from broadcasting channel\n" +
            "BroadcasterFactory.getDefault().removeAllAtmosphereResource(webSocket.resource());";
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
            "LOGGER.info(\"Received message: \" + message);\n\n" +

            "try {\n" +
            "\t// Parse message from client\n" +
            "\t%s request = new %s(message);\n" +
            "\tLOGGER.info(String.format(\"Method '%%s' was called. UUID is '%%s'. Payload is '%%s'.\", " +
              "request.method(), request.uuid(), request.payload()));\n\n" +

            "\ttry {\n" +
            "\t\t// Dispatch request\n" +
            "\t\t%s.dispatch(this.serviceProvider, webSocket, request);\n" +
            "\t}\n" +
            "\t// Could not dispatch request (e.g. unknown RPC method)\n" +
            "\tcatch (Exception e) {\n" +
            "\t\tLOGGER.error(\"Could not dispatch request: \" + e.getMessage());\n" +
            "\t}\n" +
            "}\n" +
            "// Could not parse message (e.g. illegal message format)\n" +
            "catch (Exception e) {\n" +
            "\tString errorMessage = e.getMessage();\n\n" +

            "\tLOGGER.error(errorMessage);\n" +
            "\t%s.sendMessage(webSocket, errorMessage);\n" +
            "}",
            this.messageClassFullName, this.messageClassFullName,
            DispatcherGenerator.DISPATCHER_CLASS_NAME, this.interfaceName);
    onTextMessage.getBody().setSource(methodBody);

    return onTextMessage;
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
   * Create inner message class, that contains all data that is
   * required for the RPC protocol placed on top of WebSockets.
   * That is a unique ID, the name of the RPC method that was
   * called and a JSON string with additional payload for the
   * method call.
   *
   * @return JClass object with inner message class
   *
   * @throws Exception Error during code generation
   */
  private JClass createMessageInnerClass() throws Exception
  {
    // Create inner message class
    JClass messageClass = JClass.factory.create(JModifier.PUBLIC | JModifier.STATIC | JModifier.FINAL, MESSAGE_CLASS_NAME);
    messageClass.setComment(new JClassCommentImpl(String.format("Inner '%s' class.", MESSAGE_CLASS_NAME)));

    LOGGER.debug(String.format("Created '%s' class as inner class of '%s'.", MESSAGE_CLASS_NAME, this.interfaceName));

    /*****************************************************************
     * Create fields
     *****************************************************************/

    JField delimiterField = JField.factory.create(JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL, "char", "DELIMITER", "'$'");
    delimiterField.setComment(new JFieldCommentImpl("Delimiting character for RPC protocol."));
    messageClass.add(delimiterField);

    JField uuidField = JField.factory.create(JModifier.PRIVATE, "String", "uuid");
    uuidField.setComment(new JFieldCommentImpl("Unique ID for protocol messages."));
    messageClass.add(uuidField);

    JField methodField = JField.factory.create(JModifier.PRIVATE, "String", "method");
    methodField.setComment(new JFieldCommentImpl("RPC method called in protocol message."));
    messageClass.add(methodField);

    JField payloadField = JField.factory.create(JModifier.PRIVATE, "String", "payload");
    payloadField.setComment(new JFieldCommentImpl("JSON Code as payload for protocol message."));
    messageClass.add(payloadField);

    /*****************************************************************
     * Create parameterless constructor
     *****************************************************************/

    JConstructor constructorParameterless = JConstructor.factory.create(JModifier.PUBLIC, MESSAGE_CLASS_NAME);
    constructorParameterless.setComment(new JConstructorCommentImpl("Parameterless constructor."));

    // Set method body
    String methodBody =
            "this(\"\", \"\", \"\");";
    constructorParameterless.getBody().setSource(methodBody);

    // Add constructor to class
    messageClass.add(constructorParameterless);

    /*****************************************************************
     * Create constructor with message attribute
     *****************************************************************/

    JParameter message = JParameter.factory.create(JModifier.FINAL, "String", "message");
    JMethodSignature jms = JMethodSignature.factory.create(message);

    // TODO: Add exception support to JConstructor class, so that we can do:
    //   JConstructor constructorMessage = JConstructor.factory.create(JModifier.PUBLIC, this.interfaceName, jms, new String[] { "Exception" });
    JConstructor constructorMessage = JConstructor.factory.create(JModifier.PUBLIC, MESSAGE_CLASS_NAME, jms);
    constructorMessage.setComment(new JConstructorCommentImpl("Parameterized constructor with message attribute."));

    // Set method body
    methodBody =
            "this.fromString(message);";
    constructorMessage.getBody().setSource(methodBody);

    // Add constructor to class
    messageClass.add(constructorMessage);

    /*****************************************************************
     * Create constructor with UUID, method and payload attribute
     *****************************************************************/

    JParameter uuid = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter method = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JParameter payload = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    jms = JMethodSignature.factory.create(uuid, method, payload);

    JConstructor constructorMessageParts = JConstructor.factory.create(JModifier.PUBLIC, MESSAGE_CLASS_NAME, jms);
    constructorMessageParts.setComment(new JConstructorCommentImpl("Parameterized constructor with UUID, method and payload attribute."));

    // Set method body
    methodBody =
            "this.uuid = uuid;\n" +
            "this.method = method;\n" +
            "this.payload = payload;";
    constructorMessageParts.getBody().setSource(methodBody);

    // Add constructor to class
    messageClass.add(constructorMessageParts);

    /*****************************************************************
     * Create method to populate message object from string
     *****************************************************************/

    jms = JMethodSignature.factory.create(message);

    JMethod fromString = JMethod.factory.create(JModifier.PUBLIC, "void", "fromString", jms, new String[] { "Exception" });
    fromString.setComment(new JMethodCommentImpl(String.format("Populate '%s' object from string.", MESSAGE_CLASS_NAME)));

    // Set method body
    methodBody =
            "String[] data = message.split(Pattern.quote(String.valueOf(DELIMITER)));\n\n" +

            "if (data.length == 3) {\n" +
            "\tthis.uuid = data[0];\n" +
            "\tthis.method = data[1];\n" +
            "\tthis.payload = data[2];\n" +
            "}\n" +
            "else {\n" +
            "\tthrow new Exception(\"Invalid message structure. Use 'uuid$method$payload'. " +
              "Your message had '\" + data.length + \"' parts.\");\n" +
            "}";
    fromString.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(fromString);

    /*****************************************************************
     * Create method to print message object as string
     *****************************************************************/

    JMethod asString = JMethod.factory.create(JModifier.PUBLIC, "String", "asString");
    asString.setComment(new JMethodCommentImpl(String.format("Print '%s' object as a string.", MESSAGE_CLASS_NAME)));

    // Set method body
    methodBody =
            "return this.uuid + DELIMITER + this.method + DELIMITER + this.payload;";
    asString.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(asString);

    /*****************************************************************
     * Create setter and getter for UUID
     *****************************************************************/

    jms = JMethodSignature.factory.create(uuid);

    JMethod setUUID = JMethod.factory.create(JModifier.PUBLIC, "void", "setUUID", jms);
    setUUID.setComment(new JMethodCommentImpl("Set UUID for protocol message."));

    // Set method body
    methodBody =
            "this.uuid = uuid;";
    setUUID.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(setUUID);

    JMethod getUUID = JMethod.factory.create(JModifier.PUBLIC, "String", "uuid");
    getUUID.setComment(new JMethodCommentImpl("Get UUID of protocol message."));

    // Set method body
    methodBody =
            "return this.uuid;";
    getUUID.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(getUUID);

    /*****************************************************************
     * Create setter and getter for RPC method
     *****************************************************************/

    jms = JMethodSignature.factory.create(method);

    JMethod setMethod = JMethod.factory.create(JModifier.PUBLIC, "void", "setMethod", jms);
    setMethod.setComment(new JMethodCommentImpl("Set RPC method called in protocol message."));

    // Set method body
    methodBody =
            "this.method = method;";
    setMethod.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(setMethod);

    JMethod getMethod = JMethod.factory.create(JModifier.PUBLIC, "String", "method");
    getMethod.setComment(new JMethodCommentImpl("Get RPC method called in protocol message."));

    // Set method body
    methodBody =
            "return this.method;";
    getMethod.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(getMethod);

    /*****************************************************************
     * Create setter and getter for payload
     *****************************************************************/

    jms = JMethodSignature.factory.create(payload);

    JMethod setPayload = JMethod.factory.create(JModifier.PUBLIC, "void", "setPayload", jms);
    setPayload.setComment(new JMethodCommentImpl("Set payload for protocol message."));

    // Set method body
    methodBody =
            "this.payload = payload";
    setPayload.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(setPayload);

    JMethod getPayload = JMethod.factory.create(JModifier.PUBLIC, "String", "payload");
    getPayload.setComment(new JMethodCommentImpl("Get payload of protocol message."));

    // Set method body
    methodBody =
            "return this.payload;";
    getPayload.getBody().setSource(methodBody);

    // Add method to class
    messageClass.add(getPayload);

    return messageClass;
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
}
