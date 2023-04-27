package edu.upf;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import edu.upf.model.ExtendedSimplifiedTweet;

import java.util.Arrays;

public class TwitterLanguageFilterApp {

    public static void main(String[] args){
        if (args.length < 3) {
            System.err.println("Usage: TwitterLanguageFilterApp <language> <outputDir> <input>");
            System.exit(1);
        }
        
        String language = args[0];
        String outputDir = args[1];
        String input = args[2];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("TwitterLanguageFilterApp");
        try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
            // Load input
            JavaRDD<String> tweetList = sparkContext.textFile(input);

            JavaRDD<String> tweetRDD = tweetList
                    .flatMap(tweets -> Arrays.asList(tweets.split("[\n]")).iterator())      // Splitting tweets by new line. [String type]
                    .map(tweet -> ExtendedSimplifiedTweet.fromJson(tweet))                          // Parsing tweets. [Optional<SimplifiedTweet> type]
                    .filter(tweet -> tweet.isPresent())                                // Removing the Optional.empty(). [Optional<SimplifiedTweet> type]
                    .filter(langTweets -> langTweets.get().getLanguage().equals(language))  // Filtering by language. [Optional<SimplifiedTweet> type]
                    .map(tweet -> tweet.get().toString());                                        // Transforming each tweet to String. [String type]

            System.out.println("Total tweets: " + tweetRDD.count());
            tweetRDD.saveAsTextFile(outputDir); 
        }
    }
}