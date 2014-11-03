package vektah.rust.ide.module;

import com.intellij.ui.FieldPanel;

import javax.swing.*;
import java.awt.event.ActionListener;

class CommitableFieldPanel extends FieldPanel {
    private final Runnable commitRunnable;

    public CommitableFieldPanel(final JTextField textField,
                                String labelText,
                                final String viewerDialogTitle,
                                ActionListener browseButtonActionListener,
                                final Runnable documentListener,
                                final Runnable commitPathRunnable) {
        super(textField, labelText, viewerDialogTitle, browseButtonActionListener, documentListener);
        commitRunnable = commitPathRunnable;
    }

    public void commit() {
        commitRunnable.run();
    }
}