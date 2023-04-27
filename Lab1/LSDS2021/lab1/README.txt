David Pérez Carrasco(u188332), Guillem Escriba Molto (u188331), Pau Folch (u188246)

SimplifiedTweet: Here we make the object constructor using the 6 features described in the assignment, and also the method Optional<SimplifiedTweet> which takes a String as an input,
and converts it to a JSON Object, consisting of the 6 basic features initialized to 0 or null. We check that the input string contains all these basic features and otherwise we return
an empty object, as the assignemnt says. If there are the 6 features we return the complete SimplifiedTweet. We also have defined a getLanguage method which returns the tweet language.

FileLanguageFilter: When implementing the filterLanguage class we are doing the following: First we create the constructor with Strings inputFile and outputFile which will containt the 
output fail and all the desired input files, as the statement says. Then, in the filterLanguage method, implemented from the interface, we are receiving a single String as the statement says, 
and doing the following within a try catch exception as we are allowed to do. We create the files and buffers for reading and writing and we use an extra argument for the writing one, append:true,
which means if we call again the function with different input file but same output file we are going to append the text instead of overwritting it. We use them to read the input files line by line
(until it's empty) and storing each line in a string which we use to create the SimplifiedTweet calling the previous function and finally we check if that tweet is complete and is from the filter
language and if it is we write it in the output file. Finally we close both buffers and the corresponding files. We also print the number of tweets for each filtered folder, and then we summ it out.

S3Uploader:In this class we make the constructor declaring the corresponding bucket and prefix with the input strings and the credentials with the ProfileCredentialsProvider , creating an AmazonS3
client which has the same credentials as the user executing the project, since they are provided by the Provider, no matter if the configuration is default or upf. We also define a boolean function
that checks if the corresponding bucket already exists or not, printing a line about it. Finally, we are doing the upload function in a way that it uploads the input file in the corresponding bucket
(with the language as the prefix, as established by us in the TwitterFilter main class) and if that bucket does not exists it directly creates it. 
So, answering to the question, if the bucket exists, it just overwrite the file (in case the file exists) or place the file in there, and if it does not exist it creates it and uploads the file. In 
both cases we make a print informing the user about wether the bucket existed or not.

Benchmarking:
ES: The program lasted 123 seconds to run the filter for all the folders and upload everything to the bucket. There are 509435 filtered tweets. To be more precise, the fastest folder was the 9th one,
which only lasted 5 seconds, and the slowest the 10th one, which lasted 34 seconds. It makes sense since the 10th has 2GB and the 9th 300MB, they are the biggest and the smallest respectively.The time
of the uploading was 27 seconds in total.

EN: The program lasted 126 seconds to run the filter for all the folders and upload everything to the bucket. There are 446603 filtered tweets. To be more precise, the fastest folder was the 9th one,
which only lasted 5 seconds, and the slowest the 10th one, which lasted 33 seconds. It makes sense since the 10th has 2GB and the 9th 300MB, they are the biggest and the smallest respectively.The time
of the uploading was 29 seconds in total.

CA: The program lasted 110 seconds to run the filter for all the folders and upload everything to the bucket. There are 4583 filtered tweets. To be more precise, the fastest folder was the 9th one,
which only lasted 5 seconds, and the slowest the 10th one, which lasted 31 seconds. It makes sense since the 10th has 2GB and the 9th 300MB, they are the biggest and the smallest respectively.The time
of the uploading was 20 seconds in total.  
 
We did experience minor problems when calculating the time, such as having the time in nanoseconds and converting it to seconds by dividing it. Another problem was that we could not calculate the total
number of tweets of each case, only of the separated folders and then summing it out. We could get the time of the full execution, but the number of tweets was calculated inside the Filter class, so it
was not possible to calculate the total number, since we needed to call that function for the different input files and then the integer variable that calculated the number of tweets was eliminated. 
However, all of these were minor problems since we could fix it with no headaches and without changing the code from the assesment expectations.

We were able to execute the file using the following command: java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter es out.txt lsds2022.lab1.output.<uNumber> ./twitter-data-from-2018-eurovision-final/Eurovision3.json ./twitter-data-from-2018-eurovision-final/Eurovision4.json ./twitter-data-from-2018-eurovision-final/Eurovision5.json ./twitter-data-from-2018-eurovision-final/Eurovision6.json ./twitter-data-from-2018-eurovision-final/Eurovision7.json ./twitter-data-from-2018-eurovision-final/Eurovision8.json ./twitter-data-from-2018-eurovision-final/Eurovision9.json ./twitter-data-from-2018-eurovision-final/Eurovision10.json
Note: we can execute this if and only if we have the dataset properly donwloaded and decompressed in the lab1/ folder. We are submiting the zip without it, since it is so big and its storage is not needed for 
the submission, but a execution without this decompressed folder will not work.

Extensions:
We have decided to use the tests that are suggested since in our opinion they are good enough to 
test individually if the parsing is correctly done. We could also make more tests related to the upload
for example. For example the first one checks if the parse works and the other two ones test exceptions
that can succeed.