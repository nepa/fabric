/** 13.03.2013 00:18 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JConstructor;
import de.uniluebeck.sourcegen.java.JConstructorCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
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
 * a dispatcher class for request messages.
 *
 * Each time a new request is issued, the dispatcher will check
 * its content and start a new worker thread that belongs to the
 * RPC method which was called.
 *
 * @author seidel
 */
public class DispatcherGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherGenerator.class);

  /** Name of request dispatcher class */
  public static final String DISPATCHER_CLASS_NAME = "RequestDispatcher";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Names of service operations from WSDL file */
  private Set<String> operationNames;

  /** Name of WebSockets interface class */
  private String interfaceName;

  /** Full name of inner class for RPC protocol messages */
  private String messageClassFullName;

  /** Java package name for WebSockets interface class */
  private String packageName;

  /** Name of the service provider class */
  private String serviceProviderClassName;

  /**
   * Constructor initializes the DispatcherGenerator, which
   * can create a class to dispatch request messages.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public DispatcherGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    this.operationNames = new HashSet<String>();

    // Extract global properties
    this.interfaceName = this.properties.getProperty(MidGen4JWebSocketsModule.INTERFACE_CLASS_NAME_KEY);
    this.messageClassFullName = this.interfaceName + "." + AtmosphereServerGenerator.MESSAGE_CLASS_NAME;
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
    this.serviceProviderClassName = this.properties.getProperty(MidGen4JWebSocketsModule.SERVICE_PROVIDER_CLASS_NAME_KEY);
  }

  /**
   * Create a Java class that contains the request dispatcher,
   * before processing any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    this.createDispatcherFile();
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
   * Create a source file that contains the request dispatcher.
   * The method will generate the corresponding Java class and
   * add all necessary Java imports to the file.
   *
   * @throws Exception Error during code generation
   */
  private void createDispatcherFile() throws Exception
  {
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, DISPATCHER_CLASS_NAME);

    // Add request dispatcher class
    jsf.add(this.createDispatcherClass());

    // Add required imports
    jsf.addImport("org.atmosphere.websocket.WebSocket");

    // Add imports for worker thread classes
    for (String operationName: this.operationNames)
    {
      String workerThreadClassName = DispatcherGenerator.firstLetterCapital(operationName) + "Worker";

      // TODO: Do proper duplicate and package checking?
      jsf.addImport(String.format("%s.%s.%s", this.packageName, WorkerThreadGenerator.WORKER_THREAD_PACKAGE_SEGMENT, workerThreadClassName));
    }
  }

  /**
   * Create Java class for the request dispatcher. The generated class
   * will only contain a private default constructor, as well as a
   * static method to dispatch incoming request messages.
   *
   * @return JClass object with request dispatcher class
   *
   * @throws Exception Error during code generation
   */
  private JClass createDispatcherClass() throws Exception
  {
    // Create request dispatcher class
    JClass dispatcherClass = JClass.factory.create(JModifier.PUBLIC, DISPATCHER_CLASS_NAME);
    dispatcherClass.setComment(new JClassCommentImpl("Request dispatcher class."));

    LOGGER.debug(String.format("Created '%s' class to handle incoming requests.", DISPATCHER_CLASS_NAME));

    // Create constructor
    JConstructor constructor = JConstructor.factory.create(JModifier.PRIVATE, DISPATCHER_CLASS_NAME);
    constructor.setComment(new JConstructorCommentImpl("Private constructor due to static methods."));

    // Set method body
    String methodBody =
            "// Empty implementation";
    constructor.getBody().setSource(methodBody);

    // Add constructor to class
    dispatcherClass.add(constructor);

    // Add dispatch() method
    dispatcherClass.add(this.createDispatchMethod());

    return dispatcherClass;
  }

  /**
   * Create dispatch() method for request dispatcher class. This
   * method is called for each incoming message and will start a
   * worker thread that belongs to the RPC method that was issued.
   *
   * @return JMethod object with dispatch() method
   *
   * @throws Exception Error during code generation
   */
  private JMethod createDispatchMethod() throws Exception
  {
    JParameter serviceProvider = JParameter.factory.create(JModifier.FINAL, this.serviceProviderClassName, "serviceProvider");
    JParameter webSocket = JParameter.factory.create(JModifier.FINAL, "WebSocket", "webSocket");
    JParameter requestMessage = JParameter.factory.create(JModifier.FINAL, this.messageClassFullName, "requestData");
    JMethodSignature jms = JMethodSignature.factory.create(serviceProvider, webSocket, requestMessage);

    JMethod dispatch = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC, "void", "dispatch", jms, new String[] { "Exception" });
    dispatch.setComment(new JMethodCommentImpl("Dispatch request message to worker thread class."));

    // Set method body
    String methodBody =
            "String uuid = requestData.uuid();\n" +
            "String method = requestData.method();\n" +
            "String payload = requestData.payload();";

    if (null != this.operationNames)
    {
      // Iterate all service operations
      int counter = 0;
      for (String operationName: this.operationNames)
      {
        String methodName = operationName; // TODO: firstLetterLowerCase?
        String workerThreadClassName = DispatcherGenerator.firstLetterCapital(operationName) + "Worker";

        // Create string with spaces for proper indention
        String spaces = "";
        for (int i = 0; i < workerThreadClassName.length(); ++i)
        {
          spaces += " ";
        }

        // First operation
        if (counter == 0)
        {
          methodBody += String.format(
                  "\n\n" +
                  "if (method.equals(\"%s\")) {\n" +
                  "\t%s.BUILDER.executeMethod(method)\n" +
                  "\t%s        .withData(uuid, payload)\n" +
                  "\t%s        .onProvider(serviceProvider)\n" +
                  "\t%s        .viaConnection(webSocket)\n" +
                  "\t%s        .build()\n" +
                  "\t%s        .start();\n" +
                  "}\n",
                  methodName, workerThreadClassName,
                  spaces, spaces, spaces, spaces, spaces);
        }
        // Subsequent operations
        else
        {
          methodBody += String.format(
                  "else if (method.equals(\"%s\")) {\n" +
                  "\t%s.BUILDER.executeMethod(method)\n" +
                  "\t%s        .withData(uuid, payload)\n" +
                  "\t%s        .onProvider(serviceProvider)\n" +
                  "\t%s        .viaConnection(webSocket)\n" +
                  "\t%s        .build()\n" +
                  "\t%s        .start();\n" +
                  "}\n",
                  methodName, workerThreadClassName,
                  spaces, spaces, spaces, spaces, spaces);
        }

        counter++;
      }

      methodBody += String.format(
              "else {\n" +
              "\t// TODO: Throw exception here?\n" +
              "\t%s.sendMessage(webSocket, String.format(\"Method '%%s' is unknown.\", method));",
              this.interfaceName);
    }

    dispatch.getBody().setSource(methodBody);

    return dispatch;
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
