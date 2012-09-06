/** 06.09.2012 14:40 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import de.uniluebeck.sourcegen.java.JClass;
import de.uniluebeck.sourcegen.java.JClassCommentImpl;
import de.uniluebeck.sourcegen.java.JField;
import de.uniluebeck.sourcegen.java.JFieldCommentImpl;
import de.uniluebeck.sourcegen.java.JMethod;
import de.uniluebeck.sourcegen.java.JMethodCommentImpl;
import de.uniluebeck.sourcegen.java.JMethodSignature;
import de.uniluebeck.sourcegen.java.JModifier;
import de.uniluebeck.sourcegen.java.JParameter;
import de.uniluebeck.sourcegen.java.JSourceFile;
import de.uniluebeck.sourcegen.Workspace;

import fabric.wsdlschemaparser.wsdl.FMessage;
import fabric.wsdlschemaparser.wsdl.FMessagePart;

/**
 * Helper class that contains a static method to create container
 * classes for WSDL message elements. The code was externalized
 * to this file in order to improve overall maintainability of
 * the project.
 *
 * @author seidel
 */
public class MessageObjectGenerator
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageObjectGenerator.class);

  /** Mapping from XSD built-in types to Java types */
  private static HashMap<String, String> mapping = initMapping();

  /**
   * Create container class for a single WSDL message. Objects of the
   * generated class are later used to pass arguments (message parts)
   * to a service operation.
   *
   * @param workspace Workspace for code write-out
   * @param packageName Java package name for the message class
   * @param beanPackageName Java package name for type classes
   * (JavaBeans) that the message class may import
   * @param message FMessage object from WSDL parser
   *
   * @throws Exception Error during code generation
   */
  public static void createMessageClass(final Workspace workspace, final String packageName, final String beanPackageName, final FMessage message) throws Exception
  {
    // Create class
    String className = MessageObjectGenerator.firstLetterCapital(message.getMessageName()) + "Message";
    JClass messageClass = JClass.factory.create(JModifier.PUBLIC, className);

    LOGGER.debug(String.format("Created container class for message '%s'.", message.getMessageName()));

    // Add a comment
    messageClass.setComment(new JClassCommentImpl(String.format("The '%s' message class.", className)));

    // Get source file from workspace
    JSourceFile jsf = workspace.getJava().getJSourceFile(packageName, className);

    // No duplicate classes!
    if (null == jsf.getClassByName(className))
    {
      jsf.add(messageClass);

      // Process all message parts
      for (FMessagePart messagePart: message.getParts())
      {
        String typeName = MessageObjectGenerator.getCorrectTypeName(messagePart.getNoneNullAttribute().getLocalPart());
        String variableName = messagePart.getPartName();

        /*****************************************************************
         * Add required imports
         *****************************************************************/
        if (!packageName.equals(beanPackageName) && !mapping.containsValue(typeName)) // Custom type class in different package
        {
          // Import custom types only
          String requiredImport = String.format("%s.%s", beanPackageName, typeName);

          // No duplicate imports!
          if (!jsf.containsImport(requiredImport))
          {
            jsf.addImport(requiredImport);
          }
        }

        /*****************************************************************
         * Create member variable
         *****************************************************************/
        JField member = JField.factory.create(JModifier.PRIVATE, typeName, variableName);

        member.setComment(new JFieldCommentImpl(String.format("The '%s' message part.", variableName)));

        messageClass.add(member);

        /*****************************************************************
         * Create setter method
         *****************************************************************/
        JParameter input = JParameter.factory.create(JModifier.FINAL, typeName, variableName);
        JMethodSignature jms = JMethodSignature.factory.create(input);

        JMethod setter = JMethod.factory.create(JModifier.PUBLIC, "void",
                "set" + MessageObjectGenerator.firstLetterCapital(variableName), jms);

        setter.getBody().appendSource(String.format("this.%s = %s;", variableName, variableName));
        setter.setComment(new JMethodCommentImpl(String.format("Set the '%s' message part.", variableName)));

        messageClass.add(setter);

        /*****************************************************************
         * Create getter method
         *****************************************************************/
        JMethod getter = JMethod.factory.create(JModifier.PUBLIC, typeName,
                "get" + MessageObjectGenerator.firstLetterCapital(variableName));

        getter.getBody().appendSource(String.format("return this.%s;", variableName));
        getter.setComment(new JMethodCommentImpl(String.format("Get the '%s' message part.", variableName)));

        messageClass.add(getter);
      }
    }
  }

  /**
   * Private helper method to initialize the datatype mapping
   * from XSD built-in types to Java types:
   *
   *   http://www.w3.org/TR/xmlschema-2/#built-in-datatypes
   *
   * @return Type mapping from XSD built-in types to Java types
   */
  private static HashMap<String, String> initMapping()
  {
    HashMap<String, String> typeMapping = new HashMap<String, String>();

    typeMapping.put("boolean", "boolean");
    typeMapping.put("float", "float");
    typeMapping.put("double", "double");
    typeMapping.put("byte", "byte");
    typeMapping.put("unsignedByte", "short");
    typeMapping.put("short", "short");
    typeMapping.put("unsignedShort", "int");
    typeMapping.put("int", "int");
    typeMapping.put("integer", "java.math.BigDecimal");
    typeMapping.put("positiveInteger", "java.math.BigInteger");
    typeMapping.put("unsignedInt", "java.math.BigInteger");
    typeMapping.put("long", "java.math.BigInteger");
    typeMapping.put("unsignedLong", "java.math.BigDecimal");
    typeMapping.put("decimal", "java.math.BigDecimal");
    typeMapping.put("string", "String");
    typeMapping.put("hexBinary", "byte[]");
    typeMapping.put("base64Binary", "byte[]");
    typeMapping.put("dateTime", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("time", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("date", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("gDay", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("gMonth", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("gMonthDay", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("gYear", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("gYearMonth", "javax.xml.datatype.XMLGregorianCalendar");
    typeMapping.put("duration", "javax.xml.datatype.Duration");
    typeMapping.put("NOTATION", "javax.xml.namespace.QName");
    typeMapping.put("QName", "javax.xml.namespace.QName");
    typeMapping.put("anyURI", "String");
    typeMapping.put("Name", "String");
    typeMapping.put("NCName", "String");
    typeMapping.put("negativeInteger", "java.math.BigDecimal");
    typeMapping.put("NMTOKEN", "String");
    typeMapping.put("nonNegativeInteger", "java.math.BigDecimal");
    typeMapping.put("nonPositiveInteger", "java.math.BigDecimal");
    typeMapping.put("normalizedString", "String");
    typeMapping.put("token", "String");
    typeMapping.put("any", "Object");

    return typeMapping;
  }

  /**
   * Private helper method to find the correct type name for a message
   * part. In the simple case, the part is of one of the XSD built-in
   * types and can directly be mapped to the corresponding Java type.
   * In more complex scenarios, the part can be of a custom type that
   * is defined in the inline XML Schema of the WSDL document.
   *
   * @param typeName Type name from WSDL parser (i.e. local part of
   * QName that was extracted from WSDL document)
   *
   * @return Type name that can be used in generated Java code
   */
  private static String getCorrectTypeName(final String typeName)
  {
    String result = "";

    // Type is an XSD built-in type
    if (mapping.containsKey(typeName))
    {
      result = mapping.get(typeName);
    }
    // Type is a custom type
    else
    {
      result = typeName;
    }

    return result;
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
