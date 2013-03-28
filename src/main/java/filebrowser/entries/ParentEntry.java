package filebrowser.entries;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import filebrowser.FileBrowserException;

public class ParentEntry implements Entry {
    
    private static final String PARENT_DIR_NAME_LABEL = "..";
    private final Entry entry;
    
    public ParentEntry(Entry entry) {
        this.entry = entry;
    }
    
    public List<Entry> listEntries() throws FileBrowserException {
        return entry.listEntries();
    }

    public Entry getParentEntry() {
        return entry.getParentEntry();
    }
    
    public Entry navigationTarget() {
        return getParentEntry();
    }
    
    public String getName() {
        return PARENT_DIR_NAME_LABEL;
    }
    
    public Date getLastModified() {
        return entry.getLastModified();
    }
    
    public boolean isDirectory() {
        return true;
    }
    
    public String getFullPath() {
        return entry.getFullPath();
    }

    public byte[] readContent() throws FileBrowserException {
        return entry.readContent();
    }

    public InputStream getInputStream() throws FileBrowserException {
        return entry.getInputStream();
    }

    public boolean isNavigationPossible() {
        return null != getParentEntry();
    }
}