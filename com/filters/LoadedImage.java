package com.filters;

import java.awt.*;

public class LoadedImage extends Canvas {
    Image img;

    LoadedImage(){}
    LoadedImage(Image i){ set(i); }

    public void set(Image i){
        img = i;
        repaint();
        //revalidate();
    }

    public void paint(Graphics g){
        //g.clearRect(0,0,2000,2000);
        if (img == null)
            g.drawString("no image", 20, 30);
        else
            g.drawImage(img, 0,0, this);
    }

    public Dimension getPreferredSize(){
        return new Dimension(img.getWidth(this), img.getHeight(this));
    }

    public Dimension getMinimumSize(){
        return getPreferredSize();
    }
}
