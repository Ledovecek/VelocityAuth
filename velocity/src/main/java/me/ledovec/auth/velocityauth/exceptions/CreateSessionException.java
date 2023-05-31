package me.ledovec.auth.velocityauth.exceptions;

public class CreateSessionException extends RuntimeException {

    public CreateSessionException() {
        super("Unable to create session");
    }

}
