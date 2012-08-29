/**
 * Copyright (c) 2010-2012, Institute of Telematics (Dennis Pfisterer, Marco Wegner, Dennis Boldt,
 * Sascha Seidel, Joss Widderich, et al.), University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 *   - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/** 26.07.2012 15:05 */
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

    // Execute code before processing
    LOGGER.debug("Executing code before WSDL processing.");
    wsdlHandler.executeBeforeProcessing();

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

    // Execute code after processing
    LOGGER.debug("Executing code after WSDL processing.");
    wsdlHandler.executeAfterProcessing();
  }
}
