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
package de.uniluebeck.sourcegen.c;


class CppVarImpl extends CElemImpl implements CppVar {

  private Long visability = null;

  private String initCode;
  private String varName;
  private CppTypeGenerator typeGenerator;
  private CComment comment = null;

	public CppVarImpl(CppTypeGenerator type, String varName) {
		this.typeGenerator = type;
		this.varName = varName;
	}

	public CppVarImpl(long visibility, CppTypeGenerator type, String varName) {
		this(visibility, type, varName, null);
	}

	public CppVarImpl(long visibility, CppTypeGenerator type, String varName, String initCode) {
		this.typeGenerator = type;
		this.varName = varName;
		this.visability = visibility;
		this.initCode = initCode;
	}

  @Override
  public String getInit() {
    if (initCode == null) {
      return null;
    }

    // Initialize pointers with NULL; we will use
    // 0 here, because the NULL macro may not be
    // available everywhere
    if (typeGenerator.toString().endsWith("*")) {
      initCode = "0";
    }

    StringBuffer buffer = new StringBuffer();
    buffer.append(varName + "(" + initCode + ")");
    return buffer.toString();
  }

  @Override
  public void toString(StringBuffer buffer, int tabCount) {
    indent(buffer, tabCount);

    // Write comment if necessary
    if (null != this.comment && !this.comment.isEmpty()) {
      buffer.append(Cpp.newline);
      comment.toString(buffer, tabCount);
    }
    if (visability != null) {
      // Remove private, public and protected
      long vis = new Long(visability).longValue();
      if (Cpp.isPrivate(vis)) {
        vis = vis ^ Cpp.PRIVATE;
      }
    	if (Cpp.isPublic(vis)) {
        vis = vis ^ Cpp.PUBLIC;
      }
    	if (Cpp.isProtected(vis)) {
        vis = vis ^ Cpp.PROTECTED;
    	}

      String v = Cpp.toString(vis);
      if (v.length() > 0) {
        buffer.append(v + " ");
    	}
    }
    
    buffer.append(typeGenerator.toString() + " " + varName);
  }

  public Long getVisability() {
    return visability;
	}

  public String getTypeName() {
    return typeGenerator.toString();
  }

	public String getVarName() {
    return varName;
	}

  public String getInitCode() {
    return initCode;
	}

  @Override
	public CppVar setComment(CComment comment) {
		this.comment = comment;
		return this;
  }

  @Override
	public CppVar setComment(String comment) {
    return this.setComment(new CCommentImpl(comment));
  }

  /**
   * returns OUTER::NESTED1::NESTED2::...::NESTEDN
   *
   * @return
   */
/*
  private String getParents(){
    StringBuffer myParents = new StringBuffer();
    if(this.clazz != null) {
      for (CppClass p : this.clazz.getParents()) {
        myParents.append(p.getTypeName()+ "::");
    }
    }
    return myParents.toString();
  }
*/
/*	@Override
	public CppVar setClass(CppClass clazz) {
		this.clazz = clazz;
		return this;
	}
*/

}
