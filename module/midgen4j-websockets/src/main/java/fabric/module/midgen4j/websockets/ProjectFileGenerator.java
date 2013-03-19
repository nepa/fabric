/** 19.03.2013 23:23 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.plaintext.PlainTextFile;
import fabric.module.api.FDefaultWSDLHandler;

/**
 * Fabric handler class to extend the Java Middleware Generator. This
 * handler is part of the MidGen4J-WebSockets extension and generates
 * various files to deploy the WebSockets interface as an idependent
 * project.
 *
 * The handler creates a pom.xml file that enables pulling of required
 * third-party libraries at almost zero effort. Furthermore, multiple
 * deployment descriptors are generated (i.e. web.xml, sun-web.xml and
 * glassfish-web.xml). These files facilitate running the WebSockets
 * interface on a webserver with servlet container support (e.g. Tomcat
 * or GlassFish).
 *
 * @author seidel
 */
public class ProjectFileGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectFileGenerator.class);

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** Name of the WebSockets project that will be generated */
  private String projectName;

  /** Path for files of the WebSockets project */
  private String projectPath;

  /** Name of broadcast channel */
  private String channelName;

  /** Java package name for WebSocket interface classes */
  private String packageName;

  /**
   * Constructor initializes the ProjectFileGenerator, which
   * can create various files for an independent WebSockets
   * project.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public ProjectFileGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;

    // Extract global properties
    this.projectName = this.properties.getProperty(MidGen4JWebSocketsModule.INTERFACE_CLASS_NAME_KEY);
    this.projectPath = this.properties.getProperty(MidGen4JWebSocketsModule.PROJECT_PATH_KEY);
    this.channelName = this.properties.getProperty(MidGen4JWebSocketsModule.CHANNEL_NAME_KEY);
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
  }

  /**
   * Create a Maven pom.xml file and deployment descriptors
   * for the WebSockets project, before processing any other
   * element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    this.createMavenPOM();

    this.createWebXML();
    this.createSunWebXML();
    this.createGlassfishWebXML();
  }

  /**
   * Create a pom.xml file for the WebSockets project. This file
   * will enable Apache Maven to pull all required third-party
   * libraries automatically.
   */
  private void createMavenPOM()
  {
    PlainTextFile textFile = this.workspace.getPlainText().getPlainTextFile(
            this.projectPath, "pom", "xml");

    // Create file content
    String fileContent = String.format(
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +

"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n\n" +

"  <modelVersion>4.0.0</modelVersion>\n\n" +

"  <name>%s</name>\n" +
"  <groupId>%s</groupId>\n" +
"  <artifactId>%s</artifactId>\n" +
"  <packaging>war</packaging>\n" +
"  <version>1.0-SNAPSHOT</version>\n\n" +

"  <repositories>\n" +
"    <repository>\n" +
"      <id>java.net2</id>\n" +
"      <name>Repository hosting the JEE 6 artifacts.</name>\n" +
"      <url>http://download.java.net/maven/2</url>\n" +
"    </repository>\n\n" +

"    <repository>\n" +
"      <id>Sonatype snapshots</id>\n" +
"      <name>Repository hosting the Atmosphere Framework.</name>\n" +
"      <url>https://oss.sonatype.org/content/repositories/snapshots</url>\n" +
"    </repository>\n\n" +

"    <repository>\n" +
"      <id>Sonatype releases</id>\n" +
"      <name>Repository hosting the Atmosphere Framework.</name>\n" +
"      <url>https://oss.sonatype.org/content/repositories/releases</url>\n" +
"    </repository>\n" +
"  </repositories>\n\n" +

"  <dependencies>\n" +
"    <dependency>\n" +
"      <groupId>javax</groupId>\n" +
"      <artifactId>javaee-web-api</artifactId>\n" +
"      <version>6.0</version>\n" +
"      <scope>provided</scope>\n" +
"    </dependency>\n\n" +

"    <dependency>\n" +
"      <groupId>junit</groupId>\n" +
"      <artifactId>junit</artifactId>\n" +
"      <version>3.8.2</version>\n" +
"      <scope>test</scope>\n" +
"    </dependency>\n\n" +

"    <dependency>\n" +
"      <groupId>org.slf4j</groupId>\n" +
"      <artifactId>slf4j-simple</artifactId>\n" +
"      <version>1.6.6</version>\n" +
"    </dependency>\n\n" +

"    <!-- Atmosphere Framework: Server-side runtime -->\n" +
"    <dependency>\n" +
"      <groupId>org.atmosphere</groupId>\n" +
"      <artifactId>atmosphere-runtime</artifactId>\n" +
"      <version>${atmo-version}</version>\n" +
"    </dependency>\n\n" +

"    <!-- Atmosphere Framework: Client-side JavaScript -->\n" +
"    <dependency>\n" +
"      <groupId>org.atmosphere</groupId>\n" +
"      <artifactId>atmosphere-jquery</artifactId>\n" +
"      <version>${atmo-version}</version>\n" +
"      <type>war</type>\n" +
"    </dependency>\n\n" +

"    <!--\n" +
"      This is only required, if you use the Atmosphere Framework\n" +
"      with annotations. It you use an atmosphere.xml file, this\n" +
"      dependency is unnecessary.\n" +
"    -->\n" +
"    <dependency>\n" +
"      <groupId>eu.infomas</groupId>\n" +
"      <artifactId>annotation-detector</artifactId>\n" +
"      <version>3.0.0</version>\n" +
"    </dependency>\n" +
"  </dependencies>\n\n" +

"  <build>\n" +
"    <finalName>%s</finalName>\n\n" +

"    <plugins>\n" +
"      <plugin>\n" +
"        <groupId>org.apache.maven.plugins</groupId>\n" +
"        <artifactId>maven-compiler-plugin</artifactId>\n" +
"        <version>2.3.2</version>\n" +
"        <configuration>\n" +
"          <source>1.6</source>\n" +
"          <target>1.6</target>\n" +
"        </configuration>\n" +
"      </plugin>\n\n" +

"      <plugin>\n" +
"        <groupId>org.apache.maven.plugins</groupId>\n" +
"        <artifactId>maven-war-plugin</artifactId>\n" +
"        <version>2.1.1</version>\n" +
"        <configuration>\n" +
"          <failOnMissingWebXml>true</failOnMissingWebXml>\n" +
"          <webXml>src/main/webapp/WEB-INF/web.xml</webXml>\n" +
"        </configuration>\n" +
"      </plugin>\n" +
"    </plugins>\n" +
"  </build>\n\n" +

"  <properties>\n" +
"    <!--\n" +
"      Custom property to define which version of\n" +
"      the Atmosphere Framework should be used.\n\n" +

"      To force update of snapshot and release\n" +
"      dependencies run: mvn -U clean install\n" +
"    -->\n" +
"    <atmo-version>1.1.0-SNAPSHOT</atmo-version>\n" +
"  </properties>\n\n" +

"</project>",
            this.projectName,
            this.properties.getProperty(MidGen4JWebSocketsModule.MAVEN_GROUP_ID_KEY),
            this.properties.getProperty(MidGen4JWebSocketsModule.MAVEN_ARTIFACT_ID_KEY),
            this.projectName);

    textFile.getContent().setCode(fileContent);

    LOGGER.info(String.format("Created Maven '%s.%s' for WebSockets interface.",
            textFile.getFileName(), textFile.getExtension()));
  }

  /**
   * Create a deployment descriptor for the WebSockets project.
   * The file will be named web.xml and contains information
   * about the Atmosphere servlet that acts as a controller
   * for the generated service backend.
   *
   * The deployment descriptor is needed to deploy the project
   * on a webserver with servlet container support, such as
   * Tomcat or GlassFish.
   */
  private void createWebXML()
  {
    PlainTextFile textFile = this.workspace.getPlainText().getPlainTextFile(
            this.projectPath + "/webapp/WEB-INF", "web", "xml");

    // Create file content
    String fileContent = String.format(
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +

"<web-app version=\"2.5\" xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
"  xmlns:j2ee=\"http://java.sun.com/xml/ns/javaee\"\n" +
"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_5.xsd\">\n\n" +

"  <display-name>%s</display-name>\n" +
"  <description>WebSockets interface for the central MidGen4J service provider.</description>\n\n" +

"  <filter>\n" +
"    <filter-name>CORS-Filter</filter-name>\n" +
"    <filter-class>%s.CORSFilter</filter-class>\n" +
"  </filter>\n\n" +

"  <filter-mapping>\n" +
"    <filter-name>CORS-Filter</filter-name>\n" +
"    <servlet-name>AtmosphereServlet</servlet-name>\n" +
"  </filter-mapping>\n\n" +

"  <servlet>\n" +
"    <description>AtmosphereServlet</description>\n" +
"    <servlet-name>AtmosphereServlet</servlet-name>\n" +
"    <servlet-class>org.atmosphere.cpr.AtmosphereServlet</servlet-class>\n\n" +

"    <!-- If you want to use Servlet 3.0 -->\n" +
"    <async-supported>true</async-supported>\n\n" +

"    <!-- Do NOT start servlet with web app automatically -->\n" +
"    <load-on-startup>0</load-on-startup>\n\n" +

"    <!-- List of init-param elements -->\n\n" +

"    <!--\n" +
"      Sample item:\n\n" +

"      <init-param>\n" +
"        <param-name>Name of parameter</param-name>\n" +
"        <param-value>Value of parameter</param-value>\n" +
"      </init-param>\n" +
"    -->\n" +
"  </servlet>\n\n" +

"  <servlet-mapping>\n" +
"    <servlet-name>AtmosphereServlet</servlet-name>\n" +
"    <!-- Any mapping from path to servlet -->\n" +
"    <url-pattern>/%s/*</url-pattern>\n" +
"  </servlet-mapping>\n" +
"</web-app>",
            this.projectName, this.packageName, this.channelName);

    textFile.getContent().setCode(fileContent);

    LOGGER.info(String.format("Created deployment descriptor '%s.%s' for WebSockets interface.",
            textFile.getFileName(), textFile.getExtension()));
  }

  /**
   * Create a vendor-specific deployment descriptor for the
   * WebSockets project. The file will be named sun-web.xml
   * and defines the context-root that is used when running
   * the WebSockets interface on a GlassFish server.
   *
   * This file is not always needed, for example if you use
   * a servlet container other than GlassFish.
   */
  private void createSunWebXML()
  {
    PlainTextFile textFile = this.workspace.getPlainText().getPlainTextFile(
            this.projectPath + "/webapp/WEB-INF", "sun-web", "xml");

    // Create file content
    String fileContent =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +

"<!DOCTYPE sun-web-app PUBLIC\n" +
"  \"-//Sun Microsystems, Inc.//DTD GlassFish Application Server 3.0 Servlet 3.0//EN\"\n" +
"  \"http://www.sun.com/software/appserver/dtds/sun-web-app_3_0-0.dtd\">\n\n" +

"<sun-web-app error-url=\"\">\n" +
"  <context-root>/</context-root>\n\n" +

"  <class-loader delegate=\"true\"/>\n" +
"  <jsp-config>\n" +
"    <property name=\"keepgenerated\" value=\"true\">\n" +
"      <description>Keep a copy of the generated servlet class' java code.</description>\n" +
"    </property>\n" +
"  </jsp-config>\n" +
"</sun-web-app>";

    textFile.getContent().setCode(fileContent);

    LOGGER.info(String.format("Created deployment descriptor '%s.%s' for WebSockets interface.",
            textFile.getFileName(), textFile.getExtension()));
  }

  /**
   * Create a vendor-specific deployment descriptor for the
   * WebSockets project. The file is named glassfish-web.xml
   * and will only be used by newer versions of the servlet
   * container that comes built-in with the GlassFish server.
   */
  private void createGlassfishWebXML()
  {
    PlainTextFile textFile = this.workspace.getPlainText().getPlainTextFile(
            this.projectPath + "/webapp/WEB-INF", "glassfish-web", "xml");

    // Create file content
    String fileContent =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +

"<!DOCTYPE glassfish-web-app PUBLIC\n" +
"  \"-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN\"\n" +
"  \"http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd\">\n" +
"<glassfish-web-app error-url=\"\">\n" +
"  <class-loader delegate=\"true\"/>\n" +
"  <jsp-config>\n" +
"    <property name=\"keepgenerated\" value=\"true\">\n" +
"      <description>Keep a copy of the generated servlet class' java code.</description>\n" +
"    </property>\n" +
"  </jsp-config>\n" +
"</glassfish-web-app>";

    textFile.getContent().setCode(fileContent);

    LOGGER.info(String.format("Created deployment descriptor '%s.%s' for WebSockets interface.",
            textFile.getFileName(), textFile.getExtension()));
  }
}
