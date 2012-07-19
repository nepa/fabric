/** 19.07.2012 12:25 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * Unit test for FBinding and FBindingFactory class.
 *
 * @author seidel
 */
public class FBindingTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FBinding binding = new FBindingImpl("foobar", qName);

    assertNotNull("FBinding object must not be null.", binding);
    assertEquals("Binding name must match initial value.", "foobar", binding.getBindingName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FBinding binding = FBinding.factory.create("foobar", qName);

    assertNotNull("FBinding object must not be null.", binding);
    assertEquals("Binding name must match initial value.", "foobar", binding.getBindingName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    QName initialQName = new QName("namespaceURI", "localPart");
    QName newQName = new QName("namespaceURI", "newLocalPart");
    FBinding binding = FBinding.factory.create("foo", initialQName);

    binding.setBindingName("bar");
    assertEquals("Binding name must match new value.", "bar", binding.getBindingName());

    binding.setPortTypeReference(newQName);
    assertEquals("QName value of port type reference must match new value.",
            "newLocalPart", binding.getPortTypeReference().getLocalPart());
  }

  /**
   * Test methods for handling of per-binding information.
   */
  @Test(timeout = 1000)
  public void testPerBindingInformationHandling()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FBinding binding = FBinding.factory.create("foo", qName);

    FExtensibilityElement firstElement = new FExtensibilityElement(new UnknownExtensibilityElement());
    FExtensibilityElement secondElement = new FExtensibilityElement(new UnknownExtensibilityElement());

    // Test addPerBindingInformation()
    assertEquals("Per-binding information count must be zero.", 0, binding.perBindingInformationCount());
    binding.addPerBindingInformation(firstElement);
    assertEquals("Per-binding information count must be one.", 1, binding.perBindingInformationCount());
    binding.addPerBindingInformation(secondElement);
    assertEquals("Per-binding information count must be two.", 2, binding.perBindingInformationCount());

    // Reset binding object
    binding = FBinding.factory.create("bar", qName);

    // Test addPerBindingInformations()
    HashSet<FExtensibilityElement> elements = new HashSet<FExtensibilityElement>();
    elements.add(firstElement);
    elements.add(secondElement);
    assertEquals("Per-binding information count must be zero.", 0, binding.perBindingInformationCount());
    binding.addPerBindingInformations(elements);
    assertEquals("Per-binding information count must be two.", 2, binding.perBindingInformationCount());

    // Reset binding object
    binding = FBinding.factory.create("foobar", qName);

    // Test setPerBindingInformations()
    assertEquals("Per-binding information count must be zero.", 0, binding.perBindingInformationCount());
    binding.setPerBindingInformations(elements);
    assertEquals("Per-binding information count must be two.", 2, binding.perBindingInformationCount());

    // Test getPerBindingInformations()
    Iterator iterator = binding.getPerBindingInformations().iterator();
    while (iterator.hasNext())
    {
      FExtensibilityElement element = (FExtensibilityElement)iterator.next();

      assertTrue("Binding must contain per-binding information that was added previously.", elements.contains(element));
    }
  }

  /**
   * Test methods for handling of binding operations.
   */
  @Test(timeout = 1000)
  public void testBindingOperationHandling()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FBinding binding = FBinding.factory.create("foo", qName);

    // Stuff needed to create a FBindingOperation object
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");

    // Create two binding operations
    FBindingOperation firstBindingOperation = FBindingOperation.factory.create(
            "firstBindingOperation", FOperationType.ONE_WAY, input, output);
    FBindingOperation secondBindingOperation = FBindingOperation.factory.create(
            "secondBindingOperation", FOperationType.ONE_WAY, input, output);

    // Test addBindingOperation()
    assertEquals("Binding operation count must be zero.", 0, binding.operationCount());
    binding.addBindingOperation(firstBindingOperation);
    assertEquals("Binding operation count must be one.", 1, binding.operationCount());
    binding.addBindingOperation(secondBindingOperation);
    assertEquals("Binding operation count must be two.", 2, binding.operationCount());

    // Reset binding object
    binding = FBinding.factory.create("bar", qName);

    // Test addBindingOperations()
    HashSet<FBindingOperation> operations = new HashSet<FBindingOperation>();
    operations.add(firstBindingOperation);
    operations.add(secondBindingOperation);
    assertEquals("Binding operation count must be zero.", 0, binding.operationCount());
    binding.addBindingOperations(operations);
    assertEquals("Binding operation count must be two.", 2, binding.operationCount());

    // Reset binding object
    binding = FBinding.factory.create("foobar", qName);

    // Test setBindingOperations()
    assertEquals("Binding operation count must be zero.", 0, binding.operationCount());
    binding.setBindingOperations(operations);
    assertEquals("Binding operation count must be two.", 2, binding.operationCount());

    // Test getBindingOperations()
    Iterator iterator = binding.getBindingOperations().iterator();
    while (iterator.hasNext())
    {
      FBindingOperation bindingOperation = (FBindingOperation)iterator.next();

      assertTrue("Binding must contain binding operation that was added previously.", operations.contains(bindingOperation));
    }
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    QName firstQName = new QName("namespaceURI", "firstLocalPart");
    QName secondQName = new QName("namespaceURI", "secondLocalPart");
    FBinding firstBinding = FBinding.factory.create("firstBinding", firstQName);
    FBinding secondBinding = FBinding.factory.create("secondBinding", secondQName);

    // Unequality
    assertFalse("Binding objects with different names and QName values must not be equal.", firstBinding.equals(secondBinding));

    secondBinding.setPortTypeReference(firstQName);
    assertFalse("Binding objects with different QName values must not be equal.", firstBinding.equals(secondBinding));

    // Equality
    firstBinding.setBindingName("foobar");
    secondBinding.setBindingName("foobar");
    assertTrue("Binding objects must be equal.", firstBinding.equals(secondBinding));
  }
}
