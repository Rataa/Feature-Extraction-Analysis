/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureextraction;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.edgeDetector.Canny;
import de.lmu.ifi.dbs.jfeaturelib.features.ColorHistogram;
import de.lmu.ifi.dbs.jfeaturelib.features.FCTH;
import de.lmu.ifi.dbs.jfeaturelib.features.Gabor;
import de.lmu.ifi.dbs.jfeaturelib.features.Haralick;
import de.lmu.ifi.dbs.jfeaturelib.features.Histogram;
import de.lmu.ifi.dbs.jfeaturelib.features.MPEG7EdgeHistogram;
import de.lmu.ifi.dbs.jfeaturelib.shapeFeatures.*;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ColorProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.imageanalysis.filters.CannyEdgeDetector;

/**
 *
 * @author Thimal
 */
public class FeatureExtraction {

    /**
     * @param args the command line arguments
     */
    static double min=0;
    static double max=1;
        public static String createHistogram(File f) {
        ColorProcessor image = null;
        String result = null;

        try {
            image = new ColorProcessor(ImageIO.read(f));
            System.out.println("pixel count="+image.getPixelCount()*3);
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        // load the properties from the default properties file
        // change the histogram to span just 2 bins
        // and let's just extract the histogram for the RED channel
        LibProperties prop = null;
        try {
            prop = LibProperties.get();
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
//        prop.setProperty(LibProperties.HISTOGRAMS_BINS, 24);
//        prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.RGB.name());
        // after v 1.0.1 you will be able to use this:
        // prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.Red.name());

        // initialize the descriptor, set the properties and run it
            ColorHistogram descriptor = new ColorHistogram(3, 3, 3);

//        descriptor.setProperties(prop);
        descriptor.run(image);

        // obtain the features
        List<double[]> features = descriptor.getFeatures();
       
        // print the features to system out
        for (double[] feature : features) {
            result = f.getName().replaceFirst("[.][^.]+$", "")+","+Arrays2.join(feature, ", ", "%.5f");
           
            double count=0;
            for (double val: feature){
                count+=val;
            }
            System.out.println("count      ="+feature.length);
            System.out.println(result);
        }
        
        return result;
    }
    
    public static void createHistogram(String dirPath){
        PrintWriter writer = null;
        
        


        File dir = new File (dirPath);
        try {
            (new File(dir.getParent()+"/Histogram")).mkdirs();
            writer = new PrintWriter(dir.getParent()+"/Histogram/input"+dir.getName()+".txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles()) {
                String resultString;
                resultString = createHistogram(f);
                writer.println(resultString);
            }
            writer.close();
        }
    }
    
    public static String createGaborFilter(File f) {
        ColorProcessor image = null;
        String result = null;
        try {
            image = new ColorProcessor(ImageIO.read(f));
            System.out.println("pixel count="+image.getPixelCount()*3);
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        // load the properties from the default properties file
        // change the histogram to span just 2 bins
        // and let's just extract the histogram for the RED channel
        LibProperties prop = null;
        try {
            prop = LibProperties.get();
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
//        prop.setProperty(LibProperties.HISTOGRAMS_BINS, 24);
//        prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.RGB.name());
        // after v 1.0.1 you will be able to use this:
        // prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.Red.name());

        // initialize the descriptor, set the properties and run it
         Gabor descriptor =new Gabor();
        
//        descriptor.setProperties(prop);
        descriptor.run(image);
        // obtain the features
        List<double[]> features = descriptor.getFeatures();
       
        // print the features to system out
        for (double[] feature : features) {
            result = f.getName().replaceFirst("[.][^.]+$", "")+","+Arrays2.join(feature, ", ", "%.5f");
            double count=0;
            for (double val: feature){
                if(max<val)
                    max=val;
                if(min>val)
                    min=val;
                count+=val;
            }

            System.out.println("count      ="+count);
            System.out.println("length     ="+feature.length);
            System.out.println(result);
        }
        
        return result;
    }
    
    public static void createGaborFilter(String dirPath){
        PrintWriter writer = null;
        
        


        File dir = new File (dirPath);
        try {
            (new File(dir.getParent()+"/Gabor")).mkdirs();
         
            // Directory creation failed
        
            writer = new PrintWriter(dir.getParent()+"/Gabor/input"+dir.getName()+".txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        min=0;
        max=1;
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles()) {
                String resultString;
                resultString = createGaborFilter(f);
                writer.println(resultString);
            }
            System.out.println("Gabor");
            System.out.println("max        ="+max);
            System.out.println("max        ="+min);
            writer.close();
        }
    }
    
        public static String createFCTH(File f) {
        ColorProcessor image = null;
        String result = null;
        try {
            image = new ColorProcessor(ImageIO.read(f));
            System.out.println("pixel count="+image.getPixelCount()*3);
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        // load the properties from the default properties file
        // change the histogram to span just 2 bins
        // and let's just extract the histogram for the RED channel
        LibProperties prop = null;
        try {
            prop = LibProperties.get();
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
//        prop.setProperty(LibProperties.HISTOGRAMS_BINS, 24);
//        prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.RGB.name());
        // after v 1.0.1 you will be able to use this:
        // prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.Red.name());

        // initialize the descriptor, set the properties and run it
         FCTH descriptor =new FCTH();
        
//        descriptor.setProperties(prop);
        descriptor.run(image);

        // obtain the features
        List<double[]> features = descriptor.getFeatures();
       
        // print the features to system out
        for (double[] feature : features) {
            result = f.getName().replaceFirst("[.][^.]+$", "")+","+Arrays2.join(feature, ", ", "%.5f");
            double count=0;
            for (double val: feature){
                if(max<val)
                    max=val;
                if(min>val)
                    min=val;
                count+=val;
            }

            System.out.println("count      ="+count);
            System.out.println("length     ="+feature.length);
            System.out.println(result);
        }
        
        return result;
    }
    
    public static void createFCTH(String dirPath){
        PrintWriter writer = null;
        
        


        File dir = new File (dirPath);
        try {
            (new File(dir.getParent()+"/FCTH")).mkdirs();
         
            // Directory creation failed
        
            writer = new PrintWriter(dir.getParent()+"/FCTH/input"+dir.getName()+".txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        min=0;
        max=1;
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles()) {
                String resultString;
                resultString = createFCTH(f);
                writer.println(resultString);
            }
            System.out.println("FCTH");
            System.out.println("max        ="+max);
            System.out.println("max        ="+min);
            writer.close();
        }
    }
    
    
        public static String createMPEGEdge(File f) {
        ColorProcessor image = null;
        String result = null;
        try {
            image = new ColorProcessor(ImageIO.read(f));
            System.out.println("pixel count="+image.getPixelCount()*3);
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        // load the properties from the default properties file
        // change the histogram to span just 2 bins
        // and let's just extract the histogram for the RED channel
        LibProperties prop = null;
        try {
            prop = LibProperties.get();
        } catch (IOException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
//        prop.setProperty(LibProperties.HISTOGRAMS_BINS, 24);
//        prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.RGB.name());
        // after v 1.0.1 you will be able to use this:
        // prop.setProperty(LibProperties.HISTOGRAMS_TYPE, Histogram.TYPE.Red.name());

        // initialize the descriptor, set the properties and run it
         CentroidBoundaryDistance descriptor =new CentroidBoundaryDistance();
        
//        descriptor.setProperties(prop);
        descriptor.run(image);

        // obtain the features
        List<double[]> features = descriptor.getFeatures();
       
        // print the features to system out
        for (double[] feature : features) {
            result = f.getName().replaceFirst("[.][^.]+$", "")+","+Arrays2.join(feature, ", ", "%.5f");
            double count=0;
            for (double val: feature){
                if(max<val)
                    max=val;
                if(min>val)
                    min=val;
                count+=val;
            }

            System.out.println("count      ="+count);
            System.out.println("length     ="+feature.length);
            System.out.println(result);
        }
        
        return result;
    }
    
    public static void createMPEGEdge(String dirPath){
        PrintWriter writer = null;
        
        


        File dir = new File (dirPath);
        try {
            (new File(dir.getParent()+"/MPEGEdge")).mkdirs();
         
            // Directory creation failed
        
            writer = new PrintWriter(dir.getParent()+"/MPEGEdge/input"+dir.getName()+".txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeatureExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        min=0;
        max=1;
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles()) {
                String resultString;
                resultString = createMPEGEdge(f);
                writer.println(resultString);
            }
            System.out.println("MPEGEdge");
            System.out.println("max        ="+max);
            System.out.println("max        ="+min);
            writer.close();
        }
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        for(int i=1;i<4;i++){
            createGaborFilter("C:/Users/Thimal/Desktop/feature extraction code/geospacial/newset/new/"+i);
            createFCTH("C:/Users/Thimal/Desktop/feature extraction code/geospacial/newset/new/"+i);
            createHistogram("C:/Users/Thimal/Desktop/feature extraction code/geospacial/newset/new/"+i);
            createMPEGEdge("C:/Users/Thimal/Desktop/feature extraction code/geospacial/newset/new/"+i);
        }
//            createGaborFilter("C:/Users/Thimal/Desktop/feature extraction code/geospacial");
//            createMPEGEdge("C:/Users/Thimal/Desktop/feature extraction code/geospacial");
//            createHistogram("C:/Users/Thimal/Desktop/feature extraction code/geospacial");

//            createGaborFilter("C:/Users/Thimal/Desktop/new images/New folder");
//            createFCTH("C:/Users/Thimal/Desktop/new images/New folder");
//            createHistogram("C:/Users/Thimal/Desktop/new images/New folder");
//            createMPEGEdge("C:/Users/Thimal/Desktop/new images/New folder");
        
//     File dir = new File ("C:/Users/Thimal/Desktop/feature extraction code/geospacial/geo all"); 
//     int count =0;
//     if (dir.isDirectory()) { // make sure it's a directory
//            for (final File f : dir.listFiles()) {
//               f.renameTo(new File(f.getParent()+"/satellite_"+count+".jpg"));
//               count++;
//                System.out.println("name = "+f.getPath());
//            }
//            System.out.println("count == "+count);
//
//        }
        
        
    }
    

    
    
}
