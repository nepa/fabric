/**
 * Copyright (c) 2010-2013, Institute of Telematics (Dennis Pfisterer, Marco Wegner, Dennis Boldt,
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
/** 26.07.2012 14:42 */
package fabric.module.api;

import java.util.HashSet;

import fabric.wsdlschemaparser.wsdl.FMessage;
import fabric.wsdlschemaparser.wsdl.FPortType;
import fabric.wsdlschemaparser.wsdl.FBinding;
import fabric.wsdlschemaparser.wsdl.FService;

/**
 * Public interface to handle items of a WSDL document. This
 * handler provides various callback methods that are called
 * one after another to process all parts of a WSDL document.
 *
 * Elements are processed in this order:
 *   - Messages
 *   - Port types (WSDL 2.0: Interfaces)
 *   - Bindings
 *   - Ports (WSDL 2.0: Endpoints)
 *   - Services
 *
 * Types that are defined as inline XML Schema documents are
 * not processed by this handler. They will be handled by
 * implementations of the FSchemaTreeItemHandler interface.
 *
 * Furthermore, the interface provides two callback methods
 * to execute code before and after processing the elements
 * of the WSDL document:
 *   - executeBeforeProcessing()
 *   - executeAfterProcessing()
 *
 * @author seidel
 */
public interface FWSDLItemHandler extends FItemHandlerBase
{
  /**
   * Execute code before processing elements of WSDL document.
   *
   * @throws Exception Error during execution
   */
  public abstract void executeBeforeProcessing() throws Exception;

  /**
   * Execute code after processing elements of WSDL document.
   *
   * @throws Exception Error during execution
   */
  public abstract void executeAfterProcessing() throws Exception;

  /**
   * Process messages that are defined in WSDL document.
   *
   * @param messages Messages of WSDL document
   *
   * @throws Exception Error during processing
   */
  public abstract void processMessages(final HashSet<FMessage> messages) throws Exception;

  /**
   * Process port types (WSDL 2.0: interfaces) that are defined in WSDL document.
   *
   * @param portTypes Port types of WSDL document
   *
   * @throws Exception Error during processing
   */
  public abstract void processPortTypes(final HashSet<FPortType> portTypes) throws Exception;

  /**
   * Process bindings that are defined in WSDL document.
   *
   * @param bindings Bindings of WSDL document
   *
   * @throws Exception Error during processing
   */
  public abstract void processBindings(final HashSet<FBinding> bindings) throws Exception;

  /**
   * Process services that are defined in WSDL document.
   *
   * @param services Services of WSDL document
   *
   * @throws Exception Error during processing
   */
  public abstract void processServices(final HashSet<FService> services) throws Exception;
}
