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

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.sourcegen.SourceFile;
import de.uniluebeck.sourcegen.Workspace;

public class CWorkspace {

    /** Logger object */
    private static final Logger LOGGER = LoggerFactory.getLogger(CWorkspace.class);

    // TODO Make sure that CFun has a working Comparator
    private Set<CFun> globalMethodStoreC = new HashSet<CFun>();

    private final List<SourceFile> sourceFiles;

    public CWorkspace(Workspace w) {
        this.sourceFiles = w.getSourceFiles();
    }

    public CppHeaderFile getCppHeaderFile(String fileName) {
        // Check if file is already existing and return instance if so
        for (SourceFile f : sourceFiles) {
            if (f instanceof CppHeaderFile && ((CppSourceFile) f).getFileName().equals(fileName)) {
                LOGGER.error(String.format("C++ header file '%s' exists already. Will return existing instance.", fileName));

                return (CppHeaderFile) f;
            }
        }

        // Create the new instance since it's not yet existing
        CppHeaderFileImpl file = new CppHeaderFileImpl(fileName);
        sourceFiles.add(file);
        LOGGER.info(String.format("Added new C++ header file '%s' to workspace.", fileName));

        return file;
    }

    public CppSourceFile getCppSourceFile(String fileName) {
        // Check if file is already existing and return instance if so
        for (SourceFile f : sourceFiles) {
            if (f instanceof CppSourceFile && !(f instanceof CppHeaderFile) &&
                    ((CppSourceFile) f).getFileName().equals(fileName)) {
                LOGGER.error(String.format("C++ source file '%s' exists already. Will return existing instance.", fileName));

                return (CppSourceFile) f;
            }
        }

        // Create the new instance since it's not yet existing
        CppSourceFile file = new CppSourceFileImpl(fileName);
        sourceFiles.add(file);
        LOGGER.info(String.format("Added new C++ source file '%s' to workspace.", fileName));

        return file;
    }

    public CHeaderFile getCHeaderFile(String fileName) {
        // Check if source file already existing and return instance if so
        for (SourceFile f : sourceFiles) {
            if (f instanceof CHeaderFile && f.getFileName().equals(fileName)) {
                LOGGER.error(String.format("C header file '%s' exists already. Will return existing instance.", fileName));

                return (CHeaderFile) f;
            }
        }

        // Create new instance since it's not yet existing
        CHeaderFileImpl header = new CHeaderFileImpl(fileName);
        sourceFiles.add(header);
        LOGGER.info(String.format("Added new C header file '%s' to workspace.", fileName));

        try {
            // adding header include guard (part 1)
            String guard = fileName.toUpperCase() + "_H";
            header.addBeforeDirective("ifndef " + guard);
            header.addBeforeDirective("define " + guard);

            // adding the extern "C" directive
            header.addBeforeDirective("if defined __cplusplus");
            header.addBeforeDirective(false, "extern \"C\" {");
            header.addBeforeDirective("endif");
            header.addAfterDirective("if defined __cplusplus");
            header.addAfterDirective(false, "}");
            header.addAfterDirective("endif");

            // belongs to the header include guard
            header.addAfterDirective("endif");
        }
        catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return header;
    }

    public CSourceFile getCSourceFile(String fileName) {
        // Check if source file already existing and return instance if so
        for (SourceFile f : sourceFiles) {
            if (f instanceof CSourceFile && !(f instanceof CHeaderFile) &&
                    ((CSourceFile) f).getFileName().equals(fileName)) {
                LOGGER.error(String.format("C source file '%s' exists already. Will return existing instance.", fileName));

                return (CSourceFile) f;
            }
        }

        // Create new instance since it's not yet existing
        CSourceFile file = new CSourceFileImpl(fileName);
        sourceFiles.add(file);
        LOGGER.info(String.format("Added new C source file '%s' to workspace.", fileName));

        return file;
    }

