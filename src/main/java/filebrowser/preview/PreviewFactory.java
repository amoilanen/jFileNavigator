package filebrowser.preview;

import filebrowser.ExceptionHandler;
import filebrowser.PreviewView;
import filebrowser.entries.Entry;

public class PreviewFactory {
    
    public Preview createPreview(ExceptionHandler exceptionHandler, PreviewView view, Entry entry) {
        if (entry.isDirectory()) {
            return new EmptyPreview(view, view.getNoPreviewLabel());
        }
        String entryName = entry.getName();
        int lastDotIndex = entryName.lastIndexOf(".");
        String entryExtension = "";
        
        if (lastDotIndex >= 0 && lastDotIndex <= entryName.length() - 2) {
            entryExtension = entryName.substring(lastDotIndex + 1).toLowerCase();
        }

        if (TextPreview.acceptsEntryExtension(entryExtension)) {
            return new TextPreview(exceptionHandler, view, view.getTextPreviewScrollPane(), view.getTextPreview(), entry);
        } else if (ImagePreview.acceptsEntryExtension(entryExtension)) {
            return new ImagePreview(exceptionHandler, view, view.getImagePreview(), entry);
        }
        return new EmptyPreview(view, view.getNoPreviewLabel());
    }
}