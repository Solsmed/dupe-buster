/**
 * Simple index creation with Lire
 *
 * @author Mathias Lux, mathias@juggle.at
 * @author Alexander Solsmed, mail@solsmed.se
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import net.semanticmetadata.lire.impl.GenericDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram;

import org.apache.lucene.index.IndexWriter; // lucene-core
import org.apache.lucene.index.IndexWriterConfig; // lucene-core
import org.apache.lucene.store.FSDirectory; // lucene-core
import org.apache.lucene.document.Document; // lucene-core
import org.apache.lucene.analysis.core.WhitespaceAnalyzer; // lucene-analyzers-common

public class Indexer {
    public static void main(String[] args) throws IOException {
        // Checking if arg[0] is there and if it is a directory.
        boolean passed = false;
        if (args.length > 0) {
            File f = new File(args[0]);
            System.out.println("Indexing images in " + args[0]);
            if (f.exists() && f.isDirectory()) passed = true;
        }
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
            System.exit(1);
        }
        
        // Getting all images from a directory and its sub directories.
        ArrayList<String> images = getAllImages(new File(args[0]), true);
        
        // Creating a CEDD document builder and indexing all files.
        //DocumentBuilder builder = new GenericDocumentBuilder(CEDD.class);
        // Use multiple DocumentBuilder instances:
        ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
        //builder.addBuilder(new GenericDocumentBuilder(CEDD.class));
        builder.addBuilder(new GenericDocumentBuilder(FCTH.class));
        //builder.addBuilder(new GenericDocumentBuilder(AutoColorCorrelogram.class));

        // Creating an Lucene IndexWriter
        IndexWriterConfig conf = new IndexWriterConfig(LuceneUtils.LUCENE_VERSION,
                new WhitespaceAnalyzer(LuceneUtils.LUCENE_VERSION));
        
        IndexWriter iw = new IndexWriter(FSDirectory.open(new File("index")), conf);
        
        // Iterating through images building the low level features
        for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
            String imageFilePath = it.next();
            System.out.println("Indexing " + imageFilePath);
            try {
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                Document document = builder.createDocument(img, imageFilePath);
                iw.addDocument(document);
            } catch (Exception e) {
                System.err.println("Error reading image or indexing it.");
                e.printStackTrace();
            }
        }
        // closing the IndexWriter
        iw.close();
        System.out.println("Finished indexing.");
    }

    private static ArrayList<String> getAllImages(File directory, boolean descendIntoSubDirectories) throws IOException {
        ArrayList<String> resultList = new ArrayList<String>(256);
        File[] f = directory.listFiles();
        for (File file : f) {
            //if (file != null && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".gif")) && !file.getName().startsWith("tn_")) {
            if( file.getName().toLowerCase().endsWith("_original")) {
                resultList.add(file.getCanonicalPath());
            }
            if (descendIntoSubDirectories && file.isDirectory()) {
                ArrayList<String> tmp = getAllImages(file, true);
                if (tmp != null) {
                    resultList.addAll(tmp);
                }
            }
        }
        if (resultList.size() > 0)
            return resultList;
        else
            return null;
    }
}