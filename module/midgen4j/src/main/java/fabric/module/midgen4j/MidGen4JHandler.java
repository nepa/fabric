/** 24.07.2012 13:27 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;

import fabric.module.api.FDefaultWSDLHandler;
import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;
import fabric.wsdlschemaparser.wsdl.FOperation;
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

  // TODO: Add comment
  private JClass serviceProviderClass;

  /**
   * Constructor initializes the MidGen4J handler, which generates
   * the central service provider class.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public MidGen4JHandler(Workspace workspace, Properties properties) throws Exception
  {
    // TODO: Do we throw exceptions here?
    // TODO: Are workspace and properties argument needed here?

    this.workspace = workspace;
    this.properties = properties;    
    
    this.serviceProviderClass = this.createServiceProviderClass();

    JSourceFile jsf = this.workspace.getJava().getJSourceFile(
            this.properties.getProperty(MidGen4JModule.PACKAGE_NAME_KEY),
            this.properties.getProperty(MidGen4JModule.SERVICE_PROVIDER_CLASS_NAME_KEY));
    jsf.add(this.serviceProviderClass);
  }

  // TODO: Add comment
  @Override
  public void processPortTypes(final HashSet<FPortType> portTypes) throws Exception
  {
    for (FPortType portType: portTypes)
    {
      for (FOperation operation: portType.getOperations())
      {
        if (null != this.serviceProviderClass)
        {
          this.serviceProviderClass.add(this.createMethod(operation));
        }
      }
    }
  }

  // TODO: Add comment
  private JClass createServiceProviderClass() throws Exception
  {
    String serviceProviderClassName =  this.properties.getProperty(MidGen4JModule.SERVICE_PROVIDER_CLASS_NAME_KEY);
    JClass serviceProvider = JClass.factory.create(JModifier.PUBLIC, serviceProviderClassName);

    // Add a comment
    serviceProvider.setComment(new JClassCommentImpl(String.format(
            "The '%s' service provider class.", serviceProviderClassName)));

    return serviceProvider;
  }

  // TODO: Add comment
  private JMethod createMethod(final FOperation operation) throws Exception
  {
    // Create input arguments
    JParameter methodInput = JParameter.factory.create(
            operation.getInputMessage().getOperationMessageName(),
            "inputMessage");
    
    // Create method signature
    JMethodSignature jms = JMethodSignature.factory.create(methodInput);

    // Create new method callback
    JMethod method = JMethod.factory.create(JModifier.PUBLIC,
            operation.getOutputMessage().getOperationMessageName(),
            operation.getOperationName(),
            jms);

    // Add a comment
    method.setComment(new JMethodCommentImpl(String.format(
            "Call the '%s' service operation.", operation.getOperationName())));

    method.getBody().appendSource("// TODO: Add custom business logic here");

    return method;
  }
}
