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

import filebrowser.ImagePanel;
import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class ImagePreview implements Preview {
    
    private static final List<String> ACCEPTED_EXTENSIONS_LIST = Arrays.asList("png", "jpg", "jpeg");
    
    public static final Set<String> ACCEPTED_EXTENSIONS = new HashSet<String>(ACCEPTED_EXTENSIONS_LIST);
    
    private final PreviewView view;
    
    private final ImagePanel panel;
    
    private final Entry entry;
    
    public ImagePreview(PreviewView view, ImagePanel panel, Entry entry) {
        this.view = view;
        this.panel = panel;
        this.entry = entry;
    }

    public void show() {

        //Loading preview entries in a separate thread as it can be time consuming
        SwingWorker<BufferedImage, Object> previewLoader = new SwingWorker<BufferedImage, Object>() {

            @Override
            public BufferedImage doInBackground() {
                try {
                    return ImageIO.read(entry.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                view.hidePreviews();
                panel.setVisible(true);
                try {
                    panel.setImage(get());
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
