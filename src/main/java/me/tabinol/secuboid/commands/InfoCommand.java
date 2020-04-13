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
package me.tabinol.secuboid.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface InfoCommand. Annotation used for commands in
 * me.tabinol.secuboid.commands.executor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface InfoCommand {

    /**
     * Completion map with a regex and a completion list.
     */
    public @interface CompletionMap {

        /**
         * Regex to match for completion
         * 
         * @return the string
         */
        public String regex();

        /**
         * Argument list for command completion.<br>
         * {@literal @}approveLandList: for lands to approve<br>
         * {@literal @}areaLand: an areas for selected land<br>
         * {@literal @}boolean: a boolean (true, false)<br>
         * {@literal @}command: a command<br>
         * {@literal @}flag: a flag<br>
         * {@literal @}land: a land name<br>
         * {@literal @}permission: a permission<br>
         * {@literal @}player: a player name<br>
         * {@literal @}playerContainer: a player container name or player name<br>
         * {@literal @}type: a land type<br>
         * 
         * @return the string command list
         */
        public String[] completions();
    }

    /**
     * The command name.
     *
     * @return the string
     */
    String name();

    /**
     * Aliases.
     *
     * @return the string[]
     */
    String[] aliases() default {};

    /**
     * Allowing from console?
     *
     * @return true, if successful
     */
    boolean allowConsole() default false;

    /**
     * Need a parameter? If "true", no parameter = help
     *
     * @return true, if successful
     */
    boolean forceParameter() default false;

    /**
     * {@link CompletionMap}
     * 
     * @return the command completion map
     */
    CompletionMap[] completion() default {};
}
