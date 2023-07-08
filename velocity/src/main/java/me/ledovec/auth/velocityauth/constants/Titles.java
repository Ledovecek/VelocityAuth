package me.ledovec.auth.velocityauth.constants;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;

import java.time.Duration;

public final class Titles {

    public static void sendRedirectTitle(Player player) {
        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO);
        Title title = Title.title(Component.text(" "), Component.text("§7Redirecting.."), times);
        player.showTitle(title);
    }

    public static void showRegisterTitle(Player player) {
        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO);
        Title title = Title.title(Component.text(" "), Component.text("§7Use §e/register <password> <password>"), times);
        player.showTitle(title);
    }

    public static void showLoginTitle(Player player) {
        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO);
        Title title = Title.title(Component.text(" "), Component.text("§7Use §e/login <password>"), times);
        player.showTitle(title);
        System.out.println("twl");
    }

}
