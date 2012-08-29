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
/**
 *
 */
package fabric.wsdlschemaparser.schema;

import org.apache.xmlbeans.SchemaType;



/**
 * This class represents the Fabric variation of a normalized string,
 * as specified by xs:normalizedString in XML Schema.
 *
 * @author Richard Mietz
 */
public class FNormalizedString extends FString {

	public static final int MAX_LENGTH = 63;

	/**
	 *
	 */
	public FNormalizedString( ) {
		this(null);
	}

	// --------------------------------------------------------------------

	/**
	 * Creates a normalized string having a type name.
	 *
	 * @param typeName This normalized string's name.
	 */
	public FNormalizedString(String typeName) {
		super(typeName);
                addRestriction(SchemaType.FACET_MAX_LENGTH, MAX_LENGTH);
	}
}
