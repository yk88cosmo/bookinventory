package com.example.bookInventory.exception;

public class MessageNotReadableException extends RuntimeException{
    public MessageNotReadableException(){
        super("Unable to read request data.");
    }
}
