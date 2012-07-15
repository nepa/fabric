/** 14.07.2012 20:35 */
package fabric.module.api;

import java.util.HashSet;

import fabric.wsdlschemaparser.wsdl.FMessage;
import fabric.wsdlschemaparser.wsdl.FPortType;
import fabric.wsdlschemaparser.wsdl.FBinding;
import fabric.wsdlschemaparser.wsdl.FService;

/**
 * Default handler to process items of a WSDL document.
 * This implementation actually does not do anything.
 *
 * @author seidel
 */
public class FDefaultWSDLHandler implements FWSDLItemHandler
{
  @Override
  public void processMessages(final HashSet<FMessage> messages) throws Exception
  {
    // Nothing to do
  }

  @Override
  public void processPortTypes(final HashSet<FPortType> portTypes) throws Exception
  {
    // Nothing to do
  }

  @Override
  public void processBindings(final HashSet<FBinding> bindings) throws Exception
  {
    // Nothing to do
  }

  @Override
  public void processServices(final HashSet<FService> services) throws Exception
  {
    // Nothing to do
  }
}
