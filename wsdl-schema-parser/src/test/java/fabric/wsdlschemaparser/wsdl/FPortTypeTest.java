/** 19.07.2012 11:24 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 * Unit test for FPortType and FPortTypeFactory class.
 *
 * @author seidel
 */
public class FPortTypeTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FPortType portType = new FPortTypeImpl("foobar");

    assertNotNull("FPortType object must not be null.", portType);
    assertEquals("Port type name must match initial value.", "foobar", portType.getPortTypeName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    FPortType portType = FPortType.factory.create("foobar");

    assertNotNull("FPortType object must not be null.", portType);
    assertEquals("Port type name must match initial value.", "foobar", portType.getPortTypeName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FPortType portType = FPortType.factory.create("foo");

    portType.setPortTypeName("bar");
    assertEquals("Port type name must match new value.", "bar", portType.getPortTypeName());
  }

  /**
   * Test methods for handling of webservice operations.
   */
  @Test(timeout = 1000)
  public void testOperationHandling()
  {
    FPortType portType = FPortType.factory.create("foo");

    // Stuff needed to create a FOperation object
    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);

    // Create two operations
    FOperation firstOperation = FOperation.factory.create("firstOperation", FOperationType.ONE_WAY, input, output);
    FOperation secondOperation = FOperation.factory.create("secondOperation", FOperationType.ONE_WAY, input, output);

    // Test addOperation() and operationCount()
    assertEquals("Operation count must be zero.", 0, portType.operationCount());
    portType.addOperation(firstOperation);
    assertEquals("Operation count must be one.", 1, portType.operationCount());
    portType.addOperation(secondOperation);
    assertEquals("Operation count must be two.", 2, portType.operationCount());

    // Reset port type object
    portType = FPortType.factory.create("bar");

    // Test addOperations()
    HashSet<FOperation> operations = new HashSet<FOperation>();
    operations.add(firstOperation);
    operations.add(secondOperation);
    assertEquals("Operation count must be zero.", 0, portType.operationCount());
    portType.addOperations(operations);
    assertEquals("Operation count must be two.", 2, portType.operationCount());

    // Reset port type object
    portType = FPortType.factory.create("foobar");

    // Test setOperations()
    assertEquals("Operations count must be zero.", 0, portType.operationCount());
    portType.setOperations(operations);
    assertEquals("Operations count must be two.", 2, portType.operationCount());

    // Test getOperations()
    Iterator iterator = portType.getOperations().iterator();
    while (iterator.hasNext())
    {
      FOperation operation = (FOperation)iterator.next();

      assertTrue("Port type must contain operation that was added previously.", operations.contains(operation));
    }
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    FPortType firstPortType = FPortType.factory.create("foo");
    FPortType secondPortType = FPortType.factory.create("bar");

    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);
    FOperation firstOperation = FOperation.factory.create("firstOperation", FOperationType.REQUEST_RESPONSE, input, output);
    FOperation secondOperation = FOperation.factory.create("secondOperation", FOperationType.REQUEST_RESPONSE, input, output);

    // Unequality
    firstPortType.addOperation(firstOperation);
    firstPortType.addOperation(secondOperation);
    assertFalse("Port type objects with different names and operations must not be equal.", firstPortType.equals(secondPortType));

    // Adding in different order should not affect equality!
    secondPortType.addOperation(secondOperation);
    secondPortType.addOperation(firstOperation);
    assertFalse("Port type objects with different names must not be equal.", firstPortType.equals(secondPortType));

    // Equality
    secondPortType.setPortTypeName("foo");
    assertTrue("Port type objects must be equal.", firstPortType.equals(secondPortType));
  }
}
