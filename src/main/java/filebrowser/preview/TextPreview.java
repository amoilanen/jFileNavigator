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

import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class TextPreview implements Preview {
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    private static final List<String> ACCEPTED_EXTENSIONS_LIST = Arrays.asList("txt", "html", "java",
            "css", "js", "xml", "coffee", "dart", "ts", "rb");
    
    public static final Set<String> ACCEPTED_EXTENSIONS = new HashSet<String>(ACCEPTED_EXTENSIONS_LIST);
    
    private final PreviewView view;
    
    private final JScrollPane textPreviewScrollPane;
    
    private final JTextArea textPreview;
    
    private final Entry entry;
    
    public TextPreview(PreviewView view, JScrollPane textPreviewScrollPane, JTextArea textPreview, Entry entry) {
        this.view = view;
        this.textPreviewScrollPane = textPreviewScrollPane;
        this.textPreview = textPreview;
        this.entry = entry;
    }

    public void show() {

        //Loading preview entries in a separate thread as it can be time consuming
        SwingWorker<String, Object> previewLoader = new SwingWorker<String, Object>() {

            @Override
            public String doInBackground() {
                try {
                    return new String(entry.readContent(), DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void done() {
                view.hidePreviews();
                textPreviewScrollPane.setVisible(true);
                try {
                    textPreview.setText(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        previewLoader.execute();
    }
    
    public static boolean acceptsEntryExtension(String extension) {
        return ACCEPTED_EXTENSIONS.contains(extension);
    }
}
