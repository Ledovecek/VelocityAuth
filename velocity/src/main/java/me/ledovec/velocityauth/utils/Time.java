package me.ledovec.velocityauth.utils;

import java.util.Date;

public final class Time {

    public static long getCurrentTime() {
        return new Date().getTime();
    }

}
