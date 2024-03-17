# Apache Spark for real-time Mastodon tweets with DynamoDB

Due to size limit reasons, the dataset is not uploaded here. Contact to me if interested in running the program. 

## Mastodon Streaming
* LanguageMaputils: This class tackles the processing of a tab-separated values (TSV) file containing language mappings. It performs a series of filtering operations to ensure data quality. First, it discards the header line. Then, it removes lines with less than two columns, assuming the first column always exists. These lines likely lack the desired language code or its English name. Finally, it filters out lines where the crucial ISO 639-1 code (two-digit language identifier) is empty. After this cleaning process, the remaining valid lines are transformed into a Pair RDD format, presumably for further use within the application.

* MastodonStateless: This code tackles real-time language distribution analysis in a Mastodon stream. It transforms the tweet stream into a key-value pair stream. Each pair associates the tweet's language code with an initial count of 1. It then joins this stream with a pre-processed RDD containing language mappings, grouping tweets by language and retrieving their English names. A reduceByKey operation aggregates tweet counts for each language. Finally, it sorts the results by tweet count in descending order (swapping before and after sort for most active languages first) and prints the number of tweets per language.

The command needed to be used is this:
```
spark-submit --class edu.upf.MastodonStateless target/lab3-mastodon-1.0-SNAPSHOT.jar ./src/main/resources/map.tsv --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:src/main/resources/log4j.properties
```

And a sample output is:
(English,410)
(German,70)
(French,52)
(Japanese,37)
(Chinese,24)
(Spanish; Castilian,20)
(Dutch; Flemish,18)
(Portuguese,12)
(Italian,7)
(Czech,4)
...

## Stateless Transformations
* MastodonWindows:  This code builds upon the MastodonStateless approach for real-time language distribution analysis. It utilizes windowing techniques to gain insights into language usage over time. While the core logic for transforming tweets into key-value pairs, joining with language mappings, and aggregating counts remains the same, it operates on a windowed stream instead of the entire stream. The window is set to 60 seconds, effectively analyzing the most recent three batches of tweets. This allows for the calculation of dynamic language distribution trends within the Mastodon stream. Finally, it focuses on the top 15 languages with the most tweets within the window, providing a snapshot of the most prominent languages at that specific time.
The command is:
```
spark-submit --class edu.upf.MastodonWindow target/lab3-mastodon-1.0-SNAPSHOT.jar ./src/main/resources/map.tsv --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:src/main/resources/log4j.properties
```

And a sample output is:

1st batch:
(English,287)
(German,40)
(Spanish; Castilian,11)
(Dutch; Flemish,18)
(Japanese,14)
(Portuguese,11)
(Greek, Modern (1453-),6)

1st window:
(English,287)
(German,40)
(Spanish; Castilian,11)
(Dutch; Flemish,18)
(Japanese,14)
(Portuguese,11)
(Greek, Modern (1453-),6)

2nd batch:
(English,338)
(German,48)
(Spanish; Castilian,20)
(French,14)
(Greek, Modern (1453-),10)
(Japanese,10)
(Dutch; Flemish,10)
(Portuguese,10)
(Czech,4)

2nd window:
(English,625)
(German,88)
(Spanish; Castilian,31)
(Dutch; Flemish,28)
(Japanese,24)
(Portuguese,21)
(Greek, Modern (1453-),16)
(French,14)
(Czech,4)

3rd batch:
(English,382)
(German,68)
(French,26)
(Spanish; Castilian,18)
(Dutch; Flemish,16)
(Greek, Modern (1453-),8)
(Japanese,8)
(Czech,8)
(Portuguese,8)
(Finnish,4)

3rd window:
(English,1007)
(German,156)
(Spanish; Castilian,49)
(Dutch; Flemish,44)
(French,40)
(Japanese,32)
(Portuguese,29)
(Greek, Modern (1453-),24)
(Czech,12)
(Finnish,4)

## Spark Stateful transformations with Windows
* MastodonWithState: This code extends the language analysis capabilities by incorporating usernames. It closely resembles the previous approaches but tackles the challenge of managing both language and username attributes. To achieve this, it transforms the final stream into a structure with a string (language name), two tuples, and an integer. One tuple holds the language code, while the other stores the username and an initial count of 1. This approach is necessary because directly deleting attributes originating from a join operation isn't straightforward. It then filters out any pairs with missing usernames, as these wouldn't be meaningful for the analysis. With the desired data structure in place, it performs operations similar to the previous classes: reduceByKey to aggregate user counts for each language, swapping before and after sorting to ensure the most active languages appear first, and finally sorting by count in descending order. This results in a breakdown of tweet activity per language, highlighting the most active users within each language. The prompt is the following:
```
spark-submit --class edu.upf.MastodonWithState target/lab3-mastodon-1.0-SNAPSHOT.jar ./src/main/resources/map.tsv en --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:src/main/resources/log4j.properties
```

