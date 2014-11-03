package org.intellij.rust.jps;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

public class JpsRustSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
    public static final JpsRustSdkType INSTANCE = new JpsRustSdkType();

    @NotNull
    public static File getExecutable(@NotNull String path, @NotNull String command) {
        return new File(path, SystemInfo.isWindows ? command + ".exe" : command);
    }

    @NotNull
    public static File getByteCodeCompilerExecutable(@NotNull String sdkHome) {
        return getExecutable(new File(sdkHome).getAbsolutePath(), "rustc");
    }

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
        return JpsElementFactory.getInstance().createDummyElement();
    }
}
