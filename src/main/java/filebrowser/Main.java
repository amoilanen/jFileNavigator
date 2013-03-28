package filebrowser;

import javax.swing.JFrame;

import filebrowser.preview.PreviewFactory;

public class Main {

    public static void main(String[] args) {
        LoggingSupport.configure();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BrowserController browser = new BrowserController(new PreviewFactory());
                JFrame frame = browser.createGUI();

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