or any other language instead of en, and a sample output is:
1st batch;
(4,John Manoogian III)
(3,Amplify Chaos ??)
(3,Thorpa)
(3,The Hockey Hoosier)
(3,Smudge The Insult Cat ?)
(3,AshGhebranious)
(3,Brian Allen Hammerle)
(3,Verdant Square)
(2,noplasticshower)
(2,Steve Thomas)
(2,Amanda CAARSON)
(2,Matt Dagley)
(2,Spot: NHL)
(2,CK's Technology News)
(2,Wild_Heather_860)
(2,Shaula Evans)
(2,The Metal Dog ??)
(2,the trouble with mia)
(2,Ash Wells)
(2,Walt Ruff :verified_legacy: ?)
...

1st window
(4,John Manoogian III)
(3,)
(3,Amplify Chaos ??)
(3,Thorpa)
(3,The Hockey Hoosier)
(3,Smudge The Insult Cat ?)
(3,AshGhebranious)
(3,Brian Allen Hammerle)
(3,Verdant Square)
(2,noplasticshower)
(2,Steve Thomas)
(2,Amanda CAARSON)
(2,Matt Dagley)
(2,Spot: NHL)
(2,CK's Technology News)
(2,Wild_Heather_860)
(2,Shaula Evans)
(2,The Metal Dog ??)
(2,the trouble with mia)
(2,Ash Wells)
(2,Walt Ruff :verified_legacy: ?)
...

2nd batch:
(10,Perseverance Image Bot ?)
(5,Formula 1 :verify:)
(5,Fresh IT Vulnerabilities)
(3,Aure Free Press)
(3,Steve Canon)
(3,Major League Baseball)
(3,*BOT* radio free fedi playlist)
(3,Tyler)
(3,Darren)
(2,Old_IT_geek)
(2,Free Software Foundation)
(2,Grateful Dread)
(2,? Blake Murphy :verified_legacy: ?)
(2,Vegtrafikksentralen vest)
(2,Alastair McBain :unverified:)
(2,NewsNowGamingFeed)
(2,Bridge Makes ?)
(2,Jayski ?)
(2,Vegtrafikksentralen øst)
(2,Android Dreams)
...

2nd window:
(10,Perseverance Image Bot ?)
(5,Formula 1 :verify:)
(5,Fresh IT Vulnerabilities)
(4,Thorpa)
(4,*BOT* radio free fedi playlist)
(4,Steve Canon)
(4,WACOCA)
(4,The Hockey Hoosier)
(4,John Manoogian III)
(4,Verdant Square)
(4,? UFC :verified_business: ?)
(4,AmplifyUkraine)
(3,Old_IT_geek)
(3,Aure Free Press)
(3,Grateful Dread)
(3,Amplify Chaos ??)
(3,Major League Baseball)
(3,Spot: NHL)
(3,the trouble with mia)
...

## DynamoDB

The DynamoHashtagRepository class acts as a bridge between the Spark application and a DynamoDB table for persistent hashtag tracking. Its write(SimplifiedTweetWithHashtags h) method efficiently handles both new and existing hashtags.  For new hashtags encountered in tweets, it adds them to the DynamoDB table. If a hashtag already exists, it cleverly updates its associated count and tweet ID list using the updateItem function. This ensures accurate tracking of hashtag usage over time.  The readTop10(String lang) method complements the write functionality. It takes a language parameter and retrieves the top 10 most frequent hashtags associated with that language from the DynamoDB table. This method likely serves a separate class tasked with analyzing and presenting these trending hashtags.

 "en":

{"hashTag":"press","lang":"en","count":16}
{"hashTag":"nowplaying","lang":"en","count":6}
{"hashTag":"jpop","lang":"en","count":6}
{"hashTag":"news","lang":"en","count":5}
{"hashTag":"caturday","lang":"en","count":5}
{"hashTag":"music","lang":"en","count":4}
{"hashTag":"museumarchive","lang":"en","count":4}
{"hashTag":"g1","lang":"en","count":4}
{"hashTag":"seventies","lang":"en","count":3}
{"hashTag":"photography","lang":"en","count":3}

 "es":

{"hashTag":"worf","lang":"es","count":1}
{"hashTag":"troi","lang":"es","count":1}
{"hashTag":"thenextgeneration","lang":"es","count":1}
{"hashTag":"summer","lang":"es","count":1}
{"hashTag":"startrek56","lang":"es","count":1}
{"hashTag":"startrek","lang":"es","count":1}
{"hashTag":"savethemermaids","lang":"es","count":1}
{"hashTag":"riker","lang":"es","count":1}
{"hashTag":"primavera","lang":"es","count":1}
{"hashTag":"picard","lang":"es","count":1}


 "ca":

{"hashTag":"dretslaborals","lang":"ca","count":1}
{"hashTag":"coreadelsud","lang":"ca","count":1}
{"hashTag":"324cat","lang":"ca","count":1}

Since there are less amount of catalan users than english or spanish ones, we can see that in the collected tweets there are not enought hashtags to obtain a top10 










