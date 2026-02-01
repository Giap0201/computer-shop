package com.nguyenhuugiap.computer_shop.exception;

public class StorageFileNotFoundException extends StorageException {


    public StorageFileNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageFileNotFoundException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }
}
