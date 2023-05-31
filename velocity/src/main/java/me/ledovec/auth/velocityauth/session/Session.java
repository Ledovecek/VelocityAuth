package me.ledovec.auth.velocityauth.session;

public interface Session<T> {

    T create();

    long getId();

}
