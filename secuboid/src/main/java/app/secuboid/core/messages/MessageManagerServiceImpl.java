/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.core.messages;

import app.secuboid.api.flagtypes.FlagType;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.messages.*;
import app.secuboid.core.config.ConfigService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

import static java.lang.String.format;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

@RequiredArgsConstructor
public class MessageManagerServiceImpl implements MessageManagerService {

    private static final String ERROR_MESSAGE_NOT_LOADED = "Message not yet loaded for the plugin %s";
    private static final String FILENAME_LANG_PREFIX = "lang/";
    private static final String FILENAME_LANG_FLAGS_PREFIX = "lang/flags-";
    private static final String FILENAME_LANG_SUFFIX = ".yml";
    private static final String LANG_DEFAULT = "en";

    private final Plugin plugin;
    private final ConfigService configService;

    private FileConfiguration fileConfiguration = null;
    private FileConfiguration fileConfigurationFlags = null;

    @Override
    public void onEnable(boolean isServerBoot) {

        String lang = configService.getLang();

        String langFilename = getFilePath(FILENAME_LANG_PREFIX, lang);
        String defaultLangFilename = getFilePath(FILENAME_LANG_PREFIX, LANG_DEFAULT);
        fileConfiguration = getFileConfiguration(lang, langFilename, defaultLangFilename);

        String langFilenameFlags = getFilePath(FILENAME_LANG_FLAGS_PREFIX, lang);
        String defaultLangFilenameFlags = getFilePath(FILENAME_LANG_FLAGS_PREFIX, LANG_DEFAULT
        );
        fileConfigurationFlags = getFileConfiguration(lang, langFilenameFlags, defaultLangFilenameFlags);
    }

    @Override
    public String get(MessageType messageType, MessagePath path) {
        if (fileConfiguration == null) {
            String message = format(ERROR_MESSAGE_NOT_LOADED, plugin.getName());
            Log.log().log(SEVERE, message);
            return message;
        }

        String yamlPath = path.getYamlPath();
        String format = fileConfiguration.getString(yamlPath);

        if (format == null) {
            String message = format("Message path not found path=%s", path);
            Log.log().log(WARNING, () -> format("[Plugin:%s] %s", plugin.getName(), message));
            return message;
        }

        String[] tags = path.getReplacedTags();
        Object[] args = path.getArgs();
        return formatMessage(messageType, format, tags, args);
    }

    @Override
    public void sendMessage(CommandSender sender, MessageType messageType, MessagePath path) {
        String message = get(messageType, path);
        sender.sendMessage(message);
    }

    @Override
    public void broadcastMessage(MessageType messageType, MessagePath path) {
        String message = get(messageType, path);
        plugin.getServer().broadcastMessage(message);
    }

    @Override
    public TextComponent getTextComponent(MessageType messageType, MessagePath path) {
        String message = get(messageType, path);

        return new TextComponent(message);
    }

    @Override
    public String getFlagDescription(FlagType flagType) {
        if (fileConfigurationFlags == null) {
            String message = format(ERROR_MESSAGE_NOT_LOADED, plugin.getName());
            Log.log().log(SEVERE, message);
            return message;
        }

        String name = flagType.getName();
        String description = fileConfigurationFlags.getString(name);
        if (description == null) {
            String message = format("Flag description not found: %s", name);
            Log.log().log(WARNING, () -> format("[Plugin:%s] %s", plugin.getName(), message));
            return message;
        }

        return description;
    }

    @Override
    public void sendFlagDescription(CommandSender sender, FlagType flagType) {
        String description = getFlagDescription(flagType);
        sender.sendMessage(description);
    }

    private String getFilePath(String prefix, String middle) {
        return prefix + middle + FILENAME_LANG_SUFFIX;
    }

    private FileConfiguration getFileConfiguration(String lang, String langFilename, String defaultLangFilename) {
        InputStream inputStream = getInputStream(plugin, langFilename);

        if (inputStream == null) {
            Log.log().log(WARNING,
                    () -> format("[Plugin:%s] Switch to default because this language is not found: %s:%s",
                            plugin.getName(), lang, langFilename));
            inputStream = getInputStream(plugin, defaultLangFilename);
        }

        if (inputStream == null) {
            Log.log().log(Level.SEVERE, () -> format("[Plugin:%s] No language file found", plugin.getName()));
            return new YamlConfiguration();
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        return YamlConfiguration.loadConfiguration(inputStreamReader);
    }

    private InputStream getInputStream(Plugin plugin, String langFilename) {

        // Try first in class loader (for tests)
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(langFilename);
        if (inputStream == null && plugin != null) {

            // Try inside the jar file (normal use)
            inputStream = plugin.getResource(langFilename);
        }

        return inputStream;
    }

    private String formatMessage(MessageType messageType, String format, String[] tags, Object[] args) {
        String prefix = messageType.prefix;
        String color = messageType.color;
        int argsLength = args.length;

        String[] coloredArgs = new String[argsLength];
        for (int i = 0; i < argsLength; i++) {
            coloredArgs[i] = colorArg(messageType, color, args[i]);
        }

        return prefix + color + formatRaw(format, tags, coloredArgs);
    }

    private String formatRaw(String format, String[] tags, String[] args) {
        String output = format;
        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            String arg = args[i];
            output = output.replace(tag, arg);
        }

        return output;
    }

    private String colorArg(MessageType messageType, String color, Object arg) {
        if (messageType == MessageType.NO_COLOR) {
            return Objects.toString(arg);
        }

        if (arg instanceof MessageFormatter formatter) {
            return formatter + color;
        }

        if (arg instanceof Number number) {
            return (MessageColor.NUMBER + number + color)
                    .replaceAll("([.,])", color + "$1" + MessageColor.NUMBER);
        }

        if (arg instanceof LocationPath locationPath) {
            String pathName = locationPath.getPathName();
            return (MessageColor.NAME + pathName + color)
                    .replaceAll("([@/:])", color + "$1" + MessageColor.NAME);
        }

        return MessageColor.NAME + arg + color;
    }
}
