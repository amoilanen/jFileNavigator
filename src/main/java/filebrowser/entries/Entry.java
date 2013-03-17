package filebrowser.entries;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface Entry {
    
    List<Entry> listEntries();
    
    Entry getParentEntry();
    
    Entry navigationTarget();
    
    String getName();
    
    Date getLastModified();
    
    boolean isDirectory();
        
    String getFullPath();
    
    boolean isNavigationPossible();
    
    byte[] readContent();
    
    InputStream getInputStream();
}
