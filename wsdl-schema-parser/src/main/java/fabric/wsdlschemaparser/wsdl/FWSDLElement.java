/** 05.07.2012 18:50 */
package fabric.wsdlschemaparser.wsdl;

/**
 * Abstract base class for all elements that a WSDL
 * document may contain. This means all WSDL parts
 * on the one hand, as well as their sub-parts on
 * the other hand.
 *
 * @author seidel
 */
abstract public class FWSDLElement
{
  /**
   * Getter that will return the internal name of the
   * class in Fabric (e.g. FBinding or FPort). This
   * basically is the name of the corresponding Java
   * class.
   *
   * @return Internal name of the Fabric class
   */
  public String getName()
  {
    return this.getClass().getSimpleName();
  }

  /**
   * Create a human-readable form of the WSDL element.
   *
   * @return String representation of FWSDLElement object
   */
  @Override
  abstract public String toString();
}
