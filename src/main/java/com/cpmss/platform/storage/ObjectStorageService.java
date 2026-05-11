package com.cpmss.platform.storage;

import java.io.InputStream;
import java.time.Duration;

/**
 * Cross-cutting binary object storage boundary.
 *
 * <p>Bounded-context services own file authorization and metadata persistence.
 * This service only stores, deletes, and creates temporary access URLs for
 * objects in the configured object store.
 */
public interface ObjectStorageService {

    /**
     * Stores an object.
     *
     * @param objectKey   internal object key
     * @param inputStream object content stream
     * @param sizeBytes   object size in bytes
     * @param contentType object MIME type
     */
    void putObject(String objectKey, InputStream inputStream, long sizeBytes, String contentType);

    /**
     * Deletes an object if it exists.
     *
     * @param objectKey internal object key
     */
    void deleteObject(String objectKey);

    /**
     * Creates a short-lived download URL for an object.
     *
     * @param objectKey        internal object key
     * @param downloadFilename filename hint for the client
     * @param expiry           URL lifetime
     * @return presigned URL string
     */
    String createDownloadUrl(String objectKey, String downloadFilename, Duration expiry);
}
