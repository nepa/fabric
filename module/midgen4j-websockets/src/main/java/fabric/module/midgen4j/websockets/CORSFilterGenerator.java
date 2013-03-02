/** 02.03.2013 02:56 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JInterface;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodAnnotationImpl;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;
import fabric.module.api.FDefaultWSDLHandler;

/**
 * TODO: Add class comment
 *
 * @author seidel
 */
public class CORSFilterGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(CORSFilterGenerator.class);

  /** Name of the CORS filter class */
  public static final String FILTER_CLASS_NAME = "CORSFilter";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Java package name for WebSocket interface classes */
  private String packageName;

  /**
   * TODO: Update comment
   *
   * Constructor initializes the JSONMarshallerGenerator, which
   * can create code to de-/serialize bean objects to JSON.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public CORSFilterGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    // Extract global properties
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
  }

  /**
   * TODO: Update comment
   *
   * Create a Maven pom.xml file and deployment descriptors
   * for the WebSockets project, before processing any other
   * element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    this.createCORSFilterFile();
  }

  // TODO: Add comment
  private void createCORSFilterFile() throws Exception
  {
    // Add required imports to source file
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, FILTER_CLASS_NAME);

    // Add CORS filter class
    jsf.add(this.createCORSFilterClass());

    // Add required Java imports
    jsf.addImport("java.io.IOException");
    jsf.addImport("javax.servlet.Filter");
    jsf.addImport("javax.servlet.FilterChain");
    jsf.addImport("javax.servlet.FilterConfig");
    jsf.addImport("javax.servlet.ServletException");
    jsf.addImport("javax.servlet.ServletRequest");
    jsf.addImport("javax.servlet.ServletResponse");
    jsf.addImport("javax.servlet.http.HttpServletRequest");
    jsf.addImport("javax.servlet.http.HttpServletResponse");
  }

  /**
   * TODO: Update comment
   *
   * Create JSON marshaller class that is able to convert bean
   * objects to JSON documents and vice versa. The beans must
   * be created with the Fabric TypeGen module and must have
   * JAXB annotations. The marshaller uses generics, so that
   * we do not need a new serializer class for each bean type.
   *
   * @return JClass object with JSON marshaller class
   *
   * @throws Exception Error during code generation
   */
  private JClass createCORSFilterClass() throws Exception
  {
    JClass corsFilter = JClass.factory.create(JModifier.PUBLIC, FILTER_CLASS_NAME);
    corsFilter.addImplements(JInterface.factory.create(JModifier.NONE, "Filter"));
    corsFilter.setComment(new JClassCommentImpl("Filter to enable Cross-Origin Resource Sharing (CORS)."));

    // TODO: Refactor method creation to own functions (3x)

    // Add init() method
    JParameter filterConfig = JParameter.factory.create("FilterConfig", "filterConfig");
    JMethodSignature jms = JMethodSignature.factory.create(filterConfig);

    JMethod init = JMethod.factory.create(JModifier.PUBLIC, "void", "init",
            jms, new String[] { "ServletException" });
    init.addAnnotation(new JMethodAnnotationImpl("Override"));
    init.setComment(new JMethodCommentImpl("Initialize filter."));

    // Set method body
    String methodBody = "// Empty implementation";
    init.getBody().setSource(methodBody);

    // Add method to class
    corsFilter.add(init);

    // Add destroy() method
    JMethod destroy = JMethod.factory.create(JModifier.PUBLIC, "void", "destroy");
    destroy.addAnnotation(new JMethodAnnotationImpl("Override"));
    destroy.setComment(new JMethodCommentImpl("Destroy filter."));

    // Set method body
    methodBody = "// Empty implementation";
    destroy.getBody().setSource(methodBody);

    // Add method to class
    corsFilter.add(destroy);

    // Add doFilter() method
    JParameter request = JParameter.factory.create("ServletRequest", "request");
    JParameter response = JParameter.factory.create("ServletResponse", "response");
    JParameter chain = JParameter.factory.create("FilterChain", "chain");
    jms = JMethodSignature.factory.create(request, response, chain);

    JMethod doFilter = JMethod.factory.create(JModifier.PUBLIC, "void", "doFilter",
            jms, new String[] { "IOException", "ServletException" });
    doFilter.addAnnotation(new JMethodAnnotationImpl("Override"));
    doFilter.setComment(new JMethodCommentImpl("Filter request and add CORS header to response."));

    // Set method body
    methodBody =
            "HttpServletRequest httpRequest = (HttpServletRequest)request;\n" +
            "HttpServletResponse httpResponse = (HttpServletResponse)response;\n\n" +

            "// Origin of request was NOT set\n" +
            "if (null != httpRequest.getHeader(\"Origin\"))\n" +
            "{\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Origin\", \"*\");\n" +
            "}\n\n" +

            "// Client is requesting OPTIONS\n" +
            "if (\"OPTIONS\".equals(httpRequest.getMethod()))\n" +
            "{\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Methods\", \"GET, POST, PUT, DELETE, OPTIONS\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Expose-Headers\", \"X-Cache-Date, X-Atmosphere-tracking-id\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Headers\", \"Origin, Content-Type, X-Cache-Date, \" +\n" +
            "\t\t\"X-Atmosphere-Framework, X-Atmosphere-tracking-id, X-Atmosphere-Transport\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Max-Age\", \"-1\"); // Default value: -1\n" +
            "}\n\n" +

            "chain.doFilter(httpRequest, httpResponse);";
    doFilter.getBody().setSource(methodBody);

    // Add method to class
    corsFilter.add(doFilter);

    return corsFilter;
  }
}
