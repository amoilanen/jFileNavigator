package filebrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import filebrowser.entries.Entry;
import filebrowser.entries.FileSystemEntry;

public class BrowserView implements PreviewView {
    
    private JFrame frame;

    private JTable table;
    
    private JPanel previewContainer;
    
    private JTextArea textPreview;
    
    private JScrollPane textPreviewScrollPane;
    
    private ImagePanel imagePreview;

    private JLabel noPreviewLabel;
    
    private JMenuItem setRootMenuItem;

    private JMenuItem exitMenuItem;
    
    public BrowserView() {
    }
    
    public BrowserView build() {
        frame = new JFrame(Localization.FILE_BROWSER_APP_NAME);
        frame.setLayout(new BorderLayout(8, 8));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setJMenuBar(createMenuBar());
        
        
        frame.add(createFileList(), BorderLayout.WEST);
        frame.add(createPreview(), BorderLayout.EAST);
        return this;
    }
    
    public void hidePreviews() {
        noPreviewLabel.setVisible(false);
        textPreviewScrollPane.setVisible(false);
        imagePreview.setVisible(false);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu(Localization.ACTIONS_MENU_LABEL);

        setRootMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        
        menu.add(setRootMenuItem);
        menu.add(exitMenuItem);

        menuBar.add(menu);
        return menuBar;
    }
    
    private JPanel createFileList() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        
        container.setBorder(BorderFactory.createLineBorder(Color.black));
        container.setPreferredSize(new Dimension(400, 600));
                
        EntryTableModel fileTableModel = new EntryTableModel();
        
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(true);
        
        //Current directory
        Entry initialEntry = new FileSystemEntry(new File("."));

        fileTableModel.setRoot(initialEntry);
        frame.setTitle(initialEntry.getFullPath());
        table.setModel(fileTableModel);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);

        @SuppressWarnings("unchecked")
        DefaultRowSorter<EntryTableModel, String> sorter = ((DefaultRowSorter<EntryTableModel, String>)table.getRowSorter());
        sorter.setSortKeys(Arrays.asList(
            new RowSorter.SortKey(0, SortOrder.ASCENDING),
            new RowSorter.SortKey(1, SortOrder.ASCENDING)
        ));
        sorter.sort();
        
        JScrollPane tableScroll = new JScrollPane(table);
        
        
        Dimension containerPreferredSize = container.getPreferredSize();
        
        tableScroll.setPreferredSize(new Dimension(containerPreferredSize.width, containerPreferredSize.height - 24));
        container.add(tableScroll, BorderLayout.NORTH);
        
        return container;
    }

    //TODO: Layout issue, when adding more preview modes another layout should be used.
    //BorderLayout has only 4 positions (slots) available, 3 of them are used now
    private JPanel createPreview() {
        previewContainer = new JPanel(new BorderLayout());
        previewContainer.setBorder(BorderFactory.createLineBorder(Color.black));
        previewContainer.setPreferredSize(new Dimension(600, 600));
        
        noPreviewLabel = new JLabel();
        
        noPreviewLabel.setText(Localization.NO_PREVIEW_AVAILABLE_LABEL);
        
        textPreview = new JTextArea();
        textPreview.setPreferredSize(new Dimension(500, 550));
        textPreviewScrollPane = new JScrollPane(textPreview, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        previewContainer.add(noPreviewLabel, BorderLayout.WEST);
        noPreviewLabel.setVisible(false);
        
        previewContainer.add(textPreviewScrollPane, BorderLayout.NORTH);
        textPreviewScrollPane.setVisible(false);

        imagePreview = new ImagePanel();
        
        imagePreview.setImageDimension(500, 500);
        imagePreview.setImageMargins(50, 50);
        
        previewContainer.add(imagePreview, BorderLayout.CENTER);
        imagePreview.setVisible(false);
        
        return previewContainer;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public JPanel getPreviewContainer() {
        return previewContainer;
    }
    
    public JLabel getNoPreviewLabel() {
        return noPreviewLabel;
    }
    
    public JScrollPane getTextPreviewScrollPane() {
        return textPreviewScrollPane;
    }
    
    public JTextArea getTextPreview() {
        return textPreview;
    }

    public ImagePanel getImagePreview() {
        return imagePreview;
    }
    
    public JTable getFileTable() {
        return table;
    }
    
    public JMenuItem getSetRootMenuItem() {
        return setRootMenuItem;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }
}