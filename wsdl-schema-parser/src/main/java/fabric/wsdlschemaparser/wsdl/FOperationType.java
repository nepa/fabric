/** 05.07.2012 19:30 */
package fabric.wsdlschemaparser.wsdl;

/**
 * Enumeration that defines all possible WSDL operation types.
 *
 * For more information see sections 2.4.1 to 2.4.4 at:
 *   http://www.w3.org/TR/wsdl#_porttypes
 *
 * @author seidel
 */
public enum FOperationType
{
  INVALID,
  ONE_WAY,
  REQUEST_RESPONSE,
  SOLICIT_RESPONSE,
  NOTIFICATION
}
