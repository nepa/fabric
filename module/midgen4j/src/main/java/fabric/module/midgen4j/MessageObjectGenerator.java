/** 26.07.2012 11:28 */
package fabric.module.midgen4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    String className = message.getMessageName() + "Message";
    JClass messageClass = JClass.factory.create(JModifier.PUBLIC, className);

    LOGGER.debug(String.format("Created container class for message '%s'.", message.getMessageName()));

    // Add a comment
    messageClass.setComment(new JClassCommentImpl(String.format("The '%s' message class.", className)));

    // Process all message parts
    for (FMessagePart messagePart: message.getParts())
    {
      // TODO: Prepend main Schema name
      // TODO: append "Type" for custom types and map XSD built-in types to Java?
      String typeName = messagePart.getNoneNullAttribute().getLocalPart() + "Type";
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
      JParameter input = JParameter.factory.create(typeName, variableName);
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
}
