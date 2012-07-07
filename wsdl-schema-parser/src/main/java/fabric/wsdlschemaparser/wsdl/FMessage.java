/** 05.07.2012 20:33 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a single message within a
 * WSDL document. Each message may consist of one
 * or more message parts.
 *
 * @author seidel
 */
public class FMessage extends FWSDLElement
{
  /** Name of the message */
  private String messageName;

  /** Set of message parts */
  private Set<FMessagePart> parts;

  /**
   * Parameterized constructor creates a new webservice message
   * with the given name and an empty set of message parts.
   *
   * @param messageName Name of the message
   */
  public FMessage(final String messageName)
  {
    this.messageName = messageName;
    this.parts = new HashSet<FMessagePart>();
  }

  /**
   * Set the name of the message.
   *
   * @param messageName Name of the message
   */
  public void setMessageName(final String messageName)
  {
    this.messageName = messageName;
  }

  /**
   * Get the name of the message.
   *
   * @return Name of the message
   */
  public String getMessageName()
  {
    return this.messageName;
  }

  /**
   * Add a new message part to the message.
   *
   * @param part Message part to add
   */
  public void addPart(final FMessagePart part)
  {
    this.parts.add(part);
  }

  /**
   * Add multiple new message parts to the message.
   *
   * @param parts Set of message parts to add
   */
  public void addParts(final HashSet<FMessagePart> parts)
  {
    this.parts.addAll(parts);
  }

  /**
   * Set the message parts of the message.
   *
   * @param parts Set of message parts
   */
  public void setParts(final HashSet<FMessagePart> parts)
  {
    this.parts = parts;
  }

  /**
   * Get a set off all message parts.
   *
   * @return Set of message parts
   */
  public HashSet<FMessagePart> getParts()
  {
    return (HashSet<FMessagePart>)this.parts;
  }

  /**
   * Return the number of message parts that are
   * defined within the current message object.
   *
   * @return Number of message parts
   */
  public int partCount()
  {
    return this.parts.size();
  }

  /**
   * Determine whether the message has multiple parts
   * (parts have 'element' attribute set) or just one
   * single part (part has 'type' attribute set).
   *
   * @return True if message has multiple parts, false
   * otherwise
   */
  public boolean isMultipart()
  {
    return this.parts.size() > 1;
  }

  /**
   * Determine whether the message has only one part.
   *
   * For more information see the documentation of
   * isMultipart().
   *
   * @return True if message has one single part, false
   * otherwise
   */
  public boolean isSinglepart()
  {
    return this.parts.size() == 1;
  }

  /**
   * Create a human-readable form of a message and all
   * message parts that are defined within it.
   *
   * @return String representation of FMessage object
   */
  @Override
  public String toString()
  {
    String result = "";

    result = String.format("Message: '%s' (%d part%s)", this.messageName,
            this.partCount(), (this.partCount() != 1 ? "s" : ""));

    for (FMessagePart part: this.parts)
    {
      result += "\n\t" + part.toString();
    }

    return result;
  }
}
