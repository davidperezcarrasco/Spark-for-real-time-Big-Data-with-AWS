package edu.upf;

import edu.upf.filter.FileLanguageFilter;
import edu.upf.uploader.S3Uploader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TwitterFilter {
    public static void main( String[] args ) throws Exception {
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);
        String bucket = argsList.get(2);
        System.out.println("Language: " + language + ". Output file: " + outputFile + ". Destination bucket: " + bucket);
        long t_start = System.nanoTime();
        for(String inputFile: argsList.subList(3, argsList.size())) {
            System.out.println("Processing: " + inputFile);
            final FileLanguageFilter filter = new FileLanguageFilter(inputFile, outputFile);
            long Time_start = System.nanoTime();
            filter.filterLanguage(language);
            long nano_estimated_Time = System.nanoTime();
	        System.out.println ("Time to complete the filter: " + (nano_estimated_Time - Time_start)/1000000000);
        }
        final S3Uploader uploader = new S3Uploader(bucket, language, "upf");
        uploader.upload(Arrays.asList(outputFile));
        long t_end= System.nanoTime();
	    System.out.println ("Time to complete the full program: " + (t_end - t_start)/1000000000);
    }
}
