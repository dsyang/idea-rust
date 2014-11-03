package org.intellij.rust.jps.builder;

import org.intellij.rust.jps.JpsRustModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.ModuleBasedBuildTargetType;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.util.ArrayList;
import java.util.List;

public class RustTargetType extends ModuleBasedBuildTargetType<RustTarget> {
    public static final RustTargetType PRODUCTION = new RustTargetType("rust-production", false);
    public static final RustTargetType TESTS = new RustTargetType("rust-tests", true);
    private final boolean myTests;

    private RustTargetType(String rust, boolean tests) {
        super(rust);
        myTests = tests;
    }

    @NotNull
    @Override
    public List<RustTarget> computeAllTargets(@NotNull JpsModel model) {
        List<RustTarget> targets = new ArrayList<RustTarget>();
        for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsRustModuleType.INSTANCE)) {
            targets.add(new RustTarget(module, this));
        }
        return targets;
    }

    @NotNull
    @Override
    public BuildTargetLoader<RustTarget> createLoader(@NotNull final JpsModel model) {
        return new BuildTargetLoader<RustTarget>() {
            @Nullable
            @Override
            public RustTarget createTarget(@NotNull String targetId) {
                for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsRustModuleType.INSTANCE)) {
                    if (module.getName().equals(targetId)) {
                        return new RustTarget(module, RustTargetType.this);
                    }
                }
                return null;
            }
        };
    }

    public boolean isTests() {
        return myTests;
    }
}
