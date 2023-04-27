package edu.upf.uploader;

import java.util.List;

public interface Uploader {
    /**
    * Uploads a file to the target specified through its implementation
    * @param files the files to upload
     * @return void
    */
    public void upload(List<String> files);
}
