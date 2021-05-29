package com.treasurehunt.treasurehunt.db;

import com.google.cloud.storage.*;

import java.io.*;

public class GCS {

    //"GOOGLE_CLOUD_PROJEC" is your projectID. Use "gcloud auth list" to get your projectID
    private static String GOOGLE_CLOUD_PROJECT = "treasurehunt-314717";
    private static String bucketName = "thpictures";

    // e.g. fileName="haha.png"
    public static String uploadToGCS(String fileName, InputStream fileInputStream) throws IOException {

        Storage storage = StorageOptions.newBuilder().setProjectId(GOOGLE_CLOUD_PROJECT).build().getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.createFrom(blobInfo, fileInputStream);

        //more info about the storage: https://googleapis.dev/java/google-cloud-storage/latest/com/google/cloud/storage/Storage.html

        return blob.getMediaLink();
    }

    public static void deleteFromGCS(String fileName) throws IOException {

        Storage storage = StorageOptions.newBuilder().setProjectId(GOOGLE_CLOUD_PROJECT).build().getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);

    }

    //test
    public static void main(String[] avg) throws IOException {
        deleteFromGCS("tamu_logo1.png");

    }

}
