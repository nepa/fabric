/** 17.07.2012 13:14 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

import javax.xml.namespace.QName;

/**
 * This class represents a single binding in a WSDL document.
 * Each binding element binds an abstract port type (interface)
 * to a concrete message format and communication protocol. A
 * binding consists of a unique name and a QName value that
 * references the corresponding port type element (i.e. type
 * of the binding).
 *
 * Multiple bindings can be defined for a single port type,
 * e.g. to use different protocols to call a method.
 * 
 * @author seidel
 */
public class FBindingImpl extends FWSDLElement implements FBinding
{
  /** Name of the binding */
  private String bindingName;

  /** Reference to corresponding port type */
  private QName portTypeReference;

  /** Extensibility elements with per-binding information */
  private Set<FExtensibilityElement> perBindingInformations;

  /** Operations defined in referenced port type */
  private Set<FBindingOperation> operations;

  /**
   * Parameterized constructor creates a new binding with
   * the given name and type (port type reference).
   *
   * @param bindingName Name of the binding
   * @param portTypeReference QName of referenced port type
   */
  public FBindingImpl(final String bindingName, final QName portTypeReference)
  {
    this.bindingName = bindingName;
    this.portTypeReference = portTypeReference;
    this.perBindingInformations = new HashSet<FExtensibilityElement>();
    this.operations = new HashSet<FBindingOperation>();
  }

  /**
   * Set name of the port type binding.
   *
   * @param bindingName Name of the binding
   */
  @Override
  public void setBindingName(final String bindingName)
  {
    this.bindingName = bindingName;
  }

  /**
   * Get name of the port type binding.
   *
   * @return Name of the binding
   */
  @Override
  public String getBindingName()
  {
    return this.bindingName;
  }

  /**
   * Set QName of the referenced port type.
   *
   * @param portTypeReference QName of referenced port type
   */
  @Override
  public void setPortTypeReference(final QName portTypeReference)
  {
    this.portTypeReference = portTypeReference;
  }

  /**
   * Get QName of the referenced port type.
   *
   * @return QName of referenced port type
   */
  @Override
  public QName getPortTypeReference()
  {
    return this.portTypeReference;
  }

  /**
   * Add extensibility element with per-binding information.
   *
   * @param perBindingInformation Extensibility element with
   * per-binding information
   */
  @Override
  public void addPerBindingInformation(final FExtensibilityElement perBindingInformation)
  {
    this.perBindingInformations.add(perBindingInformation);
  }

  /**
   * Add a set of extensibility elements with per-binding
   * information.
   *
   * @param perBindingInformations Set of extensibility
   * elements with per-binding information
   */
  @Override
  public void addPerBindingInformations(final HashSet<FExtensibilityElement> perBindingInformations)
  {
    this.perBindingInformations.addAll(perBindingInformations);
  }

  /**
   * Set extensibility elements with per-binding information.
   *
   * @param perBindingInformations Set of extensibility
   * elements with per-binding information
   */
  @Override
  public void setPerBindingInformations(final HashSet<FExtensibilityElement> perBindingInformations)
  {
    this.perBindingInformations = perBindingInformations;
  }

  /**
   * Get extensibility elements with per-binding information.
   *
   * @return Set of extensibility elements with per-binding
   * information
   */
  @Override
  public HashSet<FExtensibilityElement> getPerBindingInformations()
  {
    return (HashSet<FExtensibilityElement>)this.perBindingInformations;
  }

  /**
   * Return the number of extensibility elements with
   * per-binding information that are defined within
   * the current binding object.
   *
   * @return Number of extensibility elements with
   * per-binding information
   */
  @Override
  public int perBindingInformationCount()
  {
    return this.perBindingInformations.size();
  }

  /**
   * Add a webservice operation to the port type binding.
   *
   * @param operation Service operation to add
   */
  @Override
  public void addBindingOperation(final FBindingOperation operation)
  {
    this.operations.add(operation);
  }

  /**
   * Add a set of webservice operations to the port type binding.
   *
   * @param operations Set of service operations to add
   */
  @Override
  public void addBindingOperations(final HashSet<FBindingOperation> operations)
  {
    this.operations.addAll(operations);
  }

  /**
   * Set webservice operations of the port type binding.
   *
   * @param operations Set of service operations
   */
  @Override
  public void setBindingOperations(final HashSet<FBindingOperation> operations)
  {
    this.operations = operations;
  }

  /**
   * Get a set of all methods that are defined within
   * the current port type binding.
   *
   * @return Set of webservice operations
   */
  @Override
  public HashSet<FBindingOperation> getBindingOperations()
  {
    return (HashSet<FBindingOperation>)this.operations;
  }

  /**
   * Return the number of operations that are defined
   * within the port type binding.
   *
   * @return Number of operations
   */
  @Override
  public int operationCount()
  {
    return this.operations.size();
  }

  /**
   * Print port type binding in a human-readable form. That
   * is the name of the binding, as well as its type (port
   * type reference) and a list of all operations that are
   * defined within the binding.
   *
   * @return String representation of FBindingImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    result += String.format("Binding: '%s' [Port type '%s']", this.bindingName,
            (null != this.portTypeReference ? this.portTypeReference.getLocalPart() : "undefined"));

    for (FBindingOperation operation: this.operations)
    {
      result += "\n\t" + operation.toString();
    }

    return result;
  }
}
