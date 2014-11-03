package org.intellij.rust.jps.builder;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

public class RustCompilerProcessAdapter extends ProcessAdapter {
    private final CompileContext myContext;
    private final String myBuilderName;
    private final String myCompileTargetRootPath;

    public RustCompilerProcessAdapter(@NotNull CompileContext context, @NotNull String builderName, @NotNull String compileTargetRootPath) {
        myContext = context;
        myBuilderName = builderName;
        myCompileTargetRootPath = compileTargetRootPath;
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, Key outputType) {
        // Todo: parse compiler output and look for errors, detect lines, etc.
        CompilerMessage msg = new CompilerMessage(
            myBuilderName, BuildMessage.Kind.ERROR, event.getText(),
            null, -1, -1, -1, -1, -1
        );

        myContext.processMessage(msg);
    }
}
