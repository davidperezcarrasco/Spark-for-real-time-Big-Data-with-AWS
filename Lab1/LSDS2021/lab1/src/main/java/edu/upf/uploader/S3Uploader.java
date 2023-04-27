package edu.upf.uploader;

import java.io.File;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Uploader implements Uploader{
    private String bucket;
    private String prefix;
    private final AmazonS3 Client;
    
    public S3Uploader(String bucketName, String Prefix, String credentials) {
        this.bucket = bucketName;
        this.prefix = Prefix;
        
        ProfileCredentialsProvider getcreds = new ProfileCredentialsProvider(); //Create a provider of credentials
        AWSCredentials awscredentials = getcreds.getCredentials(); // Obtain our default credentials

        Client = AmazonS3ClientBuilder // Create a client
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awscredentials)) // With our credentials
        .withRegion(Regions.US_EAST_1) // Same region as the bucket
        .build();    
    }
    
    public boolean bucketExists(){
        
        if (Client.doesBucketExistV2(bucket)) {  // Check if the bucket exists
            System.out.format("Bucket %s already exists.\n", bucket); 
            return true;
        } else { 
            System.out.format("Bucket %s does NOT exist.\n", bucket);
            return false;
        } 

    }
    
    @Override
    public void upload(List<String> files) { 
        int size = files.size();
        for (int i = 0; i <size; i++) { // In case there are more than one file
            
            File f = new File(files.get(i)); // Obtain the file
            String keyName = prefix + "/" + files.get(i); // Set the directory of the bucket where we want to upload the file

            System.out.printf("Uploading file: %s\n",files.get(i)); 
            if(bucketExists()){ // Check if the bucket exists
                try {
                    Client.putObject(bucket,keyName, f); // Try to put the file in the bucket
                } catch (AmazonServiceException e) {
                    System.err.println(e.getErrorMessage());
                    System.exit(1);
                }
            }
        } 
    }

}
