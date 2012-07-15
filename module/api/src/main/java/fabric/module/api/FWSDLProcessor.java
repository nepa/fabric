/** 15.07.2012 19:27 */
package fabric.module.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fabric.wsdlschemaparser.wsdl.FWSDL;

/**
 * The class can call various callback methods on a
 * FWSDLItemHandler object xto process all items of
 * the underlying WSDL document.
 *
 * @author seidel
 */
public class FWSDLProcessor
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(FWSDLProcessor.class);

  /**
   * Process all parts of the provided FWSDL object with
   * an instance of the FDefaultWSDLHandler. This method
   * will simply call the second process() function and
   * pass 'null' as last argument.
   *
   * @param wsdl Object containing all WSDL elements
   *
   * @throws Exception Error during processing
   */
  public void process(final FWSDL wsdl) throws Exception
  {
    this.process(wsdl, null);
  }

  /**
   * Process all parts of the provided FWSDL object with
   * the given FWSDLItemHandler instance. That is call
   * the methods processMessages(), processPortTypes(),
   * processBindings() and processServices() subsequently.
   * 
   * If the second argument is 'null', an instance of the
   * FDefaultWSDLHandler will be used as handler.
   *
   * @param wsdl Object containing all WSDL elements
   * @param handler Object to handle parts of WSDL document
   *
   * @throws Exception Error during processing
   */
  public void process(final FWSDL wsdl, final FWSDLItemHandler handler) throws Exception
  {
    // Use default handler, if none is provided
    FWSDLItemHandler wsdlHandler = handler;
    if (null == wsdlHandler)
    {
      wsdlHandler = new FDefaultWSDLHandler();
    }

    // Process WSDL messages
    if (null != wsdl.getMessages() && !wsdl.getMessages().isEmpty())
    {
      LOGGER.debug("Processing messages of WSDL document.");
      wsdlHandler.processMessages(wsdl.getMessages());
    }

    // Process WSDL port types
    if (null != wsdl.getPortTypes() && !wsdl.getPortTypes().isEmpty())
    {
      LOGGER.debug("Processing port types of WSDL document.");
      wsdlHandler.processPortTypes(wsdl.getPortTypes());
    }

    // Process WSDL bindings
    if (null != wsdl.getBindings() && !wsdl.getBindings().isEmpty())
    {
      LOGGER.debug("Processing bindings of WSDL document.");
      wsdlHandler.processBindings(wsdl.getBindings());
    }

    // Process WSDL services
    if (null != wsdl.getServices() && !wsdl.getServices().isEmpty())
    {
      LOGGER.debug("Processing services of WSDL document.");
      wsdlHandler.processServices(wsdl.getServices());
    }
  }
}
