package vektah.rust.ide.module;

import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.InsertPathAction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;

public class CompilerOutputView extends JPanel {
    private JRadioButton inheritProjectCompileOutputRadioButton;
    private JRadioButton useModuleCompileOutputRadioButton;
    private JPanel rootPanel;
    private CommitableFieldPanel testOutputPath;
    private CommitableFieldPanel outputPath;

    public CompilerOutputView() {
        add(rootPanel);
    }

    private void createUIComponents() {
        outputPath = createFileInput("Output Path");
        testOutputPath = createFileInput("Test Output Path");
    }

    private CommitableFieldPanel createFileInput(String title) {
        final JTextField textField = new JTextField();
        final FileChooserDescriptor outputPathsChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();

        outputPathsChooserDescriptor.setHideIgnored(false);
        InsertPathAction.addTo(textField, outputPathsChooserDescriptor);
        FileChooserFactory.getInstance().installFileCompletion(textField, outputPathsChooserDescriptor, true, null);
        final Runnable commitRunnable = new Runnable() {
            @Override
            public void run() {

            }
        };

        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                commitRunnable.run();
            }
        });

        return new CommitableFieldPanel(textField, null, null, new BrowseFilesListener(textField, title, "", outputPathsChooserDescriptor) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                commitRunnable.run();
            }
        }, null, commitRunnable);
    }
}
