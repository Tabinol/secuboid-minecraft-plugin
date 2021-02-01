/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;

/**
 * The Class Lang.
 */
public final class Lang {

    private static String STR_POURCENT = "%";

    /**
     * The actual version of lang file.
     */
    private final int actualVersion;

    /**
     * The lang.
     */
    private String lang = null;

    /**
     * The lang file.
     */
    private File langFile;

    /**
     * The langconfig.
     */
    final FileConfiguration langconfig;

    /**
     * The plugin.
     */
    private final Secuboid secuboid;

    /**
     * Instantiates a new lang.
     *
     * @param secuboid secuboid instance
     */
    public Lang(final Secuboid secuboid) {
        this.secuboid = secuboid;
        actualVersion = MavenAppProperties.getPropertyInt("langVersion", 1);
        this.langconfig = new YamlConfiguration();
    }

    /**
     * Load config.
     *
     * @param isServerBoot is first boot?
     */
    public void loadConfig(final boolean isServerBoot) {
        this.lang = secuboid.getConf().getLang();
        this.langFile = new File(secuboid.getDataFolder() + "/lang/", lang + ".yml");
        if (secuboid.getConf().getLang() != null) {
            copyLang();
            loadYamls();
        }
        if (isServerBoot) {
            checkVersion();
        }
    }

    /**
     * Copyt the language file.
     */
    private void copyLang() {
        if (!langFile.exists()) {
            if (!langFile.getParentFile().exists() && !langFile.getParentFile().mkdirs()) {
                secuboid.getLogger()
                        .severe("Unable to create the directory " + langFile.getParentFile().getPath() + ".");
            }
            try {
                FileCopy.copyTextFromJav(secuboid.getResource("lang/" + lang + ".yml"), langFile);
            } catch (final IOException e) {
                secuboid.getLogger().severe("Unable to copy language file from jar.");
            }
        }
    }

    /**
     * Load yamls.
     */
    private void loadYamls() {
        try {
            langconfig.load(langFile);
        } catch (final IOException | InvalidConfigurationException e) {
            secuboid.getLogger().severe("Error on language load: " + e.getLocalizedMessage());
        }
    }

    // Check if it is the next version, if not, the file will be renamed

    /**
     * Check version.
     */
    private void checkVersion() {

        final int fileVersion = langconfig.getInt("VERSION");

        // We must rename the file and activate the new file
        if (actualVersion != fileVersion) {
            if (!langFile.renameTo(new File(secuboid.getDataFolder() + "/lang/", lang + ".yml.v" + fileVersion))) {
                secuboid.getLogger().severe("Unable to rename the old language file.");
            }
            loadConfig(false);
            secuboid.getLogger().info("There is a new language file. Your old language file was renamed \"" + lang
                    + ".yml.v" + fileVersion + "\".");
        }
    }

    /**
     * Gets the message.
     *
     * @param path  the path
     * @param param the param
     * @return the message
     */
    public String getMessage(final String path, final String... param) {

        String message = langconfig.getString(path);

        if (message == null) {
            return "MESSAGE NOT FOUND FOR PATH: " + path;
        }
        if (param.length >= 1) {
            // New "%i%"
            for (int i = 0; i < param.length; i++) {
                message = message.replaceFirst(STR_POURCENT + (i + 1) + STR_POURCENT, param[i]);
            }

            // Legacy "%"
            for (int i = 0; i < param.length; i++) {
                message = message.replaceFirst(STR_POURCENT, param[i]);
            }
        }

        return message;
    }

    /**
     * Gets the help.
     *
     * @param mainCommand the main command
     * @param commandName the command name
     * @return the help
     */
    public String getHelp(final String mainCommand, final String commandName) {

        final ConfigurationSection helpSec = langconfig
                .getConfigurationSection("HELP." + mainCommand + "." + commandName);

        // No help for this command?
        if (helpSec == null) {
            return null;
        }

        final Map<String, Object> valueList = helpSec.getValues(false);
        final StringBuilder sb = new StringBuilder();

        for (int t = 1; t <= valueList.size(); t++) {
            sb.append((String) valueList.get(t + "")).append(Config.NEWLINE);
        }

        return sb.toString();
    }
}
