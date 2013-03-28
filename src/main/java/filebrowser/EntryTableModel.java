package filebrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import filebrowser.entries.Entry;
import filebrowser.entries.ParentEntry;

@SuppressWarnings("serial")
public class EntryTableModel extends AbstractTableModel {
    
    enum EntryType {
        DIRECTORY(Localization.DIRECTORY_ENTRY_DESCRIPTION),
        FILE(Localization.FILE_ENTRY_DESCRIPTION);
        
        private final String description;
        
        private EntryType(String description) {
            this.description = description;
        }
    }
    
    private final String[] COLUMNS = {
        Localization.TYPE_COLUMN_LABEL,
        Localization.NAME_COLUMN_LABEL,
        Localization.LAST_MODIFIED_COLUMN_LABEL
    };
    
    private final ExceptionHandler exceptionHandler;
    
    private Entry root;
    
    private List<Entry> entries;

    public EntryTableModel(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        entries = Collections.emptyList();
    }

    public void loadEntries() {
        
        //Loading entries in a separate thread as it can be time consuming
        SwingWorker<List<Entry>, Object> entriesLoader = new SwingWorker<List<Entry>, Object>() {

            @Override
            public List<Entry> doInBackground() throws FileBrowserException {
                List<Entry> entries = new ArrayList<Entry>();
                entries.add(new ParentEntry(root));
                entries.addAll(root.listEntries());
                return entries;
            }

            @Override
            protected void done() {
                try {
                    entries = get();                    
                } catch (InterruptedException e) {
                    exceptionHandler.handleException(Localization.ERROR_LOADING_ENTRIES, e);                    
                    entries = Collections.emptyList();
                } catch (ExecutionException e) {
                    exceptionHandler.handleException(Localization.ERROR_LOADING_ENTRIES, e);
                    entries = Collections.emptyList();
                } catch (Exception e) {
                    exceptionHandler.handleException(e.getCause().getMessage(), e);                    
                    entries = Collections.emptyList();
                }
                fireTableDataChanged();
            }
        };
        entriesLoader.execute();
    }
    
    public Object getValueAt(int row, int column) {
        Entry entry = entries.get(row);
        switch (column) {
            case 0:
                return entry.isDirectory() ? EntryType.DIRECTORY.description : EntryType.FILE.description;
            case 1:
                return entry.getName();
            case 2:
                return entry.getLastModified();
        }
        return "";
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Date.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    public int getRowCount() {            
        return entries.size();
    }
    
    public Entry getEntry(int row) {
        return entries.get(row);
    }
    
    public void setRoot(Entry newRoot) {
        root = newRoot;
        loadEntries();
    }
}
