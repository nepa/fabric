/** 06.03.2013 21:51 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
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
 * worker thread classes for all RPC methods defined in the WSDL file.
 *
 * Each time a new request is issued, the WebSockets service interface
 * will create a new worker thread, in order to allow handling of
 * concurrent requests.
 *
 * @author seidel
 */
public class WorkerThreadGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThreadGenerator.class);

  /** Name of the base worker thread class */
  public static final String WORKER_THREAD_CLASS_NAME = "WorkerThread";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Java package name for WebSockets interface class */
  private String packageName;

  /** Java package name for thread worker classes */
  private String threadWorkerPackageName;

  /** Java package name of service provider class */
  private String serviceProviderPackageName;

  /** Name of the service provider class */
  private String serviceProviderClassName;

  /**
   * Constructor initializes the WorkerThreadGenerator, which
   * can create worker thread classes for all RPC methods that
   * are defined in the WSDL document.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public WorkerThreadGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    // Extract global properties
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
    this.threadWorkerPackageName = this.packageName + ".threads";
    this.serviceProviderPackageName = this.properties.getProperty(MidGen4JWebSocketsModule.SERVICE_PROVIDER_PACKAGE_NAME_KEY);
    this.serviceProviderClassName = this.properties.getProperty(MidGen4JWebSocketsModule.SERVICE_PROVIDER_CLASS_NAME_KEY);
  }

  /**
   * Create file that contains the base class of all worker threads,
   * before processing any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeBeforeProcessing() throws Exception
  {
    this.createBaseWorkerThreadFile();
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
        // Create file that holds per-operation worker thread class
        this.createWorkerThreadFile(operation);
      }
    }
  }

  /**
   * Create a file that contains the base class of all worker thread
   * classes. This method will trigger the class generation and add
   * all required Java imports to the source file.
   *
   * @throws Exception Error during code generation
   */
  private void createBaseWorkerThreadFile() throws Exception
  {
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.threadWorkerPackageName, WORKER_THREAD_CLASS_NAME);

    // Add base worker thread class
    jsf.add(this.createBaseWorkerThreadClass());

    // Add required imports
    jsf.addImport("org.atmosphere.websocket.WebSocket");

    // Do not import from same package!
    if (!this.serviceProviderPackageName.equals(this.packageName))
    {
      // Import service provider class
      jsf.addImport(this.serviceProviderPackageName + "." + this.serviceProviderClassName);
    }
  }

  /**
   * Create a file that contains a single worker thread class. All
   * child worker classes are generated on a per-operation basis
   * for each and every RPC method defined in the WSDL document.
   *
   * This method will trigger the class generation and add all
   * required Java imports to the source file.
   *
   * @param operation FOperation object from WSDL parser
   *
   * @throws Exception Error during code generation
   */
  private void createWorkerThreadFile(final FOperation operation) throws Exception
  {
    String rpcMethodName = WorkerThreadGenerator.firstLetterCapital(operation.getOperationName());
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.threadWorkerPackageName, rpcMethodName + "Worker");

    // Add child worker thread class
    jsf.add(this.createWorkerThreadClass(operation));

    // Add required imports
    jsf.addImport("org.slf4j.Logger");
    jsf.addImport("org.slf4j.LoggerFactory");
    jsf.addImport("org.atmosphere.websocket.WebSocket");

    // Do not import from same package!
    if (!this.serviceProviderPackageName.equals(this.threadWorkerPackageName))
    {
      // Import service provider class
      jsf.addImport(this.serviceProviderPackageName + "." + this.serviceProviderClassName);
    }
  }

  /**
   * Create Java class for the base worker thread. The generated class
   * will simply contain some attributes, as well as a constructor to
   * initialize new worker thread objects. However, the class itself
   * is abstract and needs to be extended for later use.
   *
   * @return JClass object with base worker thread class
   *
   * @throws Exception Error during code generation
   */
  private JClass createBaseWorkerThreadClass() throws Exception
  {
    JClass workerThread = JClass.factory.create(JModifier.PUBLIC | JModifier.ABSTRACT, WORKER_THREAD_CLASS_NAME);
    workerThread.setExtends("Thread");
    workerThread.setComment(new JClassCommentImpl("Base class for all worker threads."));

    LOGGER.debug(String.format("Created '%s' class for base worker thread.", WORKER_THREAD_CLASS_NAME));

    // Add member variables
    workerThread.add(JField.factory.create(JModifier.PROTECTED, "String", "method"));
    workerThread.add(JField.factory.create(JModifier.PROTECTED, "String", "uuid"));
    workerThread.add(JField.factory.create(JModifier.PROTECTED, "String", "payload"));
    workerThread.add(JField.factory.create(JModifier.PROTECTED, this.serviceProviderClassName, "serviceProvider"));
    workerThread.add(JField.factory.create(JModifier.PROTECTED, "WebSocket", "webSocket"));

    // Create constructor
    JParameter method = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JParameter uuid = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter payload = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    JParameter serviceProvider = JParameter.factory.create(JModifier.FINAL, this.serviceProviderClassName, "serviceProvider");
    JParameter webSocket = JParameter.factory.create(JModifier.FINAL, "WebSocket", "webSocket");

    JMethodSignature jms = JMethodSignature.factory.create(method, uuid, payload, serviceProvider, webSocket);
    JConstructor constructor = JConstructor.factory.create(JModifier.PROTECTED, WORKER_THREAD_CLASS_NAME, jms);
    constructor.setComment(new JConstructorCommentImpl(String.format("Constructor to initialize a new '%s' object.", WORKER_THREAD_CLASS_NAME)));

    // Set method body
    String methodBody =
            "this.method = method;\n" +
            "this.uuid = uuid;\n" +
            "this.payload = payload;\n" +
            "this.serviceProvider = serviceProvider;\n" +
            "this.webSocket = webSocket;";
    constructor.getBody().setSource(methodBody);

    // Add constructor to class
    workerThread.add(constructor);

    return workerThread;
  }

  /**
   * Create Java class for a single child worker thread. The generated
   * class will be derived from the base worker thread class and only
   * contains a run() method to start the thread.
   *
   * Furthermore, the code includes an inner class that utilizes the
   * Singleton and Builder design pattern, to create new worker thread
   * objects at runtime.
   *
   * @param operation FOperation object from WSDL parser
   *
   * @return JClass object with child worker thread class
   *
   * @throws Exception Error during code generation
   */
  private JClass createWorkerThreadClass(final FOperation operation) throws Exception
  {
    String rpcMethodName = WorkerThreadGenerator.firstLetterCapital(operation.getOperationName());
    String workerClassName = rpcMethodName + "Worker";

    // Create worker thread class
    JClass workerClass = JClass.factory.create(JModifier.PUBLIC, workerClassName);
    workerClass.setExtends(WORKER_THREAD_CLASS_NAME);
    workerClass.setComment(new JClassCommentImpl(String.format("The '%s' class.", workerClassName)));

    LOGGER.debug(String.format("Created '%s' class for child worker thread.", workerClassName));

    /*****************************************************************
     * Add fields
     *****************************************************************/

    JField logger = JField.factory.create(JModifier.PRIVATE | JModifier.STATIC | JModifier.FINAL,
            "Logger", "LOGGER", String.format("LoggerFactory.getLogger(%s.class)", workerClassName));
    logger.setComment(new JFieldCommentImpl("Logger object."));
    workerClass.add(logger);

    String builderClassName = workerClassName + "Builder";
    JField builder = JField.factory.create(JModifier.PUBLIC | JModifier.STATIC | JModifier.FINAL,
            builderClassName, "BUILDER", String.format("%s.getInstance", builderClassName));
    builder.setComment(new JFieldCommentImpl(String.format("Builder instance to create new '%s' objects.", workerClassName)));
    workerClass.add(builder);

    /*****************************************************************
     * Add constructor
     *****************************************************************/

    JParameter method = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JParameter uuid = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter payload = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    JParameter serviceProvider = JParameter.factory.create(JModifier.FINAL, this.serviceProviderClassName, "serviceProvider");
    JParameter webSocket = JParameter.factory.create(JModifier.FINAL, "WebSocket", "webSocket");

    JMethodSignature jms = JMethodSignature.factory.create(method, uuid, payload, serviceProvider, webSocket);
    JConstructor constructor = JConstructor.factory.create(JModifier.PRIVATE, workerClassName, jms);
    constructor.setComment(new JConstructorCommentImpl(String.format("Constructor to create a new '%s' object.", workerClassName)));

    // Set method body
    String methodBody =
            "super(method, uuid, payload, serviceProvider, webSocket);";
    constructor.getBody().setSource(methodBody);

    // Add constructor to class
    workerClass.add(constructor);

    /*****************************************************************
     * Add run method
     *****************************************************************/

    JMethod run = JMethod.factory.create(JModifier.PUBLIC, "void", "run");
    run.addAnnotation(new JMethodAnnotationImpl("Override"));
    run.setComment(new JMethodCommentImpl("Run worker thread to handle request."));

    // TODO: Block begin

    // Set method body
    methodBody = "";

    // 1. Operation has input? -> Convert it from String to Bean
    if (null != operation.getInputMessage())
    {
      String inputMessageClassName = WorkerThreadGenerator.firstLetterCapital(
              operation.getInputMessage().getMessageAttribute().getLocalPart()) + "Message";

      methodBody += String.format(
              "// Unmarshal JSON code from request\n" +
              "%s requestBeanObject = (%s)%s.jsonToInstance(%s.class, this.payload);\n\n",
              inputMessageClassName, inputMessageClassName,
              JSONMarshallerGenerator.MARSHALLER_CLASS_NAME, inputMessageClassName);
    }

    String outputMessageClassName = "";
    if (null != operation.getOutputMessage())
    {
      outputMessageClassName = WorkerThreadGenerator.firstLetterCapital(
              operation.getOutputMessage().getMessageAttribute().getLocalPart()) + "Message";
    }

    // 2. Call operation
    methodBody += String.format(
            "LOGGER.info(\"Processing '%s()' request...\");\n" +
            "%sthis.serviceProvider.%s(%s);",
            WorkerThreadGenerator.firstLetterLowercase(rpcMethodName),
            (null != operation.getOutputMessage() ? String.format("%s responseBeanObject = ", outputMessageClassName) : ""), // Method has return value?
            operation.getOperationName(),
            (null != operation.getInputMessage() ? "requestBeanObject" : "")); // Method has input?

    // 3. Operation has output? -> Convert Bean to String and send response
    if (null != operation.getOutputMessage())
    {
      methodBody += String.format(
              "\n\n" +
              "String jsonResponse = %s.instanceToJSON(responseBeanObject);\n" +
              "String responseMessage = Server.buildResponseMessage(this.uuid, this.method, jsonResponse);\n\n" +
              "LOGGER.info(\"Responding to '%s()' request...\");\n" +
              "Server.sendMessage(this.webSocket, responseMessage);\n\n",
              JSONMarshallerGenerator.MARSHALLER_CLASS_NAME,
              WorkerThreadGenerator.firstLetterLowercase(rpcMethodName));
    }

    run.getBody().setSource(methodBody);

    // TODO: Block end

    // Add method to class
    workerClass.add(run);

    /*****************************************************************
     * Add inner builder class
     *****************************************************************/

    workerClass.add(this.createInnerBuilderClass(workerClassName));

    return workerClass;
  }

  /**
   * Create inner builder class, that is used within the child worker
   * thread classes to create new thread objects at runtime. The code
   * that is generated by this method, makes use of the Singleton and
   * the Builder design pattern.
   *
   * @param workerThreadClassName Name of outer worker thread class
   *
   * @return JClass object with inner builder class
   *
   * @throws Exception Error during code generation
   */
  private JClass createInnerBuilderClass(final String workerThreadClassName) throws Exception
  {
    String builderClassName = workerThreadClassName + "Builder";

    // Create inner builder class
    JClass builderClass = JClass.factory.create(JModifier.PUBLIC | JModifier.STATIC | JModifier.FINAL, builderClassName);
    builderClass.setComment(new JClassCommentImpl(String.format("Inner '%s' class.", builderClassName)));

    LOGGER.debug(String.format("Created '%s' class as inner class of '%s'.", builderClassName, workerThreadClassName));

    /*****************************************************************
     * Add fields
     *****************************************************************/

    JField instance = JField.factory.create(JModifier.PRIVATE | JModifier.STATIC, builderClassName, "instance");
    instance.setComment(new JFieldCommentImpl("Builder instance for Singleton pattern"));
    builderClass.add(instance);

    JField method = JField.factory.create(JModifier.PRIVATE, "String", "method");
    builderClass.add(method);

    JField uuid = JField.factory.create(JModifier.PRIVATE, "String", "uuid");
    builderClass.add(uuid);

    JField payload = JField.factory.create(JModifier.PRIVATE, "String", "payload");
    builderClass.add(payload);

    JField serviceProvider = JField.factory.create(JModifier.PRIVATE, this.serviceProviderClassName, "serviceProvider");
    builderClass.add(serviceProvider);

    JField webSocket = JField.factory.create(JModifier.PRIVATE, "WebSocket", "webSocket");
    builderClass.add(webSocket);

    /*****************************************************************
     * Create constructor
     *****************************************************************/

    JConstructor constructor = JConstructor.factory.create(JModifier.PRIVATE, builderClassName);
    constructor.setComment(new JConstructorCommentImpl("Private constructor for Singleton pattern."));

    // Set method body
    String methodBody =
            "// Empty implementation";
    constructor.getBody().setSource(methodBody);

    // Add constructor to class
    builderClass.add(constructor);

    /*****************************************************************
     * Create method to get instance
     *****************************************************************/

    JMethod getInstance = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC | JModifier.SYNCHRONIZED, builderClassName, "getInstance");
    getInstance.setComment(new JMethodCommentImpl("Create a new builder instance, if it does not yet exist, and return the object."));

    // Set method body
    methodBody = String.format(
            "if (null != %s.instance) {\n" +
            "\t %s.instance = new %s();\n" +
            "}\n\n" +
            "return %s.instance;",
            builderClassName, builderClassName, builderClassName,
            builderClassName);
    getInstance.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(getInstance);

    /*****************************************************************
     * Create method to set RPC method
     *****************************************************************/

    JParameter methodParameter = JParameter.factory.create(JModifier.FINAL, "String", "method");
    JMethodSignature jms = JMethodSignature.factory.create(methodParameter);
    JMethod executeMethod = JMethod.factory.create(JModifier.PUBLIC, builderClassName, "executeMethod", jms);
    executeMethod.setComment(new JMethodCommentImpl("Set RPC method to execute."));

    // Set method body
    methodBody =
            "this.method = method;\n\n" +
            "return this;";
    executeMethod.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(executeMethod);

    /*****************************************************************
     * Create method to set data of request
     *****************************************************************/

    JParameter uuidParameter = JParameter.factory.create(JModifier.FINAL, "String", "uuid");
    JParameter payloadParameter = JParameter.factory.create(JModifier.FINAL, "String", "payload");
    jms = JMethodSignature.factory.create(uuidParameter, payloadParameter);
    JMethod dataMethod = JMethod.factory.create(JModifier.PUBLIC, builderClassName, "withData", jms);
    dataMethod.setComment(new JMethodCommentImpl("Set data of request."));

    // Set method body
    methodBody =
            "this.uuid = uuid;\n" +
            "this.payload = payload;\n\n" +
            "return this;";
    dataMethod.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(dataMethod);

    /*****************************************************************
     * Create method to set service provider
     *****************************************************************/

    JParameter serviceProviderParameter = JParameter.factory.create(JModifier.FINAL, this.serviceProviderClassName, "serviceProvider");
    jms = JMethodSignature.factory.create(serviceProviderParameter);
    JMethod providerMethod = JMethod.factory.create(JModifier.PUBLIC, builderClassName, "onProvider", jms);
    providerMethod.setComment(new JMethodCommentImpl("Set service provider to use."));

    // Set method body
    methodBody =
            "this.serviceProvider = serviceProvider;\n\n" +
            "return this;";
    providerMethod.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(providerMethod);

    /*****************************************************************
     * Create method to set connection
     *****************************************************************/

    JParameter webSocketParameter = JParameter.factory.create(JModifier.FINAL, "WebSocket", "webSocket");
    jms = JMethodSignature.factory.create(webSocketParameter);
    JMethod connectionMethod = JMethod.factory.create(JModifier.PUBLIC, builderClassName, "viaConnection", jms);
    connectionMethod.setComment(new JMethodCommentImpl("Set connection for communication."));

    // Set method body
    methodBody =
            "this.webSocket = webSocket;\n\n" +
            "return this;";
    connectionMethod.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(connectionMethod);

    /*****************************************************************
     * Create method to build worker thread object
     *****************************************************************/

    JMethod buildMethod = JMethod.factory.create(JModifier.PUBLIC, workerThreadClassName, "build");
    buildMethod.addException("Exception");
    buildMethod.setComment(new JMethodCommentImpl("Build the worker thread object."));

    // Set method body
    methodBody = String.format(
            "if (null == this.method || null == this.uuid || null == this.payload || " +
            "null == this.serviceProvider || null == this.webSocket) {\n" +
            "\tthrow new Exception(\"Initialize '%s' object properly before building.\");\n" +
            "}\n\n" +
            "return new %s(this.method, this.uuid, this.payload, this.serviceProvider, this.webSocket);",
            workerThreadClassName, workerThreadClassName);
    buildMethod.getBody().setSource(methodBody);

    // Add method to class
    builderClass.add(buildMethod);

    return builderClass;
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

  /**
   * Private helper method to lowercase the first letter of a string.
   * Function will return null, if argument was null.
   *
   * @param text Text to process
   *
   * @return Text with first letter lowercased or null
   */
  private static String firstLetterLowercase(final String text)
  {
    return (null == text ? null : text.substring(0, 1).toLowerCase() + text.substring(1, text.length()));
  }
}
