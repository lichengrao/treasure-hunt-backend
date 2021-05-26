package com.treasurehunt.treasurehunt.db.gcs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.io.IOException;
import java.io.InputStream;

public class GCS {

    private static final String bucketName = "treasure-hunt-listing-pictures";

    public static String upload(Storage storage, String fileName, InputStream fileInputStream) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.createFrom(blobInfo, fileInputStream);

        System.out.println("File uploaded to " + bucketName + " as " + fileName);
        return blob.getMediaLink();
    }
}
