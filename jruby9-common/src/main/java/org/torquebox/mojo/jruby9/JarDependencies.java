package org.torquebox.mojo.jruby9;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;

public class JarDependencies {

    public static interface Filter {
        boolean addIt(Artifact a);
    }

    static final Filter ALL_FILTER = new Filter() {

        @Override
        public boolean addIt(Artifact a) {
            return true;
        }
        
    };

    private final Set<String> coordinates = new TreeSet<>();
    private final List<Artifact> artifacts = new LinkedList<Artifact>();
    private final File jarsLockFile;
    private final File target;
    
    public JarDependencies(String target, String jarsLockFilename) {
        this(new File(target), jarsLockFilename);
    }

    public JarDependencies(File target, String jarsLockFilename) {
        this.jarsLockFile = new File(target, jarsLockFilename);
        this.target = new File(target, "jars");
    }

    public String lockFilePath() {
        return jarsLockFile.getAbsolutePath();
    }

    public void add(Artifact a) {
        String coordiante = a.getGroupId() + ":" + a.getArtifactId() + (a.hasClassifier() ? ":" + a.getClassifier() : "");
        // first comes first and not duplicates
        if (a.getType().equals("jar") && !coordinates.contains(coordiante)) {
            coordinates.add(coordiante);
            artifacts.add(a);
        }
    }
 
    public void addAll(Collection<Artifact> artifacts) {
        addAll(artifacts, ALL_FILTER);
    }
    
    public void addAll(Collection<Artifact> artifacts, Filter filter) {
        for(Artifact a: artifacts) {
            if (filter.addIt(a)) {
                add(a);
            }
        }
    }

    public void generateJarsLock() throws IOException {
        jarsLockFile.getParentFile().mkdirs();
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(jarsLockFile)))) {
            for(Artifact a: artifacts) {
                out.print(a);
                out.println(":");
            }
        }
    }
    
    public void copyJars() throws IOException {
        for(Artifact a: artifacts) {
            if (!a.getScope().equals("system")) {
                File targetFile = new File(target, a.getGroupId().replace('.', '/') + "/" +
                                                   a.getArtifactId() + "/" +
                                                   a.getVersion() + "/" +
                                                   a.getArtifactId() + "-" + a.getVersion() + "." + a.getType());
                FileUtils.copyFile(a.getFile(), targetFile);
            }
        }
    }
}