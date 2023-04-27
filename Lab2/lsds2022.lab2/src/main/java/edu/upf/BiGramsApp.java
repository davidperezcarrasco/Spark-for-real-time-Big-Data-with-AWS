package edu.upf;


import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import edu.upf.model.ExtendedSimplifiedTweet;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BiGramsApp {

    public static void main(String[] args){
        String language = args[0];
        String outputDir = args[1];
        String input = args[2];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("TwitterLanguageFilterApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> tweetList = sparkContext.textFile(input);


        JavaPairRDD<Tuple2<String,String>,Integer> tweetRDD = tweetList
            .flatMap(tweets -> Arrays.asList(tweets.split("[\n]")).iterator())     // Splitting tweets. [String type]
            .map(tweet -> ExtendedSimplifiedTweet.fromJson(tweet))                 // Parsing the tweets. [Optional<SimplifiedTweet> type]
            .filter(tweet -> tweet.isEmpty()==false)                               // Filtering the Optional.empty(). [Optional<SimplifiedTweet> type]
            .filter(langTweets -> langTweets.get().getLanguage().equals(language)) // Filtering by language. [Optional<SimplifiedTweet> type]
            .filter(originals -> originals.get().isRetweeted()==false)                    // Filtering the original tweets. [Optional<SimplifiedTweet> type]
            .map(textTweet -> textTweet.get().getText())                           // Obtaining the text of the SimplifiedTweet. [String type]
            .flatMap(biGrams -> getBiGrams(biGrams).iterator())                    // Extracts bigrams of each tweet. [Tuple2<String,String> type]
            .mapToPair(biGram -> new Tuple2<>(biGram, 1))                      // Adds the "count" of bigrams. [Tuple2<Tuple2<String,String>,Integer> type]
            .reduceByKey((a,b) -> a+b)                                             // Reduction of each bigram. [Tuple2<Tuple2<String,String>,Integer> type]
            .mapToPair(tuple -> tuple.swap())                                      // Swapping tuple variables to sort by count. [Tuple2<Integer,Tuple2<String,String>> type]
            .sortByKey(false)                                           // Sorting bigrams by the number of appearances. [Tuple2<Integer,Tuple2<String,String>> type]
            .mapToPair(tuple -> tuple.swap());                                     // Swapping again to recover the original tuple: (bigram, count). [Tuple2<Tuple2<String,String>,Integer> type]

        System.out.println("Total tweets: " + tweetRDD.count());
        tweetRDD.saveAsTextFile(outputDir); 
    }
    public static List<Tuple2<String,String>> getBiGrams(String text) {
        List<Tuple2<String,String>> biGrams = new ArrayList<>(); // New array of bigrams as tuples (Tuple2).
        String[] words = text.split("\\s+");                     // Splitting words by whitespaces.
    
        for (int i = 0; i < words.length - 1; i++) {              
            Tuple2<String,String> tuple = new Tuple2<String,String>(normalise(words[i]), normalise(words[i+1])); // Creating a tupple with normalised words i and i+1.
            biGrams.add(tuple);  // Appending the tuple to the list.
        }
    
        return biGrams;
    }
    private static String normalise(String word) {
        return word.trim().toLowerCase();
    }
}
