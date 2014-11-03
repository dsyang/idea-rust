package vektah.rust.ide.compilation;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import vektah.rust.RustFileType;
import vektah.rust.ide.sdk.RustSdkType;
import vektah.rust.ide.sdk.RustSdkUtil;

import java.nio.charset.Charset;

/**
 * Created by adam on 17/09/14.
 */
public class RustCompiler implements TranslatingCompiler {
    @NotNull
    @Override
    public String getDescription() {
        return "Rust compiler";
    }

    @Override
    public boolean validateConfiguration(CompileScope compileScope) {
        return true;
    }

    @Override
    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        String extension = file.getExtension();
        if (extension == null) {
            return false;
        }
        return extension.equals("rs") && file.getFileType() == RustFileType.INSTANCE;
    }

    @Override
    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink outputSink) {
        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setWorkDirectory(PathUtil.getParentPath(context.getProject().getProjectFilePath()));
        cmd.setPassParentEnvironment(true);

        for (Module module : moduleChunk.getNodes()) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

            VirtualFile outDir = context.getModuleOutputDirectory(module);
            if (outDir == null) {
                context.addMessage(CompilerMessageCategory.ERROR, "No output dir for module: " + module.getName(), null, -1, -1);
                return;
            }

            cmd.setWorkDirectory(outDir.getCanonicalPath());

            for (VirtualFile o: files) {
                String canonicalPath = o.getCanonicalPath();
                if (canonicalPath == null) continue;
                cmd.addParameter(canonicalPath);
            }

            Sdk sdk = moduleRootManager.getSdk();

            if (sdk == null) {
                context.addMessage(CompilerMessageCategory.ERROR, "No SDK for module: " + module.getName(), null, -1, -1);
                return;
            }

            if (sdk.getSdkType() != RustSdkType.getInstance()) {
                context.addMessage(CompilerMessageCategory.ERROR, "Not a Rust SDK for module: " + module.getName(), null, -1, -1);
                return;
            }

            String sdkHomePath = sdk.getHomePath();

            if (sdkHomePath == null) {
                context.addMessage(CompilerMessageCategory.ERROR, "No home path for Rust SDK: " + sdk.getName(), null, -1, -1);
            }

            cmd.setExePath(RustSdkUtil.getCompilerBinary(sdkHomePath).getAbsolutePath());

            ProgressIndicator progress = context.getProgressIndicator();
            progress.pushState();
            progress.setText("Compiling...");

            ProcessOutput output = null;
            try {
                output = new CapturingProcessHandler(cmd.createProcess(), Charset.defaultCharset(), cmd.getCommandLineString()).runProcess();
            } catch (ExecutionException e) {
                context.addMessage(CompilerMessageCategory.ERROR, "process throw exception: " + e.getMessage(), null, -1, -1);
            }

            if (output != null) {
                for (String error : output.getStdoutLines()) {
                    // Todo: parse rustc errors and identify lines and severity.
                    context.addMessage(CompilerMessageCategory.ERROR, error, null, -1, -1);
                }
            }

            progress.popState();
        }

    }
}
