package org.intellij.rust.jps.builder;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.intellij.rust.jps.JpsRustModuleType;
import org.intellij.rust.jps.JpsRustSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.resources.ResourcesBuilder;
import org.jetbrains.jps.incremental.resources.StandardResourceBuilderEnabler;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class RustBuilder extends TargetBuilder<RustSourceRootDescriptor, RustTarget> {
    private final static Logger LOG = Logger.getInstance(RustBuilder.class);
    public static final String NAME = "rustc";

    public RustBuilder() {
        super(Arrays.asList(RustTargetType.PRODUCTION, RustTargetType.TESTS));

        //disables java resource builder for rust modules
        ResourcesBuilder.registerEnabler(new StandardResourceBuilderEnabler() {
            @Override
            public boolean isResourceProcessingEnabled(@NotNull JpsModule module) {
                return !(module.getModuleType() instanceof JpsRustModuleType);
            }
        });
    }

    @Override
    public void build(@NotNull RustTarget target,
                      @NotNull DirtyFilesHolder<RustSourceRootDescriptor, RustTarget> holder,
                      @NotNull BuildOutputConsumer outputConsumer,
                      @NotNull CompileContext context) throws ProjectBuildException, IOException
    {
        LOG.debug(target.getPresentableName());
        if (!holder.hasDirtyFiles() && !holder.hasRemovedFiles()) return;

        JpsModule module = target.getModule();
        File outputDirectory = getBuildOutputDirectory(module, target.isTests(), context);

        rustc(target, context, outputDirectory);
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return NAME;
    }

    private static void rustc(RustTarget target,
                              CompileContext context,
                              File outputDirectory) throws ProjectBuildException {
        GeneralCommandLine commandLine = getRustcCommandLine(target, context, outputDirectory);
        Process process;
        try {
            process = commandLine.createProcess();
        } catch (ExecutionException e) {
            throw new ProjectBuildException("Failed to launch rustc", e);
        }
        BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
        ProcessAdapter adapter = new RustCompilerProcessAdapter(context, NAME, "");
        handler.addProcessListener(adapter);
        handler.startNotify();
        handler.waitFor();
    }

    private static GeneralCommandLine getRustcCommandLine(RustTarget target,
                                                          CompileContext context,
                                                          File outputDirectory) throws ProjectBuildException {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        JpsModule module = target.getModule();
        JpsSdk<JpsDummyElement> sdk = getSdk(context, module);
        File executable = JpsRustSdkType.getByteCodeCompilerExecutable(sdk.getHomePath());

        commandLine.setWorkDirectory(outputDirectory);
        commandLine.setExePath(executable.getAbsolutePath());

        

        return commandLine;
    }


    @NotNull
    private static File getBuildOutputDirectory(@NotNull JpsModule module,
                                                boolean forTests,
                                                @NotNull CompileContext context) throws ProjectBuildException {
        JpsJavaExtensionService instance = JpsJavaExtensionService.getInstance();
        File outputDirectory = instance.getOutputDirectory(module, forTests);
        if (outputDirectory == null) {
            String errorMessage = "No output dir for module " + module.getName();
            context.processMessage(new CompilerMessage(NAME, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }
        if (!outputDirectory.exists()) {
            FileUtil.createDirectory(outputDirectory);
        }
        return outputDirectory;
    }

    @NotNull
    private static JpsSdk<JpsDummyElement> getSdk(@NotNull CompileContext context,
                                                  @NotNull JpsModule module) throws ProjectBuildException {
        JpsSdk<JpsDummyElement> sdk = module.getSdk(JpsRustSdkType.INSTANCE);
        if (sdk == null) {
            String errorMessage = "No SDK for module " + module.getName();
            context.processMessage(new CompilerMessage(NAME, BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }
        return sdk;
    }
}
