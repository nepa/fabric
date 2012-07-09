/** 10.07.2012 01:18 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * Interface for one part of a surrounding message in a
 * WSDL document. This file defines all method signatures
 * for FMessagePartImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface FMessagePart
{
  /*****************************************************************
   * FMessagePartFactory inner class
   *****************************************************************/

  public static final class FMessagePartFactory
  {
    /** Factory instance for Singleton pattern */
    private static FMessagePartFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FMessagePartFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FMessagePartFactory object
     */
    public static synchronized FMessagePartFactory getInstance()
    {
      if (null == FMessagePartFactory.instance)
      {
        FMessagePartFactory.instance = new FMessagePartFactory();
      }

      return FMessagePartFactory.instance;
    }

    /**
     * Create a new FMessagePartImpl object with the given name
     * and QName value. If the message part has 'element' attribute
     * set, pass 'true' as third parameter. If the message part has
     * 'type' attribute set, use 'false' respectively.
     *
     * @param partName Name of the message part
     * @param type QName to determine type of message part
     * @param hasElementAttribute True if message part has
     * 'element' attribute set, false if 'type' attribute
     * is set
     *
     * @return FMessagePartImpl object
     */
    public FMessagePartImpl create(final String partName, final QName type, final boolean hasElementAttribute)
    {
      return new FMessagePartImpl(partName, type, hasElementAttribute);
    }

    /**
     * Create a new FMessagePartImpl object with the given name
     * and QName value. The last two arguments are mutually
     * exclusive, so only set one of them (pass 'null' for the
     * unused one). The constructor automatically sets the
     * correct type for the message part then.
     *
     * @param partName Name of the message part
     * @param elementName QName value of the 'element' attribute
     * @param typeName QName value of the 'type' attribute
     *
     * @return FMessagePartImpl object
     */
    public FMessagePartImpl create(final String partName, final QName elementName, final QName typeName)
    {
      return new FMessagePartImpl(partName, elementName, typeName);
    }
  }

  /*****************************************************************
   * FMessagePart outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FMessagePartFactory factory = FMessagePartFactory.getInstance();

  public void setPartName(final String partName);
  public String getpartName();

  public void setElementName(final QName elementName);
  public QName getElementName();

  public void setTypeName(final QName typeName);
  public QName getTypeName();

  public boolean hasElementAttribute();
  public boolean hasTypeAttribute();
}
