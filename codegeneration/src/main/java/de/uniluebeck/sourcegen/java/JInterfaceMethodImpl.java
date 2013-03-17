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
package de.uniluebeck.sourcegen.java;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.uniluebeck.sourcegen.exceptions.JDuplicateException;
import de.uniluebeck.sourcegen.exceptions.JInvalidModifierException;



class JInterfaceMethodImpl extends JElemImpl implements JInterfaceMethod {

	private static final ResourceBundle res = ResourceBundle.getBundle(JInterfaceMethodImpl.class.getCanonicalName());

	protected int modifiers;

	protected String name;

	protected String returnType;

	protected JMethodSignatureImpl signature;

	private ArrayList<String> exceptions = new ArrayList<String>();

	/**
	 * This method's list of Java annotations (e.g. Override).
	 */
	private List<JMethodAnnotation> annotations = new ArrayList<JMethodAnnotation>();

	/**
	 * This method's Javadoc comment.
	 */
	private JMethodComment comment = null;

	public JInterfaceMethodImpl(int modifiers, String returnType, String name,
			JMethodSignature signature, String[] exceptions) throws JDuplicateException,
			JInvalidModifierException {

		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
		this.signature = signature == null ? new JMethodSignatureImpl() : (JMethodSignatureImpl) signature;

		if (exceptions != null)
			for (String e : exceptions)
				addException(e);

		validateModifiers();

	}

  @Override
	public JInterfaceMethod add(JParameter... params) throws JDuplicateException {
		for (JParameter param : params)
			this.signature.add(param);
		return this;
	}

  @Override
	public JInterfaceMethod addException(String... exception) throws JDuplicateException {
		for (String e : exception)
			addExceptionInternal(e);
		return this;
	}

	private void addExceptionInternal(String exception) throws JDuplicateException {
		if (containsException(exception))
			throw new JDuplicateException(res.getString("exception.exceptions.duplicate"));
		this.exceptions.add(exception);
	}

  @Override
	public boolean containsException(String exception) {

		for (String e : exceptions)
			if (e.equals(exception))
				return true;

		return false;

	}

	/**
	 * @see de.uniluebeck.sourcegen.java.JInterfaceMethod#setComment(de.uniluebeck.sourcegen.java.JMethodComment)
	 */
  @Override
	public JInterfaceMethod setComment(JMethodComment comment) {
		this.comment = comment;
		return this;
	}

  @Override
  public JInterfaceMethod setComment(String comment) {
    this.comment = new JMethodCommentImpl(comment);
    return this;
  }

	/**
	 * @see de.uniluebeck.sourcegen.java.JInterfaceMethod#addAnnotation(de.uniluebeck.sourcegen.java.JMethodAnnotation[])
	 */
  @Override
	public JInterfaceMethod addAnnotation(JMethodAnnotation... annotations) {
	    for (JMethodAnnotation ann : annotations) {
	        this.annotations.add(ann);
	    }
	    return this;
	}

  @Override
  public JInterfaceMethod addAnnotation(String... annotations) {
      for (String annotation: annotations) {
          this.annotations.add(new JMethodAnnotationImpl(annotation));
      }

      return this;
  }

  @Override
	public boolean equals(JInterfaceMethod other) {
		return
			name.equals(((JInterfaceMethodImpl)other).name) &&
			signature.equals(((JInterfaceMethodImpl)other).signature);
	}

	public String getName() {
		return name;
	}

	protected void validateModifiers() throws JInvalidModifierException {

		// allowed:
		// 		public, abstract
		// unallowed:
		// 		final, interface, native,
		// 		private, protected, static,
		// 		strict, synchronized, transient,
		// 		volatile

		boolean invalid = JModifier.isFinal(modifiers)
				|| JModifier.isInterface(modifiers)
				|| JModifier.isNative(modifiers)
				|| JModifier.isPrivate(modifiers)
				|| JModifier.isProtected(modifiers)
				|| JModifier.isStatic(modifiers)
				|| JModifier.isStrict(modifiers)
				|| JModifier.isSynchronized(modifiers)
				|| JModifier.isTransient(modifiers)
				|| JModifier.isVolatile(modifiers);

		if (invalid)
			throw new JInvalidModifierException(res
					.getString("exception.modifier.invalid") + //$NON-NLS-1$
					JModifier.toString(modifiers));

	}

	/**
	 * @see de.uniluebeck.sourcegen.ElemImpl#toString(java.lang.StringBuffer, int)
	 */
	@Override
	public void toString(StringBuffer buffer, int tabCount) {

    // Write comment if necessary
    if (null != this.comment && !this.comment.isEmpty()) {
      comment.toString(buffer, tabCount);
    }

    // Write annotations if there are any
    for (JMethodAnnotation ann: this.annotations) {
      ann.toString(buffer, tabCount);
    }

		indent(buffer, tabCount);
		buffer.append(Modifier.toString(modifiers));
		buffer.append(" ");
		buffer.append(returnType);
		buffer.append(" ");
		buffer.append(name);
		signature.toString(buffer, 0);
		if (exceptions.size() > 0) {
			buffer.append(" throws ");
			for (String exception : exceptions) {
				buffer.append(exception);
				if (!exception.equals(exceptions.get(exceptions.size()-1)))
					buffer.append(", ");
			}
		}
		if (this instanceof JMethodImpl && !Modifier.isAbstract(modifiers)) {
			buffer.append(" {\n");
			((JMethodImpl)this).getBody().toString(buffer, tabCount+1);
			buffer.append("\n");
			indent(buffer, tabCount);
			buffer.append("}");
		}
		else
			buffer.append(";");
	}

	public static void main(String[] args) throws Exception {
		JMethodImpl method = new JMethodImpl(
				Modifier.PUBLIC | Modifier.STATIC,
				"String",
				"toString",
				JMethodSignature.factory.create(
						JParameter.factory.create(JModifier.NONE, "String", "testString"),
						JParameter.factory.create(Modifier.FINAL, "Exception", "e")
				),
				new String[] { "TestException", "Test2Exception" },
				"hello\n",
				"world"
		);
		System.out.print(method.toString(1));
	}

  @Override
  public int getModifiers() {
    return this.modifiers;
  }

  @Override
  public String getReturnType() {
    return this.returnType;
  }

  @Override
	public JMethodSignature getSignature() {
		return this.signature;
	}

}
