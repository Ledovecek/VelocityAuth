package me.ledovec.auth.velocityauth.constants;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;

import java.time.Duration;

public final class Titles {

    public static void sendRedirectTitle(Player player) {
        player.sendTitlePart(TitlePart.TITLE, Component.text(" "));
        player.sendTitlePart(TitlePart.SUBTITLE, Component.text("§7Redirecting.."));
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO));
    }

}
