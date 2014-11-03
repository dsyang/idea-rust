package org.intellij.rust.jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;
import java.io.FileFilter;

public class RustSourceRootDescriptor extends BuildRootDescriptor {
    private File root;
    private final RustTarget target;

    public RustSourceRootDescriptor(File root, RustTarget target) {
        this.root = root;
        this.target = target;
    }

    @Override
    public String getRootId() {
        return root.getAbsolutePath();
    }

    @Override
    public File getRootFile() {
        return root;
    }

    @Override
    public BuildTarget<?> getTarget() {
        return target;
    }

    @NotNull
    @Override
    public FileFilter createFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.endsWith(".rs");
            }
        };
    }
}
