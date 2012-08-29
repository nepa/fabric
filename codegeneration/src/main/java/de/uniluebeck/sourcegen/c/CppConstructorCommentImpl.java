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
package de.uniluebeck.sourcegen.c;

import java.util.Map;
import java.util.TreeMap;


/**
 * @author Dennis Boldt
 */
public class CppConstructorCommentImpl extends CCommentImpl implements CppConstructorComment {

	/**
	 * Parameters mapped to their descriptions.
	 */
	private Map<String,String> params = new TreeMap<String, String>( );

	/**
	 * Generate a Javadoc constructor comment with specified description.
	 *
	 * @param description The actual constructor comment description.
	 */
	public CppConstructorCommentImpl(String description) {
		super(description);
	}

	/**
	 * Adds a parameter to the list of method parameters.
	 *
	 * @param name The parameter's name.
	 * @param description The parameter's description.
	 */
	public void addParameter(String name, String description) {
		this.params.put(name, description);
	}

	public void addParameter(CppVar type, String description) {
		this.params.put(type.getVarName(), description);
	}

	/**
	 * @return the params
	 */
	public Map<String, String> getParameters() {
		return params;
	}

	/* (non-Javadoc)
	 * @see de.uniluebeck.sourcegen.ElemImpl#toString(java.lang.StringBuffer, int)
	 */
	@Override
	public void toString(StringBuffer buffer, int tabCount) {
		//indent(buffer, tabCount);
		buffer.append("/**" + Cpp.newline);

		addDescriptionComment(buffer, tabCount);

		if (!params.isEmpty()) {
			buffer.append(" *" + Cpp.newline);
			addParameterComments(buffer, tabCount);
		}

		//indent(buffer, tabCount);
		buffer.append(" */" + Cpp.newline);
	}

	/**
	 * @param buffer
	 * @param tabCount
	 */
	protected void addDescriptionComment(StringBuffer buffer, int tabCount) {
		buffer.append(" * ").append(this.getDescription()).append(Cpp.newline);
	}

	/**
	 * @param buffer
	 * @param tabCount
	 */
	protected void addParameterComments(StringBuffer buffer, int tabCount) {
		for (String key : params.keySet()) {
			buffer.append(" * @param ").append(key).append(" ").append(params.get(key)).append(Cpp.newline);
		}
	}
}
