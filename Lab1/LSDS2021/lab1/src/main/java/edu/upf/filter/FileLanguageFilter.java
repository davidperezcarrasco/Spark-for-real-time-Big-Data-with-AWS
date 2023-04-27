package edu.upf.filter;

import java.util.Optional;

import edu.upf.parser.SimplifiedTweet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileLanguageFilter implements LanguageFilter{
    private final String inputFile;
    private final String outputFile;

    public FileLanguageFilter(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

@Override
    public void filterLanguage(String language) throws IOException {
        BufferedReader bufferReader;
        BufferedWriter bufferWriter;
        int ntweets = 0;

        try {
            FileWriter writer = new FileWriter(outputFile.toString(), true);
            FileReader reader = new FileReader(inputFile.toString());

            bufferReader = new BufferedReader(reader);
            bufferWriter = new BufferedWriter(writer);

            String line;
            while ((line = bufferReader.readLine()) != null) {

                Optional<SimplifiedTweet> tweet = SimplifiedTweet.fromJson(line); 
                                                                                    
                if (tweet.isPresent()) {
                    String string_language = (String) tweet.get().getLanguage();
                    boolean bool_language = string_language.equals(language); 
                    if (bool_language) {
                        try {
                            bufferWriter.write(tweet.get().toString() + System.lineSeparator());
                            ntweets++;
                        }  
                        catch (IOException Exception) {
                        }
                    }
                }
            }
            System.out.println("Number of tweets so far: " + ntweets);
            bufferReader.close();
            bufferWriter.close();

            reader.close();
            writer.close();

        } 
        
        catch (IOException Exception) {
            Exception.printStackTrace();
            throw Exception;
        }
    }
}