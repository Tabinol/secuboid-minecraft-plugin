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
package app.secuboid.api.messages;

import app.secuboid.api.exceptions.SecuboidRuntimeException;

import java.lang.reflect.InvocationTargetException;

/**
 * This is the record to gets a message from the yaml. The goal here is to
 * avoid a message to a not existing path.
 */
public interface MessagePath {

    /**
     * Creates a new instance of message path.
     *
     * @param yamlPath     the yaml path
     * @param replacedTags the tags to replace
     * @param args         the arguments to put in place of tags
     * @return a new instance
     */
    static MessagePath newInstance(String yamlPath, String[] replacedTags, Object[] args) {
        try {
            Class<?> clazz = Class.forName("app.secuboid.core.messages.MessagePathImpl");
            return (MessagePath) clazz.getDeclaredConstructor(String.class, String[].class, Object[].class)
                    .newInstance(yamlPath, replacedTags, args);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new SecuboidRuntimeException("Problem with \"app.secuboid.core.messages.MessagePathImpl\" new instance", e);
        }
    }

    /**
     * Gets the yaml path.
     *
     * @return the yaml path
     */
    String getYamlPath();

    /**
     * Gets the tags to replace.
     *
     * @return the tags to replace
     */
    String[] getReplacedTags();

    /**
     * Gets the arguments to put in place of tags.
     *
     * @return the arguments to put in place of tags
     */
    Object[] getArgs();
}
