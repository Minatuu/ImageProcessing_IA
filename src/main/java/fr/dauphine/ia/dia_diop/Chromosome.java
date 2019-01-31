package fr.dauphine.ia.dia_diop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;

/**
 *
 * @author minatou
 */
public class Chromosome {

    public static final int MAX_POLYGON = 50;
    public static final int MAX_POINTS = 3;

    public Shape[] DNA; //  DNA représente une solution, elle encapsule un certain nombre de gènes(polygones)
    public static int imageLarg; //Largeur de l'image
    public static int imageLong; //Longueur de l'image
    public static BufferedImage target; //image source
    public static long NORM_COEFF;
    private static String inputFile;
    private Random gen;

    public Chromosome() {
        gen = new Random();
        DNA = new Shape[MAX_POLYGON];

        for (int i = 0; i < MAX_POLYGON; i++) {
            int X_POINTS[] = new int[2 * MAX_POINTS];
            int Y_POINTS[] = new int[2 * MAX_POINTS];
            for (int j = 0; j < 2 * MAX_POINTS; j++) {
                X_POINTS[j] = gen.nextInt(imageLarg);
                Y_POINTS[j] = gen.nextInt(imageLong);
            }
            Polygon poly = new Polygon(X_POINTS, Y_POINTS, 2 * MAX_POINTS);
            Color color = new Color(0, 0, 0, 5);
            Shape shape = new Shape(color, poly);
            DNA[i] = shape;
        }
    }

    public Chromosome(Chromosome copy) {
        this.DNA = copy.DNA;
        this.gen = new Random();
    }

    // cette fonction est une methode de reproduction 
    // pour reproduire un chromosome, on le clone.
    public static void geneMutation(Chromosome ancien, Chromosome nouveau, int index) {
        int r = ancien.DNA[index].color.getRed();
        int g = ancien.DNA[index].color.getGreen();
        int b = ancien.DNA[index].color.getBlue();
        int a = ancien.DNA[index].color.getAlpha();

        nouveau.DNA[index].color = new Color(r, g, b, a);

        for (int i = 0; i < 2 * MAX_POINTS; i++) {
            nouveau.DNA[index].polygon.X_POINTS[i] = ancien.DNA[index].polygon.X_POINTS[i];
            nouveau.DNA[index].polygon.Y_POINTS[i] = ancien.DNA[index].polygon.Y_POINTS[i];
        }
    }

    // cette fonction de mutation change carrement la population
    // changement de couleurs, de transparence et de cordonnée
    // d'une maniere completement aléatoire
    public int mutation() {
        int changer_index = gen.nextInt(MAX_POLYGON);

        double rot = gen.nextDouble() * 2;

        if (rot < 1) {
            int r = DNA[changer_index].color.getRed();
            int g = DNA[changer_index].color.getGreen();
            int b = DNA[changer_index].color.getBlue();
            int a = DNA[changer_index].color.getAlpha();
            if (rot < 0.25) {
                r = gen.nextInt(256);
            } else if (rot < 0.5) {
                g = gen.nextInt(256);
            } else if (rot < 0.75) {
                b = gen.nextInt(256);
            } else {
                a = gen.nextInt(256);
            }
            DNA[changer_index].color = new Color(r, g, b, a);
        } else {
            int changed_point = gen.nextInt(2 * MAX_POINTS);
            if (rot < 1.5) {
                DNA[changer_index].polygon.X_POINTS[changed_point] = gen.nextInt(imageLarg);
            } else {
                DNA[changer_index].polygon.Y_POINTS[changed_point] = gen.nextInt(imageLong);
            }
        }

        return changer_index;
    }

    public static BufferedImage drawDNA(Chromosome ch) {
        BufferedImage image = new BufferedImage(imageLarg, imageLong, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = image.getGraphics();

        for (int i = 0; i < MAX_POLYGON; i++) {
            graphics.setColor(ch.DNA[i].color);
            graphics.fillPolygon(ch.DNA[i].polygon.X_POINTS, ch.DNA[i].polygon.Y_POINTS, ch.DNA[i].polygon.N_POINTS);
        }

        return image;
    }

    // Cette methode calcule la "forme physique" ou fitness 
    // ou la pertinence d'une image particulière par rapport a l'image générée
    public static long calculFitness(BufferedImage test) {
        long fitness = 0;
        for (int i = 0; i < imageLarg; i++) {
            for (int j = 0; j < imageLong; j++) {
                int color1 = test.getRGB(i, j);
                int color2 = target.getRGB(i, j);
                int a1 = (color1 >> 24) & 0xff;
                int a2 = (color2 >> 24) & 0xff;
                int r1 = (color1 >> 16) & 0xff;
                int r2 = (color2 >> 16) & 0xff;
                int g1 = (color1 >> 8) & 0xff;
                int g2 = (color2 >> 8) & 0xff;
                int b1 = (color1 >> 0) & 0xff;
                int b2 = (color2 >> 0) & 0xff;
                int pixel_error = Math.abs(a1 - a2) + Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                fitness += pixel_error;
            }
        }

        return fitness;
    }

    public static void setTarget(String file) {
        try {
            target = ImageIO.read(new File(file));
            inputFile = file;
        } catch (IOException ex) {
        }

        imageLarg = target.getWidth();
        imageLong = target.getHeight();
        NORM_COEFF = imageLong * imageLong * 3 * 255;
    }

    // // Enregistrement de l'image dans un fichier .png
    public static void saveToFile(Chromosome c, int gen) {
        BufferedImage image = new BufferedImage(imageLarg, imageLong, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = image.getGraphics();
        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = (Graphics2D) graphics;

        for (int i = 0; i < MAX_POLYGON; i++) {
            int[] xps = new int[c.DNA[i].polygon.N_POINTS];
            int[] yps = new int[c.DNA[i].polygon.N_POINTS];

            for (int j = 0; j < c.DNA[i].polygon.N_POINTS; j++) {
                xps[j] = c.DNA[i].polygon.X_POINTS[j];
                yps[j] = c.DNA[i].polygon.Y_POINTS[j];
            }
            graphics.setColor(c.DNA[i].color);
            graphics.fillPolygon(xps, yps, c.DNA[i].polygon.N_POINTS);
        }

        try {
            ImageIO.write(image, "PNG", new File("text.png"));
        } catch (IOException ex) {
        }
        JPanel panelImage = new JPanel();
    }

    public static void main(String[] args) {
        new Chromosome();
    }
}
