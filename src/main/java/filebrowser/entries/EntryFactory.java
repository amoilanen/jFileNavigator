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
    
    private static Entry createFTPEntry(String entryPath) {
        FTPClient client = new FTPClient();
        URL serverURL;
        try {
            serverURL = new URL(entryPath);
            client.connect(serverURL.getHost(), serverURL.getPort());
            client.login(FTP_ANONYMOUS_USERNAME, FTP_ANONYMOUS_PASSWORD);
            client.enterLocalPassiveMode();
            
            return new FTPEntry(client, serverURL.getPath(), null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return NULL_ENTRY;
        } catch (SocketException e) {
            e.printStackTrace();
            return NULL_ENTRY;
        } catch (IOException e) {
            e.printStackTrace();
            return NULL_ENTRY;
        }
    }
    
    private static Entry createZippedEntry(File file, String entryPath, Entry parentEntry) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(entryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZippedEntry zippedEntry = new ZippedEntry(zipFile, FTP_ANONYMOUS_PASSWORD, file.getName(), new Date(file.lastModified()), null, parentEntry);
        
        zippedEntry.setNavigationPossible(true);
        return zippedEntry;
    }
    
    public static Entry createFileEntry(String entryPath, Entry parentEntry) {
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
    
    public static Entry create(String entryPath, Entry parentEntry) {
        return entryPath.startsWith(FTP_ENTRY_PREFIX) 
            ? createFTPEntry(entryPath)
            : createFileEntry(entryPath, parentEntry);
    }
    
    public static Entry create(String entryPath) {
        return create(entryPath, null);
    }
}