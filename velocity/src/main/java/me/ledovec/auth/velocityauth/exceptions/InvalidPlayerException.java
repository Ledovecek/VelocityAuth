package me.ledovec.auth.velocityauth.exceptions;

public class InvalidPlayerException extends RuntimeException {

    public InvalidPlayerException() {
        super("Invalid player provided");
    }

}
