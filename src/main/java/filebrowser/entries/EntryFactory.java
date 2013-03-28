package filebrowser.entries;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.net.ftp.FTPClient;

import filebrowser.FileBrowserException;
import filebrowser.Localization;

public class EntryFactory {
    
    private static final String FTP_ANONYMOUS_PASSWORD = "";
    
    private static final String FTP_ANONYMOUS_USERNAME = "anonymous";
    
    private static final String FTP_ENTRY_PREFIX = "ftp://";
    
    private static final String ZIP_ENTRY_SUFFIX = ".zip";
    
    private final static Entry NULL_ENTRY = new Entry() {

        public String getFullPath() {
            return "Selected destination does not exist";
        }

        public Date getLastModified() {
            return null;
        }

        public String getName() {
            return null;
        }

        public Entry getParentEntry() {
            return null;
        }

        public boolean isDirectory() {
            return false;
        }

        public List<Entry> listEntries() {
            return Collections.emptyList();
        }

        public Entry navigationTarget() {
            return null;
        }

        public boolean isNavigationPossible() {
            return false;
        }
        
        public byte[] readContent() {
            return null;
        }

        public InputStream getInputStream() {
            return null;
        }
    };
    
    private static Entry createFTPEntry(String entryPath) throws FileBrowserException {
        FTPClient client = new FTPClient();
        URL serverURL;
        try {
            serverURL = new URL(entryPath);
            client.connect(serverURL.getHost(), serverURL.getPort());
            client.login(FTP_ANONYMOUS_USERNAME, FTP_ANONYMOUS_PASSWORD);
            client.enterLocalPassiveMode();
            
            return new FTPEntry(client, serverURL.getPath(), null);
        } catch (MalformedURLException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_CREATE_ENTRY, e);
        } catch (SocketException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_CREATE_ENTRY, e);
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_CREATE_ENTRY, e);
        }
    }
    
    private static Entry createZippedEntry(File file, String entryPath, Entry parentEntry) throws FileBrowserException {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(entryPath);
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_CREATE_ENTRY, e);
        }
        ZippedEntry zippedEntry = new ZippedEntry(zipFile, FTP_ANONYMOUS_PASSWORD, file.getName(), new Date(file.lastModified()), null, parentEntry);
        
        zippedEntry.setNavigationPossible(true);
        return zippedEntry;
    }
    
    public static Entry createFileEntry(String entryPath, Entry parentEntry) throws FileBrowserException {
        File file = new File(entryPath);
        
        if (file.exists()) {
            if (entryPath.endsWith(ZIP_ENTRY_SUFFIX)) {
                return createZippedEntry(file, entryPath, parentEntry);
            } else {
                return new FileSystemEntry(file);
            }
        }
        return NULL_ENTRY;
    }
    
    public static Entry create(String entryPath, Entry parentEntry) throws FileBrowserException {
        return entryPath.startsWith(FTP_ENTRY_PREFIX) 
            ? createFTPEntry(entryPath)
            : createFileEntry(entryPath, parentEntry);
    }
    
    public static Entry create(String entryPath) throws FileBrowserException {
        return create(entryPath, null);
    }
}