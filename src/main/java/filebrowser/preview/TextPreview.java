package filebrowser.preview;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import filebrowser.ExceptionHandler;
import filebrowser.FileBrowserException;
import filebrowser.Localization;
import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class TextPreview implements Preview {
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    private static final List<String> ACCEPTED_EXTENSIONS_LIST = Arrays.asList("txt", "html", "java",
            "css", "js", "xml", "coffee", "dart", "ts", "rb");
    
    public static final Set<String> ACCEPTED_EXTENSIONS = new HashSet<String>(ACCEPTED_EXTENSIONS_LIST);
    
    private final ExceptionHandler exceptionHandler;
    
    private final PreviewView view;
    
    private final JScrollPane textPreviewScrollPane;
    
    private final JTextArea textPreview;
    
    private final Entry entry;
    
    public TextPreview(ExceptionHandler exceptionHandler, PreviewView view, JScrollPane textPreviewScrollPane, JTextArea textPreview, Entry entry) {
        this.exceptionHandler = exceptionHandler;
        this.view = view;
        this.textPreviewScrollPane = textPreviewScrollPane;
        this.textPreview = textPreview;
        this.entry = entry;
    }

    public void show() {

        //Loading preview entries in a separate thread as it can be time consuming
        SwingWorker<String, Object> previewLoader = new SwingWorker<String, Object>() {

            @Override
            public String doInBackground() throws FileBrowserException {
                try {
                    return new String(entry.readContent(), DEFAULT_ENCODING);                                        
                } catch (UnsupportedEncodingException e) {
                    throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
                }
            }

            @Override
            protected void done() {
                view.hidePreviews();
                textPreviewScrollPane.setVisible(true);
                try {
                    textPreview.setText(get());
                } catch (InterruptedException e) {
                    exceptionHandler.handleException(e.getCause().getMessage(), e);
                } catch (ExecutionException e) {
                    exceptionHandler.handleException(e.getCause().getMessage(), e);
                } catch (Exception e) {
                    exceptionHandler.handleException(e.getCause().getMessage(), e);                    
                }
            }
        };
        previewLoader.execute();
    }
    
    public static boolean acceptsEntryExtension(String extension) {
        return ACCEPTED_EXTENSIONS.contains(extension);
    }
}
