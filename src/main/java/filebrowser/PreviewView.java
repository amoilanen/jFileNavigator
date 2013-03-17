package filebrowser;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public interface PreviewView {

    JPanel getPreviewContainer();
    
    JLabel getNoPreviewLabel();
    
    JScrollPane getTextPreviewScrollPane();
    
    JTextArea getTextPreview();

    ImagePanel getImagePreview();
    
    void hidePreviews();
}
