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
package de.uniluebeck.sourcegen.protobuf.types;

public abstract class PAbstractField extends PAbstractElem {
	private String name;

	private boolean optional;

	private boolean required;

	private boolean repeated;

	private int uniqueNumberTag;

	public PAbstractField(String name, boolean optional, boolean required, boolean repeated, int uniqueNumberTag) {
		super();
		
		this.name = name;
		this.optional = optional;
		this.required = required;
		this.repeated = repeated;
		this.uniqueNumberTag = uniqueNumberTag;

		{//Ensure that only one of the flags is set
			int count = 0;

			if (isOptional())
				count++;

			if (isRepeated())
				count++;

			if (isRequired())
				count++;

			if (count > 1)
				throw new RuntimeException("More than one of optional" + isOptional() + ", repeated" + isRepeated()
						+ ", and required" + isRequired() + " is true.");
		}
		
	}

	public String getName() {
		return name;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isRepeated() {
		return repeated;
	}

	public int getUniqueNumberTag() {
		return uniqueNumberTag;
	}

}
