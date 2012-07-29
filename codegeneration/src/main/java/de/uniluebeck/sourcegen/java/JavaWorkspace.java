package de.uniluebeck.sourcegen.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import de.uniluebeck.sourcegen.SourceFile;
import de.uniluebeck.sourcegen.Workspace;

public class JavaWorkspace {

    // TODO Make sure that JMethod has a working Comparator
    private Set<JMethod> globalMethodStoreJava = new HashSet<JMethod>();

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JavaWorkspace.class);
    private final List<SourceFile> sourceFiles;

    public JavaWorkspace(Workspace w) {
        this.sourceFiles = w.getSourceFiles();
    }

    public JSourceFile getJSourceFile(String packageName, String fileName) {
        // check if source file already exists
        for (SourceFile f : this.sourceFiles) {
            if (f instanceof JSourceFile && ((JSourceFile) f).getPackageName().equals(packageName) &&
                    ((JSourceFile) f).getFileName().equals(fileName)) {
                log.error(String.format("Source file '%s' already exists. Will return existing instance.", fileName));
                
                return (JSourceFile) f;
            }
        }
        
        JSourceFile f = new JSourceFileImpl(packageName, fileName);
        this.sourceFiles.add(f);
        log.info("Sourcefile " + fileName + " added to workspace");

        return f;
    }

    public boolean containsJavaClass(String clazz) {
        for (SourceFile f : sourceFiles) {
            if (f instanceof JSourceFile) {
                JSourceFile file = (JSourceFile) f;
                JClass jclazz = file.getClassByName(clazz);
                if (jclazz != null) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public void setGlobalMethod(JMethod method) {
            globalMethodStoreJava.add(method);
    }

}
