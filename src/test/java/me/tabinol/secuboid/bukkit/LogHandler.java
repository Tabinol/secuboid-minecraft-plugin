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
package me.tabinol.secuboid.bukkit;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LogHandler
 */
public final class LogHandler extends Handler {

    final Class<?> testClass;
    final List<Map.Entry<Level, String>> levelToMessages;
    Level lastLevel;
    String lastMessage;

    public LogHandler(final Class<?> testClass) {
        this.testClass = testClass;
        levelToMessages = new ArrayList<>();
    }

    public Logger createLogger() {
        final Logger logger = Logger.getLogger(testClass.getSimpleName());
        this.setLevel(Level.FINEST);
        logger.setUseParentHandlers(false);
        logger.addHandler(this);
        logger.setLevel(Level.ALL);
        return logger;
    }

    public Map.Entry<Level, String> getLast() {
        return levelToMessages.get(levelToMessages.size() - 1);
    }

    public boolean isMessageContainsAny(final String message) {
        return levelToMessages.stream().anyMatch(entry -> entry.getValue().contains(message));
    }

    public Level checkLevel() {
        return lastLevel;
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public void flush() {
        // Nothing to do
    }

    @Override
    public void publish(final LogRecord record) {
        final String className = testClass.getSimpleName();
        final Level level = record.getLevel();
        final String message = record.getMessage();
        System.out.println(String.format("[%s][%s] %s", className, level.getName(), message));
        levelToMessages.add(new AbstractMap.SimpleEntry<>(level, message));
    }
}