/** 29.06.2013 17:32 */
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

    LOGGER.debug(String.format("Created '%s' class for JSON de-/serialization of bean objects.", MARSHALLER_CLASS_NAME));

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
    JParameter beanObject = JParameter.factory.create(JModifier.FINAL, "T", "beanObject");
    JMethodSignature jms = JMethodSignature.factory.create(beanObject);

    JMethod instanceToJSON = JMethod.factory.create(JModifier.PUBLIC,
            "String", "instanceToJSON", jms, new String[] { "Exception" });
    instanceToJSON.setComment(new JMethodCommentImpl("Serialize bean object to JSON document."));

    // Set method body
    String methodBody =
            "ObjectMapper mapper = new ObjectMapper();\n" +
            "mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);\n" +
            "mapper.configure(SerializationFeature.INDENT_OUTPUT, true);\n\n" +
            "AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());\n" +
            "mapper.setAnnotationIntrospector(introspector);\n\n" +
            "return mapper.writeValueAsString(beanObject);";
    instanceToJSON.getBody().setSource(methodBody);

    // Add required imports
    this.addRequiredImport("com.fasterxml.jackson.databind.AnnotationIntrospector");
    this.addRequiredImport("com.fasterxml.jackson.databind.ObjectMapper");
    this.addRequiredImport("com.fasterxml.jackson.databind.SerializationFeature");
    this.addRequiredImport("com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector");

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

    JMethod jsonToInstance = JMethod.factory.create(JModifier.PUBLIC,
            "T", "jsonToInstance", jms, new String[] { "Exception" });
    jsonToInstance.setComment(new JMethodCommentImpl("Deserialize JSON document to bean object."));

    // Set method body
    String methodBody =
            "ObjectMapper mapper = new ObjectMapper();\n" +
            "mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);\n\n" +
            "AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());\n" +
            "mapper.setAnnotationIntrospector(introspector);\n\n" +
            "return mapper.readValue(jsonDocument, beanClass);";
    jsonToInstance.getBody().setSource(methodBody);

    // Add required imports
    this.addRequiredImport("com.fasterxml.jackson.databind.AnnotationIntrospector");
    this.addRequiredImport("com.fasterxml.jackson.databind.DeserializationFeature");
    this.addRequiredImport("com.fasterxml.jackson.databind.ObjectMapper");
    this.addRequiredImport("com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector");

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
