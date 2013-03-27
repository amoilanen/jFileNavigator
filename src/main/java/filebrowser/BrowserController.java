package filebrowser;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import filebrowser.entries.Entry;
import filebrowser.entries.EntryFactory;
import filebrowser.preview.Preview;
import filebrowser.preview.PreviewFactory;

public class BrowserController {
    
    private BrowserView view;
    
    private PreviewFactory previewFactory;
    
    private Entry currentEntry;
    
    public BrowserController(PreviewFactory previewFactory) {
        this.previewFactory = previewFactory;
    }
    
    private Entry getSelectedEntry() {
        int selectedIndex = view.getFileTable().getSelectionModel().getLeadSelectionIndex();
        int rowCount = view.getFileTable().getModel().getRowCount();
        
        if (selectedIndex >= 0 && selectedIndex < rowCount) {
            int modelRowIndex = view.getFileTable().convertRowIndexToModel(selectedIndex);

            return ((EntryTableModel)view.getFileTable().getModel()).getEntry(modelRowIndex);
        }
        return null;
    }

    private void navigateToEntry(Entry entry) {
        Entry navigationTarget = entry.navigationTarget();
        
        if (entry.isNavigationPossible() && (null != navigationTarget)) {
            view.getFileTable().getSelectionModel().clearSelection();
            ((EntryTableModel) view.getFileTable().getModel()).setRoot(navigationTarget);
            view.getFrame().setTitle(navigationTarget.getFullPath());
        }
    }
    
    private void previewEntry(Entry entry) {
        Preview preview = previewFactory.createPreview(view, entry);
        preview.show();
        view.getPreviewContainer().repaint();
    }
    
    @SuppressWarnings("serial")
    public JFrame createGUI() {
        view = new BrowserView().build();

        ListSelectionListener listSelectionListener = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                try {                    
                    Entry selectedEntry = getSelectedEntry();

                    /*
                     * Comparing with the current entry to avoid loading same resource multiple times.
                     * Event listener is called several times sometimes. Better (and correct) solution?
                     */
                    if ((null != selectedEntry) && (currentEntry != selectedEntry)) {
                        currentEntry = selectedEntry;
                        previewEntry(selectedEntry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        view.getFileTable().getSelectionModel().addListSelectionListener(listSelectionListener);
        view.getFileTable().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        view.getFileTable().getActionMap().put("Enter", new AbstractAction() {
            
            public void actionPerformed(ActionEvent event) {
            Entry selectedEntry = getSelectedEntry();
                
            if (null != selectedEntry) {
               navigateToEntry(getSelectedEntry());
            }
            }
        });
        view.getFileTable().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Entry selectedEntry = getSelectedEntry();
                    
                    if (null != selectedEntry) {                    
                        navigateToEntry(getSelectedEntry());
                    }
                }
            }
        });        
        
        view.getSetRootMenuItem().setAction(new SetRootAction());
        view.getExitMenuItem().setAction(new ExitAction());
        
        return view.getFrame();
    }

    @SuppressWarnings("serial")
    public class ExitAction extends AbstractAction {

        public ExitAction() {
            super(Localization.EXIT_MENUITEM_LABEL);
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
        }
        
        public void actionPerformed(ActionEvent e) {
            for (Frame frame : Frame.getFrames()) {
                if (frame.isActive()) {
                    WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                    frame.dispatchEvent(windowClosing);
                }
            }
        }
    }
    
    @SuppressWarnings("serial")
    public class SetRootAction extends AbstractAction {

        public SetRootAction() {
            super(Localization.SETROOT_MENUITEM_LABEL);
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        }
        
        public void actionPerformed(ActionEvent e) {
            final String pathToBrowse = (String) JOptionPane.showInputDialog(view.getFrame(),
                    Localization.BROWSE_DIALOG_MESSAGE, Localization.BROWSE_DIALOG_TITLE, JOptionPane.PLAIN_MESSAGE);

            //Creating entry in a separate thread as it can fail and be slow
            SwingWorker<Entry, Object> entryCreator = new SwingWorker<Entry, Object>() {

                @Override
                public Entry doInBackground() {
                    return EntryFactory.create(pathToBrowse);
                }

                @Override
                protected void done() {
                    Entry selectedEntry = null;
                    try {
                        selectedEntry = get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if ((null == selectedEntry) || !selectedEntry.isDirectory()) {
                        JOptionPane.showMessageDialog(view.getFrame(),
                                Localization.NO_ROOT_DIALOG_MESSAGE, Localization.NO_ROOT_DIALOG_TITLE,
                                JOptionPane.ERROR_MESSAGE);
                    }
                    navigateToEntry(selectedEntry);
                }
            };
            entryCreator.execute();
        }
    }
}