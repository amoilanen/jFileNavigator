package filebrowser.preview;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import filebrowser.ExceptionHandler;
import filebrowser.FileBrowserException;
import filebrowser.ImagePanel;
import filebrowser.Localization;
import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class ImagePreview implements Preview {
    
    private static final List<String> ACCEPTED_EXTENSIONS_LIST = Arrays.asList("png", "jpg", "jpeg");
    
    public static final Set<String> ACCEPTED_EXTENSIONS = new HashSet<String>(ACCEPTED_EXTENSIONS_LIST);
    
    private final ExceptionHandler exceptionHandler;
    
    private final PreviewView view;
    
    private final ImagePanel panel;
    
    private final Entry entry;
    
    public ImagePreview(ExceptionHandler exceptionHandler, PreviewView view, ImagePanel panel, Entry entry) {
        this.exceptionHandler = exceptionHandler;
        this.view = view;
        this.panel = panel;
        this.entry = entry;
    }

    public void show() {

        //Loading preview entries in a separate thread as it can be time consuming
        SwingWorker<BufferedImage, Object> previewLoader = new SwingWorker<BufferedImage, Object>() {

            @Override
            public BufferedImage doInBackground() throws FileBrowserException {
                try {
                    return ImageIO.read(entry.getInputStream());                    
                } catch (IOException e) {
                    throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
                }
            }

            @Override
            protected void done() {
                view.hidePreviews();
                panel.setVisible(true);
                try {
                    panel.setImage(get());
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
