/** 08.10.2012 23:30 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import fabric.module.api.FDefaultWSDLHandler;

import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;

import fabric.wsdlschemaparser.wsdl.FMessage;
import fabric.wsdlschemaparser.wsdl.FMessagePart;
import fabric.wsdlschemaparser.wsdl.FOperation;
import fabric.wsdlschemaparser.wsdl.FOperationMessage;
import fabric.wsdlschemaparser.wsdl.FPortType;

/**
 * Fabric handler class for the Java Middleware Generator module. This
 * class defines a couple of callback methods which get called by the
 * WSDL processor while reading a file. The MidGen4JHandler acts upon
 * those function calls and generates a central service provider class
 * in the workspace. This class can later be extended with business
 * logic by application developers individually.
 *
 * @author seidel
 */
public class MidGen4JHandler extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MidGen4JHandler.class);

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Java package name for bean classes */
  private String beanPackageName;

  /** Name of main bean class */
  private String beanMainClassName;

  /** Flag to skip creation of bean main class */
  private boolean skipBeanMainClass;

  /** Java package name for generated classes */
  private String packageName;

  /** Name of the service provider class */
  private String serviceProviderClassName;

  /** Class with central service provider */
  private JClass serviceProviderClass;

  /** Message definitions from WSDL file */
  private Set<FMessage> messages;

  /**
   * Constructor initializes the MidGen4J handler, which generates
   * the central service provider class.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public MidGen4JHandler(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    // Extract global properties
    this.beanPackageName = this.properties.getProperty(MidGen4JModule.BEAN_PACKAGE_NAME_KEY);
    this.beanMainClassName = this.properties.getProperty(MidGen4JModule.BEAN_MAIN_CLASS_NAME_KEY);
    this.skipBeanMainClass = ("true").equals(this.properties.getProperty(MidGen4JModule.SKIP_BEAN_MAIN_CLASS_KEY).toLowerCase());

    this.packageName = this.properties.getProperty(MidGen4JModule.PACKAGE_NAME_KEY);
    this.serviceProviderClassName = this.properties.getProperty(MidGen4JModule.SERVICE_PROVIDER_CLASS_NAME_KEY);

    this.messages = new HashSet<FMessage>();
  }

  /**
   * Create class for central service provider before processing
   * any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeBeforeProcessing() throws Exception
  {
    this.serviceProviderClass = this.createServiceProviderClass();

    // Add class to source file
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, this.serviceProviderClassName);
    jsf.add(this.serviceProviderClass);
  }

  /**
   * Delete source file that contains the main bean class. The Fabric
   * TypeGen module always creates this file, but it is not needed by
   * the MidGen4J module. Developers can set a flag in the properties
   * file, such that the MidGen4J base module will remove the file
   * from the Java workspace, before it will actually be serialized
   * to disk.
   *
   * @throws Exception Error during source file deletion
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    // Delete main bean class (if desired)
    if (this.skipBeanMainClass)
    {
      this.workspace.getJava().deleteJSourceFile(this.beanPackageName, this.beanMainClassName);
    }
  }

  /**
   * Process messages that are defined in WSDL document.
   *
   * @param messages Messages of WSDL document
   *
   * @throws Exception Error during processing
   */
  @Override
  public void processMessages(final HashSet<FMessage> messages) throws Exception
  {
    // MidGen4J does not support message parts with element-attribute
    for (FMessage message: messages)
    {
      for (FMessagePart part: message.getParts())
      {
        if (part.hasElementAttribute())
        {
          throw new Exception(String.format(
                  "Message '%s' has a part '%s' with element-attribute. This is not supported. " +
                  "Either provide a WSDL document with no embedded XML Schema and only XSD built-in types or " +
                  "use embedded XSD code, define all types globally and reference them using the type-attribute.",
                  message.getMessageName(), part.getPartName()));
        }
      }
    }

    // Copy message definitions to use them in processPortTypes()
    this.messages = messages;
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
    // Create method stub for each service operation
    for (FPortType portType: portTypes)
    {
      for (FOperation operation: portType.getOperations())
      {
        // Create service method and add it to class
        if (null != this.serviceProviderClass)
        {
          this.serviceProviderClass.add(this.createServiceMethod(operation));
        }

        // Create container class for input message (if any)
        if (null != operation.getInputMessage())
        {
          this.createMessageClass(operation.getInputMessage());
        }

        // Create container class for output message (if any)
        if (null != operation.getOutputMessage())
        {
          this.createMessageClass(operation.getOutputMessage());
        }

        // Do NOT create container classes for fault messages
      }
    }
  }

  /**
   * This method will iterate over all message elements that are
   * defined in the WSDL file and search for the name of the
   * current operation message.
   *
   * We need this workaround, because message elements do not know
   * whether they are used as input, output or fault message. On
   * the other hand, operation messages know whether they are an
   * input, output or fault message, but they do not know their
   * own message parts.
   *
   * So we can now pass an input or output operation message to
   * this method and it will find the corresponding message element
   * with its message parts. This way we can skip fault messages
   * as desired in processPortTypes().
   *
   * @param operationMessage Operation message object (either an
   * input, output or fault message)
   *
   * @throws Exception Error during creation of message class
   */
  private void createMessageClass(final FOperationMessage operationMessage) throws Exception
  {
    String messageName = operationMessage.getMessageAttribute().getLocalPart();

    // Iterate all WSDL message elements
    for (FMessage message: this.messages)
    {
      // Create message class, if operation message was found
      if (messageName.equals(message.getMessageName()))
      {
        MessageObjectGenerator.createMessageClass(this.workspace, this.packageName, this.beanPackageName, message);
      }
    }
  }

  /**
   * Create class for central service provider.
   *
   * @return JClass object with service provider class
   *
   * @throws Exception Error during code generation
   */
  private JClass createServiceProviderClass() throws Exception
  {
    JClass serviceProvider = JClass.factory.create(JModifier.PUBLIC, this.serviceProviderClassName);

    LOGGER.debug(String.format("Created service provider class '%s'.", this.serviceProviderClassName));

    // Add a comment
    serviceProvider.setComment(new JClassCommentImpl(String.format(
            "The '%s' service provider class.", this.serviceProviderClassName)));

    return serviceProvider;
  }

  /**
   * Create method stub for a single service operation. The WSDL input
   * and output messages are mapped to the method's arguments and return
   * type.
   *
   * @param operation FOperation object from WSDL parser
   *
   * @return JMethod object with method stub
   *
   * @throws Exception Error during code generation
   */
  private JMethod createServiceMethod(final FOperation operation) throws Exception
  {
    // Create input argument
    JParameter methodInput = null;

    // Operation has an input value
    if (null != operation.getInputMessage())
    {
      methodInput = JParameter.factory.create(
              JModifier.FINAL,
              MidGen4JHandler.firstLetterCapital(
                operation.getInputMessage().getMessageAttribute().getLocalPart()) + "Message",
              "inputMessage");
    }

    // Create method signature
    JMethodSignature jms = JMethodSignature.factory.create(methodInput);

    // Create output argument
    String methodOutput = "void";
    if (null != operation.getOutputMessage())
    {
      methodOutput = MidGen4JHandler.firstLetterCapital(
              operation.getOutputMessage().getMessageAttribute().getLocalPart()) + "Message";
    }

    // Create method stub
    JMethod method = JMethod.factory.create(
            JModifier.PUBLIC,
            methodOutput,
            operation.getOperationName(),
            jms);

    LOGGER.debug(String.format("Created method stub for service operation '%s'.", operation.getOperationName()));

    // Set method body
    String methodBody = "// TODO: Add your custom business logic here";

    // Operation has a return value
    if (null != operation.getOutputMessage())
    {
      methodBody = String.format(
              "%s result = new %s();\n\n" +
              "%s\n\n" +
              "return result;",
              methodOutput, methodOutput, methodBody);
    }
    method.getBody().appendSource(methodBody);

    // Add a comment
    method.setComment(new JMethodCommentImpl(String.format(
            "Call the '%s' service operation.", operation.getOperationName())));

    return method;
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
