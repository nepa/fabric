/** 29.10.2012 18:30 */
package fabric.module.midgen4j.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Collections;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;
import fabric.module.api.FDefaultWSDLHandler;

/**
 * Fabric handler class to extend the Java Middleware Generator. This
 * handler is part of the MidGen4J-WebSockets extension and generates
 * code to convert bean objects to JSON documents and vice versa. The
 * beans must be created with the Fabric TypeGen module and must have
 * JAXB annotations. The JSONMarshallerGenerator is a conventional WSDL
 * handler and defines a couple of callback methods to process WSDL files.
 *
 * @author seidel
 */
public class JSONMarshallerGenerator extends FDefaultWSDLHandler
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(JSONMarshallerGenerator.class);

  /** Name of the JSON marshaller class */
  public static final String MARSHALLER_CLASS_NAME = "JSONMarshaller";

  /** Workspace object for code write-out */
  private Workspace workspace;

  /** Properties object for module configuration */
  private Properties properties;

  /** List of required Java imports */
  private ArrayList<String> requiredImports;

  /** Java package name for WebSocket interface classes */
  private String packageName;

  /**
   * Constructor initializes the JSONMarshallerGenerator, which
   * can create code to de-/serialize bean objects to JSON.
   *
   * @param workspace Workspace object for source code write-out
   * @param properties Properties object with module options
   */
  public JSONMarshallerGenerator(Workspace workspace, Properties properties) throws Exception
  {
    this.workspace = workspace;
    this.properties = properties;
    this.requiredImports = new ArrayList<String>();

    // Extract global properties
    this.packageName = this.properties.getProperty(MidGen4JWebSocketsModule.PACKAGE_NAME_KEY);
  }

  /**
   * Create class for JSON marshaller before processing any other
   * element of the WSDL document.
   *
   * @throws Exception Error during code generation
   */
  @Override
  public void executeBeforeProcessing() throws Exception
  {
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, MARSHALLER_CLASS_NAME);

    // Create JSON marshaller class
    jsf.add(this.createJSONMarshallerClass());
    LOGGER.info(String.format("Created '%s' class for JSON de-/serialization of bean objects.", MARSHALLER_CLASS_NAME));
  }

  /**
   * Add required Java imports to source file of the JSON marshaller
   * class, after processing all elements of the WSDL document.
   *
   * @throws Exception Error during execution
   */
  @Override
  public void executeAfterProcessing() throws Exception
  {
    // Add required imports to source file
    JSourceFile jsf = this.workspace.getJava().getJSourceFile(this.packageName, MARSHALLER_CLASS_NAME);

    // Sort list alphabetically
    Collections.sort(this.requiredImports);
    for (String requiredImport: this.requiredImports)
    {
      jsf.addImport(requiredImport);
    }
  }

  /**
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
  private JClass createJSONMarshallerClass() throws Exception
  {
    JClass jsonMarshaller = JClass.factory.create(JModifier.PUBLIC, MARSHALLER_CLASS_NAME + "<T>");
    jsonMarshaller.setComment(new JClassCommentImpl("The JSON marshaller class."));

    // Add methods to class
    jsonMarshaller.add(this.createSerializeMethod());
    jsonMarshaller.add(this.createDeserializeMethod());

    return jsonMarshaller;
  }

  /**
   * Create method to serialize a bean object to a JSON document.
   *
   * @return JMethod object to serialize bean objects
   *
   * @throws Exception Error during code generation
   */
  private JMethod createSerializeMethod() throws Exception
  {
    // Create marshaller function
    JParameter beanClass = JParameter.factory.create(JModifier.FINAL, "Class<T>", "beanClass");
    JParameter beanObject = JParameter.factory.create(JModifier.FINAL, "T", "beanObject");
    JMethodSignature jms = JMethodSignature.factory.create(beanClass, beanObject);

    JMethod instanceToJSON = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC, "String", "instanceToJSON", jms);
    instanceToJSON.setComment(new JMethodCommentImpl("Serialize bean object to JSON document."));

    // Set method body
    String methodBody =
            "JAXBContext context = JAXBContext.newInstance(beanClass);\n" +
            "Marshaller marshaller = context.createMarshaller();\n\n" +
            "Configuration configuration = new Configuration();\n" +
            "MappedNamespaceConvention convention = new MappedNamespaceConvention(configuration);\n" +
            "StringWriter jsonDocument = new StringWriter();\n" +
            "XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(convention, jsonDocument);\n" +
            "marshaller.marshal(beanObject, xmlStreamWriter);\n\n" +
            "return jsonDocument.toString();";
    instanceToJSON.getBody().setSource(methodBody);

    // Add required imports
    this.addRequiredImport("java.io.StringWriter");
    this.addRequiredImport("javax.xml.bind.JAXBContext");
    this.addRequiredImport("javax.xml.bind.Marshaller");
    this.addRequiredImport("javax.xml.stream.XMLStreamWriter");
    this.addRequiredImport("org.codehaus.jettison.mapped.Configuration");
    this.addRequiredImport("org.codehaus.jettison.mapped.MappedNamespaceConvention");
    this.addRequiredImport("org.codehaus.jettison.mapped.MappedXMLStreamWriter");

    return instanceToJSON;
  }

  /**
   * Create method to deserialize a JSON document to a bean object.
   *
   * @return JMethod object to deserialize JSON documents
   *
   * @throws Exception Error during code generation
   */
  private JMethod createDeserializeMethod() throws Exception
  {
    // Create unmarshaller function
    JParameter beanClass = JParameter.factory.create(JModifier.FINAL, "Class<T>", "beanClass");
    JParameter jsonDocument = JParameter.factory.create(JModifier.FINAL, "String", "jsonDocument");
    JMethodSignature jms = JMethodSignature.factory.create(beanClass, jsonDocument);

    JMethod jsonToInstance = JMethod.factory.create(JModifier.PUBLIC | JModifier.STATIC, "T", "jsonToInstance", jms);
    jsonToInstance.setComment(new JMethodCommentImpl("Deserialize JSON document to bean object."));

    // Set method body
    String methodBody =
            "JAXBContext context = JAXBContext.newInstance(beanClass);\n" +
            "Unmarshaller unmarshaller = context.createUnmarshaller();\n\n" +
            "JSONObject jsonObject = new JSONObject(jsonDocument);\n" +
            "Configuration configuration = new Configuration();\n" +
            "MappedNamespaceConvention convention = new MappedNamespaceConvention(configuration);\n" +
            "XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(jsonObject, convention);\n\n" +
            "return (T)unmarshaller.unmarshal(xmlStreamReader);";
    jsonToInstance.getBody().setSource(methodBody);

    // Add required imports
    this.addRequiredImport("javax.xml.bind.JAXBContext");
    this.addRequiredImport("javax.xml.bind.Unmarshaller");
    this.addRequiredImport("javax.xml.stream.XMLStreamReader");
    this.addRequiredImport("org.codehaus.jettison.json.JSONObject");
    this.addRequiredImport("org.codehaus.jettison.mapped.Configuration");
    this.addRequiredImport("org.codehaus.jettison.mapped.MappedNamespaceConvention");
    this.addRequiredImport("org.codehaus.jettison.mapped.MappedXMLStreamReader");

    return jsonToInstance;
  }

  /**
   * Private helper method to add an import to the internal list
   * of required imports. All entries will later be written to
   * the Java source file.
   *
   * Multiple calls to this function with the same argument will
   * result in a single import statement on source code write-out.
   *
   * @param requiredImport Name of required import
   */
  private void addRequiredImport(final String requiredImport)
  {
    if (null != requiredImport && !this.requiredImports.contains(requiredImport))
    {
      this.requiredImports.add(requiredImport);
    }
  }
}
