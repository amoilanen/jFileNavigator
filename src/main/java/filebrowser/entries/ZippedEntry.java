package filebrowser.entries;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import filebrowser.FileBrowserException;
import filebrowser.Localization;

public class ZippedEntry implements Entry {
    
    private final ZipFile zipFile;
    
    private final String fullPathInsideZipFile;
    
    private final String name;
    
    private final Date lastModified;
    
    private final ZipEntry zipEntry;
    
    private final Entry parentEntry;
    
    private boolean isNavigationPossible = false;

    public ZippedEntry(ZipFile zipFile, String fullPathInsideZipFile, String name, Date lastModified, ZipEntry zipEntry, Entry parentEntry) {
        this.zipFile = zipFile;
        this.fullPathInsideZipFile = fullPathInsideZipFile;
        this.name = name;
        this.lastModified = lastModified;
        this.zipEntry = zipEntry;
        this.parentEntry = parentEntry;
    }
    
    public void setNavigationPossible(boolean isNavigationPossible) {
        this.isNavigationPossible = isNavigationPossible;
    }
    
    @SuppressWarnings("unchecked")
    public List<Entry> listEntries() throws FileBrowserException {
        List<Entry> children = new ArrayList<Entry>();
        Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zipFile.entries();
        
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            String zipEntryName = zipEntry.getName();
          
            if (zipEntryName.startsWith(fullPathInsideZipFile)) {
                String relativeName = zipEntryName.substring(fullPathInsideZipFile.length());
                String[] nameParts = relativeName.split("/");
              
                if (nameParts.length <= 1) {
                    String shortRelativeName = nameParts[0];
                  
                    children.add(new ZippedEntry(zipFile, zipEntryName, shortRelativeName, lastModified, zipEntry, this)); 
                }
            }
        }
        return children;
    }
    
    public Entry getParentEntry() {
        return parentEntry;
    }
    
    public Entry navigationTarget() {
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getLastModified() {
        return lastModified;
    }
    
    public boolean isDirectory() {
        return fullPathInsideZipFile.endsWith("/");
    }
    
    public String getFullPath() {
        return zipFile.getName() + ":" + fullPathInsideZipFile;
    }

    public boolean isNavigationPossible() {
        return isNavigationPossible || isDirectory();
    }
    
    public byte[] readContent() throws FileBrowserException {
        try {
            return IOUtils.toByteArray(getInputStream());
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
        }
    }
    
    public InputStream getInputStream() throws FileBrowserException {
        try {
            return new BufferedInputStream(zipFile.getInputStream(zipEntry));
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
        }
    }
}