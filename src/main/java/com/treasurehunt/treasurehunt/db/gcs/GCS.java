package com.treasurehunt.treasurehunt.db.gcs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.io.InputStream;

public class GCS {

    // Deployment: change for deployment
    private static final String bucketName = "treasure-hunt-listing-pictures";

    // Upload picture to listing-pictures bucket in GCS
    public static String uploadPicture(Storage storage, String pictureId, InputStream fileInputStream) throws GCSException {
        try {
            // Create blobId and blobInfo
            BlobId blobId = BlobId.of(bucketName, pictureId);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            // Upload blob into GCS
            Blob blob = storage.createFrom(blobInfo, fileInputStream);

            // Return the media link of the uploaded blob
            System.out.println("Picture uploaded to " + bucketName + " as " + pictureId);
            return blob.getMediaLink();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GCSException(String.format("Failed to upload %s to %s", pictureId, bucketName));
        }
    }

    // Remove uploaded picture from listing-pictures bucket in GCS
    public static void deletePicture(Storage storage, String pictureId) throws GCSException {
        try {

            // Delete picture from picture bucket
            storage.delete(bucketName, pictureId);

            System.out.println("Picture " + pictureId + " was deleted from " + bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GCSException(String.format("Failed to delete %s from %s", pictureId, bucketName));
        }
    }
}
