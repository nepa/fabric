/**
 * Copyright (c) 2010, Dennis Pfisterer, Marco Wegner, Institute of Telematics, University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fabric.module.api;

import java.util.ArrayList;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Basic module interface in Fabric.
 *
 * A module has a name and description as well and can supply default
 * values for any properties which are used to customise the behaviour
 * of this module.
 *
 * @author Marco Wegner
 */
public interface FModuleBase {

    /**
     * Returns this module's name.
     *
     * @return The module name.
     */
    public abstract String getName();

    /**
     * Returns this module's description.
     *
     * @return The module description.
     */
    public abstract String getDescription();
    
    /**
     * Creates and returns all handlers of this module, e.g. to
     * walk an XML Schema object tree or the elements of a WSDL
     * document.
     * 
     * @param workspace Workspace for code write-out
     * 
     * @return List of item handlers.
     * 
     * @throws Exception Error during handler instantiation
     */
    public abstract ArrayList<FItemHandlerBase> getHandlers(Workspace workspace) throws Exception;
}
