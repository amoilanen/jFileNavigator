package filebrowser;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

    private int imageWidth;
    
    private int imageHeight;
    
    private int marginLeft;
    
    private int marginTop;
    
    private BufferedImage image;

    public ImagePanel() {
    }

    public void setImageDimension(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }
    
    public void setImageMargins(int left, int top) {
        this.marginLeft = left;
        this.marginTop = top;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
        this.repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, marginLeft, marginTop, imageWidth, imageHeight, null);       
    }
}
