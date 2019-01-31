package fr.dauphine.ia.dia_diop;

import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;



public class Processing extends Application {
    private static String img;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
      JFileChooser jfc = new JFileChooser("resources");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        jfc.setFileFilter(imageFilter);
        jfc.showDialog(new JLabel(), "Choose Image");
        File file = jfc.getSelectedFile();
        String Image = file.getAbsolutePath();
         Chromosome.setTarget(Image);
        Chromosome best_DNA = new Chromosome();
        Chromosome test_DNA = new Chromosome();

        for (int i = 0; i < Chromosome.MAX_POLYGON; i++) {
            Chromosome.geneMutation(best_DNA, test_DNA, i);
        }

        long maxFitness = Long.MAX_VALUE;
        long fitness1 = maxFitness;
        long fitness_best = maxFitness;
        double fitness = 0;

        int beneficial = 0, total = 0;

        long startTimestamp = System.currentTimeMillis();

        while (true) {
            int change_index = test_DNA.mutation();
            fitness1 = Chromosome.calculFitness(Chromosome.drawDNA(test_DNA));

            if (fitness1 < fitness_best) {
                Chromosome.geneMutation(test_DNA, best_DNA, change_index);

                fitness_best = fitness1;
                fitness = 100 * (1 - ((double) fitness_best / Chromosome.NORM_COEFF));
                beneficial++;
                total++;
            } else {
                Chromosome.geneMutation(best_DNA, test_DNA, change_index);
                total++;
            }
            if (total % 1000 == 0) {
                Chromosome.saveToFile(best_DNA, total);
                System.out.printf(" Fitness: %f, Time: %ds%n", fitness, (System.currentTimeMillis() - startTimestamp) / 1000);
            }
        }
    }
}