    /**
     * Delete a C++ header file from the workspace. The method
     * will return 'true' on success or 'false' if no file with
     * the given name was found.
     *
     * @param fileName Name of C++ header file
     *
     * @return True if file was deleted successfully, false otherwise
     */
    public boolean deleteCppHeaderFile(final String fileName) {
        boolean success = false;

        // Iterate all source files
        SourceFile sourceFile = null;
        Iterator<SourceFile> iterator = this.sourceFiles.iterator();
        while (iterator.hasNext()) {
            sourceFile = iterator.next();

            // Search C++ header files
            if (sourceFile instanceof CppHeaderFile) {
                CppHeaderFile cppHeaderFile = (CppHeaderFile)sourceFile;

                if (fileName.equals(cppHeaderFile.getFileName())) {
                    iterator.remove();
                    success = true;

                    LOGGER.info(String.format("Removed C++ header file '%s' from workspace.", cppHeaderFile.getFileName()));
                }
            }
        }

        if (!success) {
            LOGGER.info(String.format("Could not remove C++ header file '%s' from workspace. File does not exist.", fileName));
        }

        return success;
    }

    /**
     * Delete a C++ source file from the workspace. The method
     * will return 'true' on success or 'false' if no file with
     * the given name was found.
     *
     * @param fileName Name of C++ source file
     *
     * @return True if file was deleted successfully, false otherwise
     */
    public boolean deleteCppSourceFile(final String fileName) {
        boolean success = false;

        // Iterate all source files
        SourceFile sourceFile = null;
        Iterator<SourceFile> iterator = this.sourceFiles.iterator();
        while (iterator.hasNext()) {
            sourceFile = iterator.next();

            // Search C++ source files
            if (sourceFile instanceof CppSourceFile) {
                CppSourceFile cppSourceFile = (CppSourceFile)sourceFile;

                if (fileName.equals(cppSourceFile.getFileName())) {
                    iterator.remove();
                    success = true;

                    LOGGER.info(String.format("Removed C++ source file '%s' from workspace.", cppSourceFile.getFileName()));
                }
            }
        }

        if (!success) {
            LOGGER.info(String.format("Could not remove C++ source file '%s' from workspace. File does not exist.", fileName));
        }

        return success;
    }

    /**
     * Delete a C header file from the workspace. The method will
     * return 'true' on success or 'false' if no file with the
     * given name was found.
     *
     * @param fileName Name of C header file
     *
     * @return True if file was deleted successfully, false otherwise
     */
    public boolean deleteCHeaderFile(final String fileName) {
        boolean success = false;

        // Iterate all source files
        SourceFile sourceFile = null;
        Iterator<SourceFile> iterator = this.sourceFiles.iterator();
        while (iterator.hasNext()) {
            sourceFile = iterator.next();

            // Search C header files
            if (sourceFile instanceof CHeaderFile) {
                CHeaderFile cHeaderFile = (CHeaderFile)sourceFile;

                if (fileName.equals(cHeaderFile.getFileName())) {
                    iterator.remove();
                    success = true;

                    LOGGER.info(String.format("Removed C header file '%s' from workspace.", cHeaderFile.getFileName()));
                }
            }
        }

        if (!success) {
            LOGGER.info(String.format("Could not remove C header file '%s' from workspace. File does not exist.", fileName));
        }

        return success;
    }

    /**
     * Delete a C source file from the workspace. The method will
     * return 'true' on success or 'false' if no file with the
     * given name was found.
     *
     * @param fileName Name of C source file
     *
     * @return True if file was deleted successfully, false otherwise
     */
    public boolean deleteCSourceFile(final String fileName) {
        boolean success = false;

        // Iterate all source files
        SourceFile sourceFile = null;
        Iterator<SourceFile> iterator = this.sourceFiles.iterator();
        while (iterator.hasNext()) {
            sourceFile = iterator.next();

            // Search C source files
            if (sourceFile instanceof CSourceFile) {
                CSourceFile cSourceFile = (CSourceFile)sourceFile;

                if (fileName.equals(cSourceFile.getFileName())) {
                    iterator.remove();
                    success = true;

                    LOGGER.info(String.format("Removed C source file '%s' from workspace.", cSourceFile.getFileName()));
                }
            }
        }

        if (!success) {
            LOGGER.info(String.format("Could not remove C source file '%s' from workspace. File does not exist.", fileName));
        }

        return success;
    }

    public boolean containsCHeaderFile(String fileName) {
        for (SourceFile f : sourceFiles) {
            if (f instanceof CHeaderFile && f.getFileName().equals(fileName)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsCSourceFile(String fileName) {
        for (SourceFile f : sourceFiles) {
            if (f instanceof CSourceFile && !(f instanceof CHeaderFile) && f.getFileName().equals(fileName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Stores a new method in the store
     *
     * @param fun CFun object
     */
    public void setGlobalMethod(CFun fun) {
        globalMethodStoreC.add(fun);
    }

}
