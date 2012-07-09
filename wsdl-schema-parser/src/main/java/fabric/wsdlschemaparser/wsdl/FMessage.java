/** 10.07.2012 00:51 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * Interface for a single message in a WSDL document. This
 * file defines all method signatures for FMessageImpl and
 * contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface FMessage
{
  /*****************************************************************
   * FMessageFactory inner class
   *****************************************************************/

  public static final class FMessageFactory
  {
    /** Factory instance for Singleton pattern */
    private static FMessageFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FMessageFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FMessageFactory object
     */
    public static synchronized FMessageFactory getInstance()
    {
      if (null == FMessageFactory.instance)
      {
        FMessageFactory.instance = new FMessageFactory();
      }

      return FMessageFactory.instance;
    }

    /**
     * Create a new FMessageImpl object with the given message name.
     *
     * @param messageName Name of the message
     *
     * @return FMessageImpl object
     */
    public FMessageImpl create(final String messageName)
    {
      return new FMessageImpl(messageName);
    }
  }

  /*****************************************************************
   * FMessage outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FMessageFactory factory = FMessageFactory.getInstance();

  public void setMessageName(final String messageName);
  public String getMessageName();

  public void addPart(final FMessagePart part);
  public void addParts(final HashSet<FMessagePart> parts);
  public void setParts(final HashSet<FMessagePart> parts);
  public HashSet<FMessagePart> getParts();

  public int partCount();

  public boolean isSinglepart();
  public boolean isMultipart();
}
