package com.filters;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.*;

public class ImageFiltersDemo extends Frame implements ActionListener {
    Image img, fimg, curimg;
    LoadedImage lim;
    PlugInFilter pif;
    Label lab;
    Button open, reset, save;

    FileDialog fd;
    JFileChooser dir;

    String[] filters = {"Grayscale","Invert","Contrast","Blur","Sharpen"};

    ImageFiltersDemo(){
        Panel p = new Panel();
        add(p, BorderLayout.SOUTH);

        lab = new Label("");
        add(lab, BorderLayout.NORTH);

        lim = new LoadedImage();

        open = new Button("Open...");
        open.addActionListener((ae) -> {
            fd = new FileDialog(this, "File Dialog");
            fd.setVisible(true);
            String file = fd.getDirectory() + fd.getFile();
            //System.out.println(file);
            if (fd.getFile() == null)
                return;

            try {
                File imageFile = new File(file);
                img = ImageIO.read(imageFile);
            } catch (IOException exc){
                System.out.println("Error to load");
                System.exit(0);
            }

            lim.set(img);
            add(lim, BorderLayout.CENTER);
            repaint();
            revalidate(); // !!!!!!!!

        });
        p.add(open);

        reset = new Button("Reset");
        reset.addActionListener(this);
        p.add(reset);

        for (String fstr : filters){
            Button b = new Button(fstr);
            b.addActionListener(this);
            p.add(b);
        }

        save = new Button("Save...");
        save.addActionListener((ae) -> {
            if (fimg == null)
                return;
            dir = new JFileChooser();
            dir.setCurrentDirectory(new File("."));
            dir.setDialogTitle("Choose directory to save file");
            dir.setApproveButtonText("Save here");
            dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dir.setAcceptAllFileFilterUsed(false);
            //
            if (dir.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = dir.getSelectedFile().getAbsolutePath();
                //System.out.println(path);

                try {
                    ImageIO.write(this.toBufferedImage(fimg), "png", new File(path + "/output.png"));
                } catch (IOException exc){
                    System.out.println("Error to load");
                    System.exit(0);
                }
            }
        });
        p.add(save);

        // Inner anonymous class
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                System.exit(0);
            }
        });
    }

    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void actionPerformed(ActionEvent ae){
        String a = "";

        try {
            a = ae.getActionCommand();
            if (a.equals("Reset")){
                lim.set(img);
                fimg = null;
                lab.setText("Original");
            } else {
                // !!! Filter classes are within package, so full name to class is required: Class.forName("com.filters." + a)
                pif = (PlugInFilter) (Class.forName("com.filters." + a)).getConstructor().newInstance();

                fimg = (fimg != null) ? fimg : img;
                fimg = pif.filter(this, fimg);
                lim.set(fimg);
                lab.setText("Filtered: " + a);
            }
            repaint();
        } catch (ClassNotFoundException e) {
            lab.setText(a + " not found");
            lim.set(img);
        } catch (InstantiationException e) {
            lab.setText("coulbn't new " + a);
        } catch (IllegalAccessException e){
            lab.setText("access error  " + a);
        } catch (NoSuchMethodException | InvocationTargetException e){
            lab.setText("filter error " + a);
        }
    }

    public static void main(String[] args){
        ImageFiltersDemo app = new ImageFiltersDemo();
        app.setSize(new Dimension(700, 600));
        app.setTitle("Demo");
        app.setVisible(true);
    }
}