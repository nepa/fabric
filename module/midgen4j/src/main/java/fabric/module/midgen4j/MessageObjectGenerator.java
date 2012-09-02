/** 31.08.2012 16:52 */
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

  /**
   * Create container class for a single WSDL message. Objects of the
   * generated class are later used to pass arguments (message parts)
   * to a service operation.
   *
   * @param message FMessage object from WSDL parser
   *
   * @return JClass object with message container class
   *
   * @throws Exception Error during code generation
   */
  public static JClass createMessageClass(final FMessage message) throws Exception
  {
    // Create class
    String className = MessageObjectGenerator.firstLetterCapital(message.getMessageName()) + "Message";
    JClass messageClass = JClass.factory.create(JModifier.PUBLIC, className);

    LOGGER.debug(String.format("Created container class for message '%s'.", message.getMessageName()));

    // Add a comment
    messageClass.setComment(new JClassCommentImpl(String.format("The '%s' message class.", className)));

    // Process all message parts
    for (FMessagePart messagePart: message.getParts())
    {
      String typeName = MessageObjectGenerator.getCorrectTypeName(messagePart.getNoneNullAttribute().getLocalPart());
      String variableName = messagePart.getPartName();

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

    return messageClass;
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

    // Mapping from XSD built-in types to Java types:
    //   http://www.w3.org/TR/xmlschema-2/#built-in-datatypes
    HashMap<String, String> mapping = new HashMap<String, String>();
    mapping.put("boolean", "boolean");
    mapping.put("float", "float");
    mapping.put("double", "double");
    mapping.put("byte", "byte");
    mapping.put("unsignedByte", "short");
    mapping.put("short", "short");
    mapping.put("unsignedShort", "int");
    mapping.put("int", "int");
    mapping.put("integer", "java.math.BigDecimal");
    mapping.put("positiveInteger", "java.math.BigInteger");
    mapping.put("unsignedInt", "java.math.BigInteger");
    mapping.put("long", "java.math.BigInteger");
    mapping.put("unsignedLong", "java.math.BigDecimal");
    mapping.put("decimal", "java.math.BigDecimal");
    mapping.put("string", "String");
    mapping.put("hexBinary", "byte[]");
    mapping.put("base64Binary", "byte[]");
    mapping.put("dateTime", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("time", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("date", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("gDay", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("gMonth", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("gMonthDay", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("gYear", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("gYearMonth", "javax.xml.datatype.XMLGregorianCalendar");
    mapping.put("duration", "javax.xml.datatype.Duration");
    mapping.put("NOTATION", "javax.xml.namespace.QName");
    mapping.put("QName", "javax.xml.namespace.QName");
    mapping.put("anyURI", "String");
    mapping.put("Name", "String");
    mapping.put("NCName", "String");
    mapping.put("negativeInteger", "java.math.BigDecimal");
    mapping.put("NMTOKEN", "String");
    mapping.put("nonNegativeInteger", "java.math.BigDecimal");
    mapping.put("nonPositiveInteger", "java.math.BigDecimal");
    mapping.put("normalizedString", "String");
    mapping.put("token", "String");
    mapping.put("any", "Object");

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
}
