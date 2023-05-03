package me.ledovec.velocityauth.session;

public interface Session<T> {

    T create();

    long getId();

}
