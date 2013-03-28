package filebrowser.preview;

import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.junit.Before;
import org.junit.Test;

import filebrowser.ExceptionHandler;
import filebrowser.ImagePanel;
import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class PreviewFactoryTest {

    private JLabel noPreviewLabel;
    
    private JScrollPane textPreviewScroll;

    private JTextArea textPreview;
    
    private JPanel previewContainer;

    private ImagePanel imagePreview;

    private ExceptionHandler exceptionHandler;
    
    private PreviewView view;
    
    private PreviewFactory factory;
    
    @Before
    public void before() {
        factory = new PreviewFactory();
        view = createMock(PreviewView.class);
        exceptionHandler = createMock(ExceptionHandler.class);
        
        noPreviewLabel = createMockBuilder(JLabel.class).createMock();
        textPreviewScroll = createMockBuilder(JScrollPane.class).createMock();
        textPreview = createMockBuilder(JTextArea.class).createMock();
        previewContainer = createMockBuilder(JPanel.class).createMock();
        imagePreview = createMockBuilder(ImagePanel.class).createMock();

        expect(view.getNoPreviewLabel()).andReturn(noPreviewLabel).anyTimes();
        expect(view.getTextPreviewScrollPane()).andReturn(textPreviewScroll).anyTimes();
        expect(view.getTextPreview()).andReturn(textPreview).anyTimes();
        expect(view.getPreviewContainer()).andReturn(previewContainer).anyTimes();
        expect(view.getImagePreview()).andReturn(imagePreview).anyTimes();
        
        replay(view);
    }
    
    @Test
    public void test_if_entry_is_a_directory_then_empty_preview() {
        Entry entry = createMock(Entry.class);
        
        expect(entry.isDirectory()).andReturn(true);
        
        replay(entry);
        
        Preview preview = factory.createPreview(exceptionHandler, view, entry);

        assertTrue("Empty preview is returned for a directory", preview instanceof EmptyPreview);
    }

    @Test
    public void test_if_has_one_of_the_text_extensions_then_a_text_preview_is_created() {
        List<String> textFiles = Arrays.asList("file.txt", "file.html", "file.java",
                "file.css", "file.js", "file.xml", "file.coffee", "file.dart", "file.ts", "file.rb");

        for (String textFile : textFiles) {
            Entry entry = createMock(Entry.class);

            expect(entry.isDirectory()).andReturn(false);
            expect(entry.getName()).andReturn(textFile).anyTimes();

            replay(entry);

            Preview preview = factory.createPreview(exceptionHandler, view, entry);

            assertTrue("Text preview should be created for " + textFile, preview instanceof TextPreview);
        }
    }

    @Test
    public void test_if_has_one_of_the_image_extensions_then_an_image_preview_is_created() {
        List<String> imageFiles = Arrays.asList("file.png", "file.jpg", "file.jpeg",
                "file.PNG", "file.JPG", "file.JPEG");

        for (String imageFile : imageFiles) {
            Entry entry = createMock(Entry.class);

            expect(entry.isDirectory()).andReturn(false);
            expect(entry.getName()).andReturn(imageFile).anyTimes();

            replay(entry);

            Preview preview = factory.createPreview(exceptionHandler, view, entry);

            assertTrue("Image preview should be created for " + imageFile, preview instanceof ImagePreview);
        }
    }

    @Test
    public void test_if_unknown_extension_then_an_empty_preview_is_created() {
        String unknownFile = "file.mp3";
        Entry entry = createMock(Entry.class);
        
        expect(entry.isDirectory()).andReturn(false);
        expect(entry.getName()).andReturn(unknownFile).anyTimes();
        
        replay(entry);
        
        Preview preview = factory.createPreview(exceptionHandler, view, entry);

        assertTrue("Empty preview is returned for an unknown file", preview instanceof EmptyPreview);
    }
}