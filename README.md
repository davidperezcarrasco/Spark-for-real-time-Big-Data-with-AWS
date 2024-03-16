# Apache Spark for real-time Mastodon tweets with DynamoDB

## Mastodon Streaming
* LanguageMaputils: Here we do a filter to discard the header from map.tsv and also another filter to discard first the lines with less than 2 columns, since the first column is always present and it the row has 2 columns or less it means it has not the desired language code or the english name of the language, so we can discard these at first, and then also we discard the languages whose ISO 639-1 Code (the desired two digits code) is empty. Then we return the pairrdd thanks to the maptopair.

* MastodonStateless: After trying a lof of combinations here, we have get a relatively simple code to do what exactly the statement asks us. First we transform the stream with the tweets into a pairstream with the language of that tweet and 1 as a initialized count, we then make a join with the rdd obtained from the languagemap that is gonna group all the tweets with their corresponding languages, and we are gonna select only the english name of the language (from the languagemaprdd) and the number of tweets,which is that count integer that we are gonna sum with the reducebyKey operation and we are also gonna sort everything with the sortByKey operation in the same stream with false as ascending attribute, for what we are gonna need to swap before and after that call to make the ordering absed on the numebr of tweets. Finally we print the number of tweets of each language.

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
* MastodonWindows: Again, after studying a lot of different options, we have found a code really similar to the previous one, in fact it's the exact same for the mini batchs, and the corresponding operations with the Windows, which we initialize with 60 seconds (in order to get the last 3 batchs) but all the operations are in the same structure as with the batchs, with the only difference that now we do it to a stream.window. Now we do print the 15 top languages.
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
* MastodonWithState: Here we do the exact same as before but a bit trickier to be able to use both the language and the user name. We use the final stream with a string (language name from buildmap) and two tuples, one with the language code and a tuple with the user name and the integer initialized to 1. We do this cause we cannot directly delete an attributed originated from a join and we want to cosnerve the count and the user name. It could be tricky, but it is not more complex or heavier than any other possible implementation, ant the output is exactly the expected, since then we select only the desired attributes (user name and count) and do the exact same operations as before, the reducebykey, the swaps and the sortbykey. Also, before we filtered deleting pairs with no username, since we considered it'd make no sense to consider a tuple contaning all the no-username tweets. The prompt is the following:
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

General Notes: Eventhough when running any of our programs you could see a lot of error lines on the command prompt, related to a bigBone Exception, that does not affect our output at all. It's jsut a matter of datstructures with the number of followers of some users or other things like this that does not have anything to do with our code, and if you pay attention to the time of the lines, every 20 seconds you're gonna see corresponding the batch and the window.

## DynamoDB

The essential part is in the DynamoHashtagRepository.java, this is composed by two methods, the write(SimpliedTweetWithHashtags h) this one enters as parameter each tweet read from the stream, then it maps it and instead of using the function putItem, we use updateItem that also adds the new items to the dynamo table but when the item is already in the table it just updates the count and the tweet_ids list. This method is called in the main class MastodonHashtags.java with the same commands as before, since this is a spark program that reads from a Mastodon stream. On the other hand, we have the second method, readTop10(String lang), this method is a java class that must be called with an additional argument as language to filter the tweets by the given language, then, the main class of MastodonHashtagsrReader.java colleccts all the hashtags collected in the table of the dynamo and then it chooses the 10 with more repetitions. 
The first main class, the writer does not have any output, it justs fills the dynamo table, the second one, instead prints the top 10 hashtags of a given language as we can see:

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










