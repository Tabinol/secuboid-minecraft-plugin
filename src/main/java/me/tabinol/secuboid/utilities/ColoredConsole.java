/**
 * Source from https://forums.bukkit.org/threads/util-colored-console-output.168889/
 */
package me.tabinol.secuboid.utilities;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi.Attribute;

public class ColoredConsole {

    private static final Map<ChatColor, String> ansicolors = new EnumMap<ChatColor, String>(
            ChatColor.class);
    private static final ChatColor[] colors = ChatColor.values();

    private static String colorize(String msg) {
        ansicolors.put(ChatColor.BLACK,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_BLUE,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_GREEN,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_AQUA,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_RED,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Attribute.RESET)
                .fg(Ansi.Color.MAGENTA).boldOff().toString());
        ansicolors.put(ChatColor.GOLD,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff()
                        .toString());
        ansicolors.put(ChatColor.GRAY,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff()
                        .toString());
        ansicolors.put(ChatColor.DARK_GRAY,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold()
                        .toString());
        ansicolors.put(ChatColor.BLUE,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold()
                        .toString());
        ansicolors.put(ChatColor.GREEN,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold()
                        .toString());
        ansicolors.put(ChatColor.AQUA,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold()
                        .toString());
        ansicolors.put(ChatColor.RED,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold()
                        .toString());
        ansicolors.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Attribute.RESET)
                .fg(Ansi.Color.MAGENTA).bold().toString());
        ansicolors.put(ChatColor.YELLOW,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold()
                        .toString());
        ansicolors.put(ChatColor.WHITE,
                Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold()
                        .toString());
        ansicolors.put(ChatColor.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW)
                .toString());
        ansicolors.put(ChatColor.BOLD, Ansi.ansi()
                .a(Attribute.UNDERLINE_DOUBLE).toString());
        ansicolors.put(ChatColor.STRIKETHROUGH,
                Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        ansicolors.put(ChatColor.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE)
                .toString());
        ansicolors.put(ChatColor.ITALIC, Ansi.ansi().a(Attribute.ITALIC)
                .toString());
        ansicolors.put(ChatColor.RESET, Ansi.ansi().a(Attribute.RESET)
                .toString());

        for (ChatColor c : colors) {
            if (!ansicolors.containsKey(c)) {
                msg = msg.replaceAll(c.toString(), "");
            } else {
                msg = msg.replaceAll(c.toString(), ansicolors.get(c));
            }
        }
        return msg;
    }

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void info(String msg) {
        if (OS.indexOf("win") >= 0) {
            AnsiConsole.out.print(colorize(msg)
                    + Ansi.ansi().reset().toString());
        } else {
            System.out.println(colorize(msg) + Ansi.ansi().reset().toString());
        }

    }
}
