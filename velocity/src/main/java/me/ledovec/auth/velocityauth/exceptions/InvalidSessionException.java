package me.ledovec.auth.velocityauth.exceptions;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("Unable to find session");
    }

}
