package vektah.rust.ide.module;

import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RustOutputEditor extends ModuleElementsEditor {
    private final RustBuildElementsEditor buildElementsEditor;

    protected RustOutputEditor(final ModuleConfigurationState state) {
        super(state);
        buildElementsEditor = new RustBuildElementsEditor(state);
    }

    @Override
    protected JComponent createComponentImpl() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(UIUtil.PANEL_SMALL_INSETS));
        final GridBagConstraints gc =
                new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        panel.add(new CompilerOutputView(), gc);

        return panel;
    }

    @Override
    public void saveData() {


    }

    @Override
    public String getDisplayName() {
        return ProjectBundle.message("project.roots.path.tab.title");
    }


    @Override
    public void moduleStateChanged() {
        super.moduleStateChanged();
    }


    @Override
    public void moduleCompileOutputChanged(final String baseUrl, final String moduleName) {
        super.moduleCompileOutputChanged(baseUrl, moduleName);
    }

    @Override
    @Nullable
    @NonNls
    public String getHelpTopic() {
        return "projectStructure.modules.paths";
    }
}
