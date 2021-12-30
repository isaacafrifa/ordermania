package com.iam.exception;

public class ItemNotFound extends RuntimeException {
    /**
     * Default Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    //constructor
    public ItemNotFound() {
        super("Inventory item not found");
    }

    //customized msg constructor
    public ItemNotFound(String msg) {
        super(msg);
    }

}