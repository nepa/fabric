/** 08.03.2013 23:44 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
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
 * TODO: Implement class and add comment
 *
 * @author seidel
 */
public class AtmosphereServerGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(AtmosphereServerGenerator.class); // TODO: Use logger in class

  // TODO: Add comment
  public static final String SERVER_CLASS_NAME = "Server"; // TODO: Use MidGen4JWebSocketsModule.INTERFACE_CLASS_NAME_KEY instead?

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
    serverClass.setExtends("WebSocketHandlerAdapter");
    serverClass.addAnnotation(new JClassAnnotationImpl(String.format(
            "WebSocketHandlerService(path = \"%s\"})", "echo"))); // TODO: Set channel name dynamically
    serverClass.setComment(new JClassCommentImpl(String.format(
            "The '%s' class.", SERVER_CLASS_NAME)));
    jsf.add(serverClass);

    // Add LOGGER constant
    JField logger = JField.factory.create(
            JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL,
            "Logger",
            "LOGGER",
            String.format("LoggerFactory.getLogger(%s.class)", SERVER_CLASS_NAME));
    logger.setComment(new JFieldCommentImpl("Logger object."));
    serverClass.add(logger);

    // Add DELIMITER constant
    JField delimiter = JField.factory.create(
            JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL,
            "char",
            "DELIMITER",
            "'$'");
    delimiter.setComment(new JFieldCommentImpl("Demiliter char for RCP protocol."));
    serverClass.add(delimiter);

    // Add ServiceProvider object
    JField serviceProvider = JField.factory.create(
            JModifier.PRIVATE,
            "ServiceProvider", // TODO: Set class name dynamically
            "serviceProvider");
    serviceProvider.setComment(new JFieldCommentImpl("Service provider object."));
    serverClass.add(serviceProvider);

    // Add constructor
    JConstructor constructor = JConstructor.factory.create(JModifier.PUBLIC, SERVER_CLASS_NAME);
    constructor.setComment(new JConstructorCommentImpl(
            String.format("Create new '%s' object.", SERVER_CLASS_NAME)));

    // Set method body
    String methodBody = String.format(
            "LOGGER.info(\"WebSockets server created.\");\n\n" +
            "this.serviceProvider = new %s();",
            "ServiceProvider"); // TODO: Set class name dynamically
    constructor.getBody().setSource(methodBody);

    serverClass.add(constructor);

    // Add onOpen() method
    JParameter webSocket = JParameter.factory.create("WebSocket", "webSocket");
    JMethodSignature jms = JMethodSignature.factory.create(webSocket);

    JMethod onOpen = JMethod.factory.create(JModifier.PUBLIC, "void", "onOpen", jms);
    onOpen.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onOpen.setComment(new JMethodCommentImpl("Callback for newly opened connection."));

    // Set method body
    methodBody = String.format(
            "LOGGER.info(\"WebSocket opened.\");\n\n" +

            "// Subscribe current WebSocket to channel '%s'\n" +
            "webSocket.resource().setBroadcaster(BroadcasterFactory.getDefault().lookup(\"%s\", true));\n\n" +

            "// Send message\n" +
            "%s.broadcastMessage(\"%s\", \"Server opened connection.\"); // TODO: Do NOT broadcast to ALL clients!",
            "/echo", "/echo", SERVER_CLASS_NAME, "/echo"); // TODO: Set channel names dynamically
    onOpen.getBody().setSource(methodBody);

    serverClass.add(onOpen);

    // Add onClose() method
    JMethod onClose = JMethod.factory.create(JModifier.PUBLIC, "void", "onClode", jms);
    onClose.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onClose.setComment(new JMethodCommentImpl("Callback for recently closed connection."));

    // Set method body
    methodBody =
            "LOGGER.info(\"WebSocket closed.\");\n\n" +

            "// Remove WebSocket from broadcasting channel\n" +
            "BroadcastFactory.getDefault().removeAllAtmosphereResource(webSocket.resource());";
    onClose.getBody().setSource(methodBody);

    serverClass.add(onClose);

    // Add onTextMessage() method
    JParameter message = JParameter.factory.create("String", "message");
    jms = JMethodSignature.factory.create(webSocket, message);

    JMethod onTextMessage = JMethod.factory.create(JModifier.PUBLIC, "void", "onTextMessage", jms);
    onTextMessage.addAnnotation(JMethodAnnotationImpl.OVERRIDE);
    onTextMessage.setComment(new JMethodCommentImpl("Callback for received message."));

    // Set method body
    methodBody =
            "// TODO"; // TODO: Create method body
    onTextMessage.getBody().setSource(methodBody);

    serverClass.add(onTextMessage);

    // Add buildResponseMessage() method
    JParameter uuid = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter method = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JParameter payload = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    jms = JMethodSignature.factory.create(uuid, method, payload);

    JMethod buildResponse = JMethod.factory.create(
            JModifier.PUBLIC | JModifier.STATIC,
            "String", "buildResponseMessage", jms);
    buildResponse.setComment(new JMethodCommentImpl(
            "Build a response message according to RPC protocol."));

    // Set method body
    methodBody = String.format(
            "return uuid + %s.DELIMITER + method + %s.DELIMITER + payload;",
            SERVER_CLASS_NAME, SERVER_CLASS_NAME);
    buildResponse.getBody().setSource(methodBody);

    serverClass.add(buildResponse);

    // Add sendMessage() method
    jms = JMethodSignature.factory.create(webSocket, message);

    JMethod sendMessage = JMethod.factory.create(
            JModifier.PUBLIC | JModifier.STATIC,
            "void", "sendMessage", jms);
    sendMessage.setComment(new JMethodCommentImpl("Send message to a single client,"));

    // Set method body
    methodBody =
            "try {\n" +
            "\twebSocket.write(message);\n" +
            "}\n" +
            "catch (Exception e) {\n" +
            "\tLOGGER.error(\"Error: \" + e.getMessage());\n" +
            "}";
    sendMessage.getBody().setSource(methodBody);

    serverClass.add(sendMessage);

    // Add broadcastMessage() method
    JParameter targetChannel = JParameter.factory.create(JModifier.FINAL, "String", "targetChannel");
    message = JParameter.factory.create(JModifier.FINAL, "String", "message");
    jms = JMethodSignature.factory.create(targetChannel, message);

    JMethod broadcastMessage = JMethod.factory.create(
            JModifier.PUBLIC | JModifier.STATIC,
            "void", "broadcastMessage", jms);
    broadcastMessage.setComment(new JMethodCommentImpl(
            "Broadcast message to all clients that are subscribed to a certain channel."));

    // Set method body
    methodBody =
            "MetaBroadcaster.getDefault().broadcastTo(targetChannel, message);";
    broadcastMessage.getBody().setSource(methodBody);

    serverClass.add(broadcastMessage);
    // TODO: Block end
  }
}
