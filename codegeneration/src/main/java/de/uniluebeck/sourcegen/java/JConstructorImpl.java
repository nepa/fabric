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

import de.uniluebeck.sourcegen.exceptions.JConflictingModifierException;
import de.uniluebeck.sourcegen.exceptions.JDuplicateException;
import de.uniluebeck.sourcegen.exceptions.JInvalidModifierException;

class JConstructorImpl extends JElemImpl implements JConstructor {

    private static final ResourceBundle res = ResourceBundle.getBundle(JConstructorImpl.class.getCanonicalName());

    private int modifiers;

    private String className;

    private JMethodSignatureImpl signature;

    private ArrayList<String> exceptions = new ArrayList<String>();

    private JMethodBodyImpl body;

    private JConstructorComment comment = null;

    /**
     * This constructor's list of Java annotations.
     */
    private List<JConstructorAnnotation> annotations = new ArrayList<JConstructorAnnotation>();

    public JConstructorImpl(int modifiers, String className, JMethodSignature signature, String[] exceptions, String... source)
            throws JDuplicateException, JConflictingModifierException, JInvalidModifierException {

        this.modifiers = modifiers;
        this.className = className;
        this.signature = (signature != null) ? (JMethodSignatureImpl) signature : JMethodSignature.factory.createEmptySignature();
        this.body = (source != null) ? new JMethodBodyImpl(source) : JMethodBody.factory.createEmpty();

        if (null != exceptions) {
            for (String e: exceptions) {
                this.addException(e);
            }
        }

        validateModifiers();

    }

    @Override
    public boolean equals(JConstructor other) {
        return className.equals(((JConstructorImpl)other).className) &&
                  signature.equals(((JConstructorImpl)other).signature);
    }

    @Override
    public JConstructor addException(String... exceptions) throws JDuplicateException {
        for (String e: exceptions) {
            this.addExceptionInternal(e);
        }

        return this;
    }

    private void addExceptionInternal(String exception) throws JDuplicateException {
        if (this.containsException(exception)) {
            throw new JDuplicateException(res.getString("exception.exceptions.duplicate"));
        }

        this.exceptions.add(exception);
    }

    @Override
    public boolean containsException(String exception) {
        for (String e: this.exceptions) {
            if (e.equals(exception)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public JMethodSignature getSignature() {
        return signature;
    }

    @Override
    public JMethodBody getBody() {
        return body;
    }

    private void validateModifiers() throws JInvalidModifierException, JConflictingModifierException {

        // allowed:
        //		public, protected, private
        // unallowed:
        // 		abstract, final, interface
        // 		native, static, strict,
        // 		synchronized, transient, volatile

        boolean invalid =
                JModifier.isAbstract(modifiers) ||
                        JModifier.isFinal(modifiers) ||
                        JModifier.isInterface(modifiers) ||
                        JModifier.isNative(modifiers) ||
                        JModifier.isStatic(modifiers) ||
                        JModifier.isStrict(modifiers) ||
                        JModifier.isSynchronized(modifiers) ||
                        JModifier.isTransient(modifiers) ||
                        JModifier.isVolatile(modifiers);

        if (invalid)
            throw new JInvalidModifierException(
                    res.getString("exception.modifier.invalid") + //$NON-NLS-1$
                            JModifier.toString(modifiers)
            );

        if (JModifier.isConflict(modifiers))
            throw new JConflictingModifierException(
                    res.getString("exception.modifier.conflict") //$NON-NLS-1$
            );

    }

    /**
     * @see de.uniluebeck.sourcegen.JConstructor#setComment(de.uniluebeck.sourcegen.JConstructorComment)
     */
    @Override
    public JConstructor setComment(JConstructorComment comment)
    {
        this.comment = comment;
        return this;
    }
    
    /**
     * @see de.uniluebeck.sourcegen.java.JConstructor#addAnnotation(de.uniluebeck.sourcegen.java.JConstructorAnnotation[])
     */
    @Override
    public JConstructor addAnnotation(JConstructorAnnotation... annotations) {
        for (JConstructorAnnotation ann : annotations) {
            this.annotations.add(ann);
        }
        return this;
    }

    @Override
    public JConstructor addAnnotation(String... annotations) {
        for (String annotation: annotations) {
            this.annotations.add(new JConstructorAnnotationImpl(annotation));
        }

        return this;
    }

    @Override
    public void toString(StringBuffer buffer, int tabCount) {

        // write comment if necessary
        if (comment != null) {
            comment.toString(buffer, tabCount);
        }

        // write annotations if there are any
        for (JConstructorAnnotation ann : this.annotations) {
            ann.toString(buffer, tabCount);
        }

        if (toStringModifiers(buffer, tabCount, modifiers))
            buffer.append(" ");
        buffer.append(className);
        signature.toString(buffer, 0);

        if (this.exceptions.size() > 0) {
            buffer.append(" throws ");

            for (String e: this.exceptions) {
                buffer.append(e);

                if (!e.equals(this.exceptions.get(this.exceptions.size() - 1))) {
                    buffer.append(", ");
                }
            }
        }

        buffer.append(" {\n");
        body.toString(buffer, tabCount + 1);
        buffer.append("\n");
        indent(buffer, tabCount);
        buffer.append("}");
    }

    public static void main(String[] args) throws Exception {
        JConstructor constructor = JConstructor.factory.create(
                Modifier.PRIVATE,
                "FooClass",
                JMethodSignature.factory.create(
                    JParameter.factory.create(JModifier.NONE, "String", "barParam")),
                new String[] { "TestException", "Test2Exception" },
                "System.out.println(\"Hello World!\");\n" +
                "System.out.println(barParam);"
        );
        System.out.println(constructor.toString(1));

        JConstructor constructor2 = JConstructor.factory.create(
                Modifier.PRIVATE,
                "FooClass",
                JMethodSignature.factory.create(
                    JParameter.factory.create(JModifier.NONE, "String", "barParam")),
                new String[] { "TestException", "Test2Exception" }
        );
        System.out.println(constructor2.toString(1));

        JConstructor constructor3 = JConstructor.factory.create(
                Modifier.PRIVATE,
                "FooClass",
                JMethodSignature.factory.create(
                    JParameter.factory.create(JModifier.NONE, "String", "barParam"))
        );
        System.out.println(constructor3.toString(1));

        JConstructor constructor4 = JConstructor.factory.create(
                Modifier.PRIVATE,
                "FooClass"
        );
        System.out.println(constructor4.toString(1));
    }

}
