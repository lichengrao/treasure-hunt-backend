package com.treasurehunt.treasurehunt.db.gcs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.io.InputStream;

public class GCS {

    private static final String bucketName = "treasure-hunt-listing-pictures";

    // Upload picture to listing-pictures bucket in GCS
    public static String uploadPicture(Storage storage, String fileName, InputStream fileInputStream) throws GCSException {
        try {
            // Create blobId and blobInfo
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            // Upload blob into GCS
            Blob blob = storage.createFrom(blobInfo, fileInputStream);

            // Return the media link of the uploaded blob
            System.out.println("File uploaded to " + bucketName + " as " + fileName);
            return blob.getMediaLink();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GCSException(String.format("Failed to upload %s to %s", fileName, bucketName));
        }
    }
}