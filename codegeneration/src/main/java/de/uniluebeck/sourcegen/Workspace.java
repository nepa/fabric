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
package de.uniluebeck.sourcegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import de.uniluebeck.sourcegen.c.CHeaderFile;
import de.uniluebeck.sourcegen.c.CSourceFile;
import de.uniluebeck.sourcegen.c.CWorkspace;
import de.uniluebeck.sourcegen.c.CppHeaderFile;
import de.uniluebeck.sourcegen.c.CppSourceFile;
import de.uniluebeck.sourcegen.dot.DotGraphWorkspace;
import de.uniluebeck.sourcegen.java.JSourceFile;
import de.uniluebeck.sourcegen.java.JavaWorkspace;
import de.uniluebeck.sourcegen.js.JSSourceFile;
import de.uniluebeck.sourcegen.js.JavaScriptWorkspace;
import de.uniluebeck.sourcegen.protobuf.ProtobufWorkspace;

public class Workspace {

    // ###################################################################
    // Logging
    // ###################################################################

    private final org.slf4j.Logger log = LoggerFactory.getLogger(Workspace.class);

    // ###################################################################
    // Properties
    // ###################################################################

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    // ###################################################################
    // Java workspace
    // ###################################################################

    private JavaWorkspace java;

    public JavaWorkspace getJava() {
        return java;
    }

    // ###################################################################
    // C workspace
    // ###################################################################

    private CWorkspace c;

    public CWorkspace getC() {
        return c;
    }

    // ###################################################################
    // JavaScript workspace
    // ###################################################################

    private JavaScriptWorkspace javaScript;

    public JavaScriptWorkspace getJavaScript() {
        return javaScript;
    }

    // ###################################################################
    // Protobuf workspace
    // ###################################################################

    private ProtobufWorkspace protobuf;

    public ProtobufWorkspace getProtobuf() {
        return protobuf;
    }

    // ###################################################################
    // Dot workspace
    // ###################################################################

    /**
     * Helper for dot graph creation in this workspace.
     */
    private final DotGraphWorkspace dot;

    /**
     * Returns the helper for dot graph generation in this workspace.
     *
     * @return The dot graph helper instance.
     */
    public DotGraphWorkspace getDotHelper() {
        return this.dot;
    }

    // ###################################################################
    // SourceFiles
    // ###################################################################

    private final List<SourceFile> sourceFiles = new LinkedList<SourceFile>();

    public List<SourceFile> getSourceFiles() {
        return this.sourceFiles;
    }

    // ###################################################################
    // Workspace
    // ###################################################################

    public Workspace(Properties properties) {
        this.properties = properties;

        // Set up the workspaces
        this.java = new JavaWorkspace(this);
        this.c = new CWorkspace(this);
        this.javaScript = new JavaScriptWorkspace(this);
        this.protobuf = new ProtobufWorkspace(this);
        this.dot = new DotGraphWorkspace(this);
    }

    /**
     * Generates all source files
     *
     * @throws Exception
     */
    public void generate() throws Exception {
        log.info("Generating " + sourceFiles.size() + " source files.");

        jPackagePrefix = properties.getProperty(KEY_JAVA_PKG_PREFIX, "");

        long timeStart = (new Date()).getTime();

        for (SourceFile sourceFile : sourceFiles) {

            String dirString = getDirString(sourceFile);
            String fileString = getFileString(sourceFile);

            File dir = new File(dirString);
            File file = new File(dirString + fileString);

            assureDirExists(dir);
            assureFileExists(file);

            log.info("Generating file " + file.getAbsolutePath() + ".");

            StringBuffer buffer = new StringBuffer(sourceFile.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(buffer.toString() + "\n");
            log.debug("Sourcecode of " + sourceFile.getFileName() + ":\n");
            log.debug(buffer.toString());
            writer.close();
        }

        long timeEnd = (new Date()).getTime();
        log.info("Generated " + sourceFiles.size() + " source files in " + (timeEnd - timeStart) + " ms.");
    }

    // ###################################################################
    // OTHER STUFF
    // ###################################################################

    // TODO: Is that stuff really needed here?

    public static final String KEY_C_FILENAME = "c.filename";

    public static final String KEY_JAVA_PKG_PREFIX = "java.package";

    public static final String KEY_PROJECTDIR = "project.dir";

    public static final String KEY_PROJECTSUBDIR = "project.subdir";

    private String jPackagePrefix;

    public String getJPackagePrefix() {
        return jPackagePrefix;
    }

    private void assureDirExists(File dir) throws Exception {
      if (!dir.exists() && !dir.mkdirs()) {
        throw new Exception("File output directory could not be created.");
      }
    }

    private void assureFileExists(File file) throws Exception {
      if (!file.exists() && !file.createNewFile()) {
        throw new Exception("File could not be created.");
      }
    }

    private String getFileString(SourceFile sourceFile) {
        if (sourceFile instanceof JSourceFile)
            return sourceFile.getFileName() + ".java";
        if (sourceFile instanceof CHeaderFile)
            return sourceFile.getFileName() + ".h";
        if (sourceFile instanceof CSourceFile)
            return sourceFile.getFileName() + ".c";
        if (sourceFile instanceof CppHeaderFile)
            return sourceFile.getFileName() + ".hpp";
        if (sourceFile instanceof CppSourceFile)
            return sourceFile.getFileName() + ".cpp";
        if (sourceFile instanceof JSSourceFile)
            return sourceFile.getFileName() + ".js";
        return sourceFile.getFileName();
    }

    private String getDirString(SourceFile sourceFile) throws Exception {
        String projectDirString = properties.getProperty(KEY_PROJECTDIR, System.getProperty("user.dir"));

        // Use the desired output directory
        if (properties.containsKey("fabric.output_directory")) {
          projectDirString = properties.getProperty("fabric.output_directory");
        }

        String subDir = properties.getProperty(KEY_PROJECTSUBDIR, "");

        projectDirString = assureTrailingSeparator(projectDirString);
        projectDirString += subDir;

        if (sourceFile instanceof JSourceFile) {
            JSourceFile jSourceFile = (JSourceFile) sourceFile;
            projectDirString = assureTrailingSeparator(projectDirString);
            projectDirString += jPackagePrefix.replace('.', File.separatorChar);
            projectDirString = assureTrailingSeparator(projectDirString);

            if (null == jSourceFile.getPackageName())
            {
              throw new Exception(String.format("PackageName is null for source file '%s'. " +
                      "Maybe you did not set it correctly in your module?", jSourceFile.getFileName()));
            }
            else
            {
            	projectDirString += jSourceFile.getPackageName().replace('.', File.separatorChar);
            }
        }

        projectDirString = assureTrailingSeparator(projectDirString);
        return projectDirString;
    }

    private String assureTrailingSeparator(String s) {
        return s.endsWith(File.separator) ? s : s + File.separator;
    }
}
