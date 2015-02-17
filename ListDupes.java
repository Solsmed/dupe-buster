/**
 * Simple duplicates finder
 *
 * @author Alexander Solsmed, mail@solsmed.se
 */

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram;
import net.semanticmetadata.lire.ImageDuplicates;

import org.apache.lucene.index.IndexReader; // lucene-core
import org.apache.lucene.index.DirectoryReader; // lucene-core
import org.apache.lucene.store.FSDirectory; // lucene-core

public class ListDupes {
    public static void main(String[] args) throws IOException {
        IndexReader ir = DirectoryReader.open(FSDirectory.open(new File("index")));

        ImageSearcher searcher[] = new ImageSearcher[1];
        searcher[0] = new GenericFastImageSearcher(0, FCTH.class);

        String newline = System.getProperty("line.separator");

        System.out.println("--- Dupe report ---");
        for (int i = 0; i < searcher.length; i++) {
            ImageDuplicates dupes = searcher[i].findDuplicates(ir);
            System.out.println(i + ";\t" + "Search method " + i + ", " + dupes.length() + " dupe cases");
            //System.out.println(i + ";\t" + );

            for (int j = 0; j < dupes.length(); j++) {
                List<String> copies = dupes.getDuplicate(j);
                System.out.println(i + ";" + j + ".\t" + "Dupe case " + j + ", " + copies.size() + " copies");
                //System.out.println(i + ";" + j + ".\t" + );
                BufferedWriter bw = new BufferedWriter(new FileWriter("dupelogs/method-" + i + "-dupecase-" + j + ".log"));

                for (int k = 0; k < copies.size(); k++) {
                    String copyName = copies.get(k);
                    System.out.println(i + ";" + j + "." + k + ":\t" + copyName);
                    bw.write(copyName);
                    bw.write(newline);
                }

                bw.flush();
                bw.close();
            }
        }
    }
}