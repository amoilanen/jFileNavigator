package filebrowser.entries;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import filebrowser.FileBrowserException;

public interface Entry {
    
    List<Entry> listEntries() throws FileBrowserException;
    
    Entry getParentEntry();
    
    Entry navigationTarget();
    
    String getName();
    
    Date getLastModified();
    
    boolean isDirectory();
        
    String getFullPath();
    
    boolean isNavigationPossible();
    
    byte[] readContent() throws FileBrowserException;
    
    InputStream getInputStream() throws FileBrowserException;
}
