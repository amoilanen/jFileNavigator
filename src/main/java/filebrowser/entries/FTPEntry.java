package filebrowser.entries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import filebrowser.FileBrowserException;
import filebrowser.Localization;

public class FTPEntry implements Entry {
    
    private final FTPClient client;
    
    private final String path;
    
    /*
     * May be 'null', only not null when this file was listed in a current FTPEntry.
     * This is due to the specificity of the API of FTPClient.
     */
    private final FTPFile file;

    public FTPEntry(FTPClient client, String path, FTPFile file) {
        this.client = client;
        this.path = path;
        this.file = file;
    }
    
    public List<Entry> listEntries() throws FileBrowserException {
        FTPFile[] children;
        try {
            children = client.listFiles(path);
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_CHILD_ENTRIES_LIST, e);
        }
        List<Entry> entries = new ArrayList<Entry>();
        
        for (FTPFile child : children) {
            entries.add(new FTPEntry(client, path + "/" + child.getName(), child));
        }
        return entries;
    }

    public Entry getParentEntry() {
        StringBuilder parentPath = new StringBuilder();
        String[] pathParts = path.split("/");
        List<String> nonEmptyPathParts = new ArrayList<String>();
        
        for (String pathPart : pathParts) {
            if (pathPart.length() > 0) {
                nonEmptyPathParts.add(pathPart);
            }
        }
        if (nonEmptyPathParts.size() > 0) {
            nonEmptyPathParts.remove(nonEmptyPathParts.size() - 1);
        }        
        for (String nonEmptyPathPart : nonEmptyPathParts) {
            parentPath.append("/").append(nonEmptyPathPart);
        }
        parentPath.append("/");
        return (!parentPath.toString().equals(path)) 
            ? new FTPEntry(client, parentPath.toString(), null)
            : null;
    }
    
    public Entry navigationTarget() {
        return this;
    }
    
    public String getName() {
        return (null != file) ? file.getName() : "";
    }
    
    public Date getLastModified() {
        return (null != file) ? file.getTimestamp().getTime() : new Date(0);
    }
    
    public boolean isDirectory() {
        return (null == file) || file.isDirectory();
    }
    
    public String getFullPath() {
        return path;
    }

    public boolean isNavigationPossible() {
        return isDirectory();
    }
    
    public byte[] readContent() throws FileBrowserException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            client.retrieveFile(path, out);
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
        }
        return out.toByteArray();
    }

    public InputStream getInputStream() throws FileBrowserException {
        return new ByteArrayInputStream(readContent());
    }
}