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
package de.uniluebeck.sourcegen.java;

import de.uniluebeck.sourcegen.exceptions.JConflictingModifierException;
import de.uniluebeck.sourcegen.exceptions.JDuplicateException;
import de.uniluebeck.sourcegen.exceptions.JInvalidModifierException;

public interface JConstructor extends JLangElem {

    class JavaConstructorFactory {

        private static JavaConstructorFactory instance;

        static JavaConstructorFactory getInstance() {
            if (instance == null)
                instance = new JavaConstructorFactory();
            return instance;
        }

        private JavaConstructorFactory() { /* not to be invoked */ }

        public JConstructor create(int modifiers, String className, JMethodSignature signature,
                String[] exceptions, String... source) throws JDuplicateException, JConflictingModifierException, JInvalidModifierException {
            return new JConstructorImpl(modifiers, className, signature, exceptions, source);
        }

        public JConstructor create(int modifiers, String className, JMethodSignature signature)
                throws JDuplicateException, JConflictingModifierException, JInvalidModifierException {
            return new JConstructorImpl(modifiers, className, signature, null);
        }

        public JConstructor create(int modifiers, String className)
                throws JDuplicateException, JConflictingModifierException, JInvalidModifierException {
            return new JConstructorImpl(modifiers, className, null, null);
        }

    }

    public static final JavaConstructorFactory factory = JavaConstructorFactory.getInstance();

    public boolean equals(JConstructor other);

    public JConstructor addException(String... exceptions) throws JDuplicateException;
    public boolean containsException(String exception);

    public String getClassName();
    public JMethodSignature getSignature();
    public JMethodBody getBody();

    /**
     * Set the Javadoc comment for the current constructor.
     *
     * @param comment The Java constructor comment.
     * @return This object.
     */
    public JConstructor setComment(JConstructorComment comment);

    public JConstructor setComment(String comment);

    /**
     * Adds an annotation to this class.
     *
     * @param annotations The Java constructor's annotation.
     * @return This object.
     */
    public JConstructor addAnnotation(JConstructorAnnotation... annotations);

    public JConstructor addAnnotation(String... annotations);
}
