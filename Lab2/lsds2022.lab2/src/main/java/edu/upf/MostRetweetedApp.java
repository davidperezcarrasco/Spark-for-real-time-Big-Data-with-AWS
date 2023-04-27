package edu.upf;

import org.apache.spark.api.java.JavaSparkContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import edu.upf.model.ExtendedSimplifiedTweet;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MostRetweetedApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: MostRetweetedApp <output-dir> <input>");
            System.exit(1);
        }
        String outputDir = args[0];
        String input = args[1];
        // Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("MostRetweetedApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        try {
            // Load input
            JavaRDD<String> tweetList = sparkContext.textFile(input);
            // Parse tweets
            JavaRDD<ExtendedSimplifiedTweet> tweets = tweetList.map(ExtendedSimplifiedTweet::fromJson)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
            // Group tweets by user
            JavaPairRDD<Long, ExtendedSimplifiedTweet> tweetsByUser = tweets.filter(tweet -> tweet.isRetweeted())
                    .mapToPair(tweet -> new Tuple2<>(tweet.getRetweetedUserId(), tweet))
                    .cache();
            // Find the 10 most retweeted users
            List<Long> mostRetweetedUsers = tweetsByUser.groupByKey()
                .mapToPair(userAndTweets -> new Tuple2<>(userAndTweets._1(), Iterables.size(userAndTweets._2())))
                .sortByKey()
                .map(tuple -> tuple._1())
                .take(10);
            // Filter tweets by most retweeted users and find the most retweeted tweet for each user
            JavaPairRDD<Long, ExtendedSimplifiedTweet> mostRetweetedByUser = tweetsByUser
                .filter(tuple -> mostRetweetedUsers.contains(tuple._1()))
                .reduceByKey((tweet1, tweet2) -> {
                    if (tweet1.getRetweetCount() > tweet2.getRetweetCount()) {
                        return tweet1;
                    } else {
                        return tweet2;
                    }
                });
            // Sort by number of retweets and take top 10
            List<Tuple2<ExtendedSimplifiedTweet, Long>> top10 = mostRetweetedByUser.mapToPair(Tuple2::swap)
                    .sortByKey(false)
                    .take(10);
            // Save output
            sparkContext.parallelize(top10).saveAsTextFile(outputDir);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Stop the Spark context
            sparkContext.stop();
        }

    
    }
}


