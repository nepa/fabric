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
/** 26.07.2012 14:37 */
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
  public void executeBeforeProcessing() throws Exception
  {
    // Nothing to do
  }

  @Override
  public void executeAfterProcessing() throws Exception
  {
    // Nothing to do
  }

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
