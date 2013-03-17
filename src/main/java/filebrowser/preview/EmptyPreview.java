package filebrowser.preview;

import javax.swing.JLabel;

import filebrowser.PreviewView;

public class EmptyPreview implements Preview {
    
    private final PreviewView view;
    
    private final JLabel noPreviewLabel;
    
    public EmptyPreview(PreviewView view, JLabel noPreviewLabel) {
        this.view = view;
        this.noPreviewLabel = noPreviewLabel;
    }

    public void show() {
        this.view.hidePreviews();
        this.noPreviewLabel.setVisible(true);
    }
}