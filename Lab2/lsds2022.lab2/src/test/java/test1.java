import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import scala.Int;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class test1 {
    public static void main(String[] args){
        String text = "Ayer me comí un plato _maca@rones";
        List<Tuple2<Tuple2<String,String>,Integer>> biG = getBiGrams(text);
        System.out.println(biG);
    }
    public static List<Tuple2<Tuple2<String,String>,Integer>> getBiGrams(String text) {
        List<Tuple2<Tuple2<String,String>,Integer>> biGrams = new ArrayList<>();
        String[] words = text.split("\\s+");
    
        for (int i = 0; i < words.length - 1; i++) {
            Tuple2<String,String> bigram = new Tuple2<String,String>(normalise(words[i]), normalise(words[i+1]));
            Tuple2<Tuple2<String,String>,Integer> tuple = new Tuple2<Tuple2<String,String>,Integer>(bigram, 1);
            biGrams.add(tuple);
        }
    
        return biGrams;
    }
    private static String normalise(String word) {
        return word.trim().toLowerCase();
    }
}
