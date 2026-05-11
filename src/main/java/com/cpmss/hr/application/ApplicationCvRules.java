package com.cpmss.hr.application;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.platform.exception.ApiException;

/**
 * Business rules for current CV metadata on job applications.
 */
public class ApplicationCvRules {

    /**
     * Validates upload metadata before storing the binary object.
     *
     * @param originalFilename submitted filename
     * @param contentType      submitted MIME type
     * @param sizeBytes        submitted size in bytes
     */
    public void validateUploadMetadata(String originalFilename, String contentType,
                                       long sizeBytes) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ApiException(HrErrorCode.APPLICATION_CV_FILENAME_REQUIRED);
        }
        if (contentType == null || contentType.isBlank()) {
            throw new ApiException(HrErrorCode.APPLICATION_CV_CONTENT_TYPE_REQUIRED);
        }
        if (sizeBytes <= 0) {
            throw new ApiException(HrErrorCode.APPLICATION_CV_SIZE_INVALID);
        }
    }

    /**
     * Requires an application to already have a current CV reference.
     *
     * @param application application being read
     */
    public void requireCurrentCv(Application application) {
        if (application.getCvObjectKey() == null || application.getCvObjectKey().isBlank()) {
            throw new ApiException(HrErrorCode.APPLICATION_CV_NOT_FOUND);
        }
    }
}
