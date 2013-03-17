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
package de.uniluebeck.sourcegen.c;

import java.util.List;
import java.util.LinkedList;

import de.uniluebeck.sourcegen.exceptions.CppDuplicateException;

/**
 * @author Dennis Boldt
 */
class CppNamespaceImpl extends CElemImpl implements CppNamespace {

    private String name;
    private CComment comment = null;

    private List<CFun> cfuns = new LinkedList<CFun>();
    private List<CppClass> classes = new LinkedList<CppClass>();
    // TODO: Add support for nested namespaces
    //private List<CppNamespace> namespaces = new LinkedList<CppNamespace>();

    // TODO: Add support for nested namespaces
    // Needed for nested namespaces
    private List<String> parents = new LinkedList<String>();

    boolean isPrepared = false;

    public CppNamespaceImpl(String name) {
        this.name = name;
    }

    public CppNamespace add(long vis, CFun... functions) throws CppDuplicateException {
        for (CFun f : functions) {
            if (this.contains(f)) {
                throw new CppDuplicateException("Function " + f.getName() + " already exists.");
            }
            this.cfuns.add(f);
        }
        return this;
    }

    @Override
    public CppNamespace add(CppClass... cppClasses) throws CppDuplicateException {
        for (CppClass c : cppClasses) {
            if (this.contains(c)) {
                throw new CppDuplicateException("Class " + c.getName() + " already exists.");
            }
            this.classes.add(c);
        }
        return this;
    }

    public boolean contains(CFun fun) {
        for (CFun c : this.cfuns) {
            if (c.getName().equals(fun.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(CppClass cppClass) {
        for (CppClass c : this.classes) {
            if (c.getName().equals(cppClass.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<CFun> getFuns() {
        return this.cfuns;
    }

    @Override
    public List<CppClass> getClasses() {
        return this.classes;
    }

    @Override
    public void toString(StringBuffer buffer, int tabCount) {
        prepare();

        // Write comment if necessary
        if (null != this.comment && !this.comment.isEmpty()) {
            comment.toString(buffer, tabCount);
        }

        buffer.append("namespace " + this.name);
        buffer.append(Cpp.newline + "{" + Cpp.newline);


        if (null != cfuns && cfuns.size() > 0) {
            StringBuffer inner = new StringBuffer();
            // Add signatures of C functions
            for (CFun fun : cfuns) {
                if (null != fun.getComment()) {
                    inner.append(fun.getComment().toString());
                }
                inner.append(fun.getSignature() + ";" + Cpp.newline + Cpp.newline);
            }
            appendBody(buffer, inner, tabCount + 1);
            buffer.append(Cpp.newline + Cpp.newline);
        }

        if (null != classes && classes.size() > 0) {
            StringBuffer inner = new StringBuffer();
            // FIXME: A tab to much
            for (CppClass c : classes) {
                inner.append(c.toString() + Cpp.newline + Cpp.newline);
            }
            appendBody(buffer, inner, tabCount + 1);
        }
        buffer.append(Cpp.newline + "};" + Cpp.newline);
    }

    protected void indent(StringBuffer buffer, int tabCount) {
        for (int i = 0; i < tabCount; i++)
            buffer.append("\t");
    }

/* TODO
    @Override
    public CppNamespace addParents(List<CppNamespace> cppClass, CppNamespace clazz) {
        this.parents.addAll(cppClass);
        this.parents.add(clazz);
        return this;
    }

    public List<CppNamespace> getParents() {
        return parents;
    }
*/
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CppNamespace setComment(CComment comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public CppNamespace setComment(String comment) {
        return this.setComment(new CCommentImpl(comment));
    }

    /**
     * This method prepares the files
     */
    @Override
    public void prepare() {
        if (isPrepared)
            return;

        for (CFun f : this.cfuns) {
            f.addParents(this.parents, this.getName());
        }

        for (CppClass c : this.classes) {
            c.addParents(this.parents, this.getName());
            c.prepare();
        }

        isPrepared = true;
    }
}
