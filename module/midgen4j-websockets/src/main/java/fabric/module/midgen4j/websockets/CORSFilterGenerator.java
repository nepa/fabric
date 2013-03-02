/** 02.03.2013 21:27 */
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
 * Fabric handler class to extend the Java Middleware Generator. This
 * handler is part of the MidGen4J-WebSockets extension and generates
 * a CORS filter compliant to the Java Servlet 2.3 specification. The
 * filter is later used to activate Cross-Origin Resource Sharing, so
 * that the WebSockets service interface can process cross-domain
 * requests.
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
   * Constructor initializes the CORSFilterGenerator, which can
   * create a filter to activate Cross-Domain Resource Sharing.
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
   * Create a Java class that contains the CORS filter, before
   * processing any other element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    this.createCORSFilterFile();
  }

  /**
   * Create a source file that contains the CORS filter. The
   * method will generate the corresponding Java class and
   * add all necessary Java imports to the file.
   *
   * @throws Exception Error during code generation
   */
  private void createCORSFilterFile() throws Exception
  {
    // Create source file
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, FILTER_CLASS_NAME);

    // Add CORS filter class
    jsf.add(this.createCORSFilterClass());

    // Add required imports to source file
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
   * Create CORS filter class with its three default methods
   * init(), destroy() and doFilter(). Filters were introduced
   * with the Java Servlet 2.3 specifications and are used to
   * apply various changes to HTTP requests and responses.
   *
   * For example, filters can implement compression and encoding
   * algorithems that should be applied to all requests a servlet
   * container receives.
   *
   * Also read:
   *   http://www.oracle.com/technetwork/java/filters-137243.html
   *
   * @return JClass object with CORS filter class
   *
   * @throws Exception Error during code generation
   */
  private JClass createCORSFilterClass() throws Exception
  {
    JClass corsFilter = JClass.factory.create(JModifier.PUBLIC, FILTER_CLASS_NAME);
    corsFilter.addImplements(JInterface.factory.create(JModifier.NONE, "Filter"));
    corsFilter.setComment(new JClassCommentImpl("Filter to enable Cross-Origin Resource Sharing (CORS)."));

    // Add methods to class
    corsFilter.add(this.createInitMethod());
    corsFilter.add(this.createDestroyMethod());
    corsFilter.add(this.createDoFilterMethod());

    return corsFilter;
  }

  /**
   * Create method to initialize the CORS filter.
   *
   * @return JMethod object to initialize filter
   *
   * @throws Exception Error during code generation
   */
  private JMethod createInitMethod() throws Exception
  {
    JParameter filterConfig = JParameter.factory.create("FilterConfig", "filterConfig");
    JMethodSignature jms = JMethodSignature.factory.create(filterConfig);

    JMethod init = JMethod.factory.create(JModifier.PUBLIC, "void", "init",
            jms, new String[] { "ServletException" });
    init.addAnnotation(new JMethodAnnotationImpl("Override"));
    init.setComment(new JMethodCommentImpl("Initialize filter."));

    // Set method body
    String methodBody = "// Empty implementation";
    init.getBody().setSource(methodBody);

    return init;
  }

  /**
   * Create method to destroy the CORS filter.
   *
   * @return JMethod object to destroy filter
   *
   * @throws Exception Error during code generation
   */
  private JMethod createDestroyMethod() throws Exception
  {
    JMethod destroy = JMethod.factory.create(JModifier.PUBLIC, "void", "destroy");
    destroy.addAnnotation(new JMethodAnnotationImpl("Override"));
    destroy.setComment(new JMethodCommentImpl("Destroy filter."));

    // Set method body
    String methodBody = "// Empty implementation";
    destroy.getBody().setSource(methodBody);

    return destroy;
  }

  /**
   * Create method to apply the CORS filter.
   *
   * @return JMethod object to apply filter
   *
   * @throws Exception Error during code generation
   */
  private JMethod createDoFilterMethod() throws Exception
  {
    JParameter request = JParameter.factory.create("ServletRequest", "request");
    JParameter response = JParameter.factory.create("ServletResponse", "response");
    JParameter chain = JParameter.factory.create("FilterChain", "chain");
    JMethodSignature jms = JMethodSignature.factory.create(request, response, chain);

    JMethod doFilter = JMethod.factory.create(JModifier.PUBLIC, "void", "doFilter",
            jms, new String[] { "IOException", "ServletException" });
    doFilter.addAnnotation(new JMethodAnnotationImpl("Override"));
    doFilter.setComment(new JMethodCommentImpl("Filter request and add CORS header to response."));

    // Set method body
    String methodBody =
            "HttpServletRequest httpRequest = (HttpServletRequest)request;\n" +
            "HttpServletResponse httpResponse = (HttpServletResponse)response;\n\n" +

            "// Origin of request was NOT set\n" +
            "if (null != httpRequest.getHeader(\"Origin\")) {\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Origin\", \"*\");\n" +
            "}\n\n" +

            "// Client is requesting OPTIONS\n" +
            "if (\"OPTIONS\".equals(httpRequest.getMethod())) {\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Methods\", \"GET, POST, PUT, DELETE, OPTIONS\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Expose-Headers\", \"X-Cache-Date, X-Atmosphere-tracking-id\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Allow-Headers\", \"Origin, Content-Type, X-Cache-Date, \" +\n" +
            "\t\t\"X-Atmosphere-Framework, X-Atmosphere-tracking-id, X-Atmosphere-Transport\");\n" +
            "\thttpResponse.addHeader(\"Access-Control-Max-Age\", \"-1\"); // Default value: -1\n" +
            "}\n\n" +

            "chain.doFilter(httpRequest, httpResponse);";
    doFilter.getBody().setSource(methodBody);

    return doFilter;
  }
}
