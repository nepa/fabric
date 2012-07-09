/** 08.07.2012 00:25 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a single port type element of
 * a WSDL document (WSDL 2.0: interface element). Each
 * port type consists of a unique name and a set of
 * webservice operations.
 * 
 * @author seidel
 */
public class FPortTypeImpl extends FWSDLElement implements FPortType
{
  /** Name of the port type (interface) */
  private String portTypeName;

  /** Set of webservice operations */
  private Set<FOperation> operations;

  /**
   * Parameterized constructor creates a new port type
   * (interface) with the given name.
   *
   * @param portTypeName Name of the service interface
   */
  public FPortTypeImpl(final String portTypeName)
  {
    this.portTypeName = portTypeName;
    this.operations = new HashSet<FOperation>();
  }

  /**
   * Set name of the port type (interface).
   *
   * @param portTypeName Name of the service interface
   */
  @Override
  public void setPortTypeName(final String portTypeName)
  {
    this.portTypeName = portTypeName;
  }

  /**
   * Get name of the port type (interface).
   *
   * @return Name of the service interface
   */
  @Override
  public String getPortTypeName()
  {
    return this.portTypeName;
  }

  /**
   * Add a webservice operation to the service interface.
   *
   * @param operation Service operation to add
   */
  @Override
  public void addOperation(final FOperation operation)
  {
    this.operations.add(operation);
  }

  /**
   * Add a set of webservice operations to the service
   * interface.
   *
   * @param operations Set of service operations to add
   */
  @Override
  public void addOperations(final HashSet<FOperation> operations)
  {
    this.operations.addAll(operations);
  }

  /**
   * Set webservice methods of the port type (interface).
   *
   * @param operations Set of service operations
   */
  @Override
  public void setOperations(final HashSet<FOperation> operations)
  {
    this.operations = operations;
  }

  /**
   * Get a set of all methods that are defined within
   * the current webservice port type (interface).
   *
   * @return Set of webservice operations
   */
  @Override
  public HashSet<FOperation> getOperations()
  {
    return (HashSet<FOperation>)this.operations;
  }

  /**
   * Return the number of operations that are defined
   * within the webservice port type (interface).
   * 
   * @return Number of operations
   */
  @Override
  public int operationCount()
  {
    return this.operations.size();
  }

  /**
   * Print webservice interface (port type) in a human-readable
   * form. That is the name of the interface, as well as a list
   * of all methods that are defined within the port type.
   *
   * @return String representation of FPortTypeImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    result += String.format("Interface (port type): '%s'", this.portTypeName);

    for (FOperation operation: this.operations)
    {
      result += "\n\t" + operation.toString();
    }

    return result;
  }
}
