package me.ledovec.velocityauth.exceptions;

public class InvalidPlayerException extends RuntimeException {

    public InvalidPlayerException() {
        super("Invalid player provided");
    }

}
