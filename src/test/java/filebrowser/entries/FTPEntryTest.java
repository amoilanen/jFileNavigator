package filebrowser.entries;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

public class FTPEntryTest {
    
    @Test
    public void test_successful_navigation_to_parent_entry() {
        assertEquals("Navigation from a folder", "/part1/part2/", getParentPath("/part1/part2/part3/"));
        assertEquals("Navigation from a file", "/part1/part2/", getParentPath("/part1/part2/part3"));
        assertEquals("Navigation from a first-level folder", "/", getParentPath("/part1/"));
        assertEquals("Navigation from a first-level file", "/", getParentPath("/part1"));
    }
    
    @Test
    public void test_navigation_to_parent_is_not_possible() {
        assertNull("Navigation up from root is not possible", getParentEntry("/"));
    }
    
    private static String getParentPath(String path) {       
        return getParentEntry(path).getFullPath();
    }

    private static Entry getParentEntry(String path) {
        FTPEntry entry = new FTPEntry(null, path, null);
        
        return entry.getParentEntry();
    }
}