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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.commands.executor.CommandExec;
import me.tabinol.secuboid.commands.executor.CommandHelp;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * The Class OnCommand.
 */
public final class CommandListener implements CommandExecutor, TabCompleter {

    private final Secuboid secuboid;
    private final Map<String, Class<? extends CommandExec>> commandToClass;
    private final List<String> consoleCommands;
    private final List<String> playerCommands;

    /**
     * Instantiates a new on command.
     *
     * @param secuboid secuboid instance
     */
    public CommandListener(final Secuboid secuboid) {

        this.secuboid = secuboid;

        // Create Command list
        commandToClass = new TreeMap<>();
        consoleCommands = new ArrayList<>();
        playerCommands = new ArrayList<>();

        for (final CommandClassList presentClass : CommandClassList.values()) {
            // Store commands information
            final InfoCommand infoCommand = presentClass.getCommandClass().getAnnotation(InfoCommand.class);
            final Class<? extends CommandExec> commandClass = presentClass.getCommandClass();
            addCommandToList(infoCommand, infoCommand.name().toLowerCase(), commandClass);
            for (final String alias : infoCommand.aliases()) {
                addCommandToList(infoCommand, alias.toLowerCase(), commandClass);
            }
        }
    }

    private void addCommandToList(final InfoCommand infoCommand, final String command,
            final Class<? extends CommandExec> commandClass) {
        commandToClass.put(command, commandClass);
        playerCommands.add(command);
        if (infoCommand.allowConsole()) {
            consoleCommands.add(command);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // Others commands then /secuboid, /claim and /fd will not be send.
        final ArgList argList = new ArgList(secuboid, args, sender);
        try {
            // Check the command to send
            getCommand(sender, cmd, argList);
            return true;
            // If error on command, send the message to the player
        } catch (final SecuboidCommandException ex) {
            ex.notifySender();
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias,
            final String[] args) {
        final String firstChars = args[args.length - 1];

        // First arguments
        if (args.length <= 1) {
            if (sender instanceof Player) {
                // Player connected
                return filterList(playerCommands, firstChars);
            }
            // Console?
            return filterList(consoleCommands, firstChars);
        }

        // Next arguments

        // Take command
        final Class<? extends CommandExec> cv = commandToClass.get(args[0]);
        if (cv == null) {
            return Collections.emptyList();
        }

        // Take the line without the command
        final String line = StringChanges.arrayToString(args, 1, args.length - 2);
        final InfoCommand ci = cv.getAnnotation(InfoCommand.class);

        // Find match with regex
        for (final CompletionMap completionMap : ci.completion()) {
            final Pattern pattern = Pattern.compile(completionMap.regex(), Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(line).matches()) {
                return makeArgList(sender, completionMap.completions(), firstChars);
            }
        }

        // No match found
        return Collections.emptyList();
    }

    private List<String> makeArgList(final CommandSender sender, final String[] completions, final String firstChars) {
        final List<String> argList = new ArrayList<>();
        for (final String completion : completions) {
            switch (completion) {
            case "@approveLandList":
                argList.addAll(listApproveLandList(sender));
                break;
            case "@areaLand":
                argList.addAll(listAreaLand(sender));
                break;
            case "@boolean":
                argList.addAll(listBoolean());
                break;
            case "@command":
                argList.addAll(listCommand());
                break;
            case "@flag":
                argList.addAll(listFlag());
                break;
            case "@land":
                argList.addAll(listLand());
                break;
            case "@number":
                argList.addAll(Arrays.asList("1", "10", "100", "1000"));
                break;
            case "@player":
                argList.addAll(listPlayer());
                break;
            case "@playerContainer":
                argList.addAll(listPlayerContainer(firstChars));
                break;
            case "@permission":
                argList.addAll(listPermission());
                break;
            case "@type":
                argList.addAll(listType());
                break;
            default:
                argList.add(completion);
            }
        }
        return filterList(argList, firstChars);
    }

    private Set<String> listApproveLandList(final CommandSender sender) {
        if (sender.hasPermission("secuboid.collisionapprove")) {
            return secuboid.getLands().getApproves().getApproveList().keySet();
        }
        return Collections.emptySet();
    }

    private List<String> listAreaLand(final CommandSender sender) {
        final PlayerConfEntry playerConf = secuboid.getPlayerConf().get(sender);
        if (playerConf.getSelection() != null && playerConf.getSelection().hasSelection()) {
            final Land land = playerConf.getSelection().getLand();
            final List<String> areasStrs = land.getAreasKey().stream().map(key -> key.toString())
                    .collect(Collectors.toList());
            return areasStrs;
        }
        return Collections.emptyList();
    }

    private List<String> listBoolean() {
        return Arrays.asList("false", "true");
    }

    private List<String> listCommand() {
        return playerCommands;
    }

    private Set<String> listFlag() {
        return secuboid.getPermissionsFlags().getFlagTypeNames();
    }

    private List<String> listLand() {
        final List<String> argList = new ArrayList<>();
        for (final Land land : secuboid.getLands().getLands()) {
            argList.add(land.getName());
        }
        return argList;
    }

    private Set<String> listPlayer() {
        return secuboid.getPlayersCache().getPlayerNames();
    }

    private List<String> listPlayerContainer(final String firstChars) {
        final List<String> argList = new ArrayList<>();
        if (firstChars.matches("^.:")) {
            switch (firstChars.substring(0, 1).toLowerCase()) {
            case "b":
                argList.add("B:perm.perm");
                break;
            case "g":
                argList.add("G:group");
                break;
            case "p":
                argList.addAll(secuboid.getPlayersCache().getPlayerNames().stream()
                        .map(playerName -> String.format("P:%s", playerName)).collect(Collectors.toList()));
                break;
            default:
            }
        } else {
            argList.addAll(secuboid.getPlayersCache().getPlayerNames());
            for (final PlayerContainerType pcType : PlayerContainerType.values()) {
                if (pcType != PlayerContainerType.PLAYERNAME) {
                    if (pcType.hasParameter()) {
                        argList.add(String.format("%s:", pcType.getOneLetterCode()));
                    } else {
                        argList.add(pcType.getPrint());
                    }
                }
            }
        }
        return argList;
    }

    private Set<String> listPermission() {
        return secuboid.getPermissionsFlags().getPermissionTypeNames();
    }

    private List<String> listType() {
        return secuboid.getTypes().getTypes().stream().map(Type::getName).collect(Collectors.toList());
    }

    private List<String> filterList(final List<String> matches, final String firstChars) {
        return matches.stream().filter(match -> match.toLowerCase().startsWith(firstChars.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Gets command from args.
     *
     * @param sender  the sender
     * @param cmd     the command
     * @param argList arguments list
     * @throws SecuboidCommandException
     */
    private void getCommand(final CommandSender sender, final Command cmd, final ArgList argList)
            throws SecuboidCommandException {

        try {
            // Show help if there is no arguments
            if (argList.isLast()) {
                new CommandHelp(secuboid, null, sender, null).commandExecute();
                return;
            }

            final String command = argList.getNext().toLowerCase();

            // take the name
            final Class<? extends CommandExec> cv = commandToClass.get(command.toLowerCase());

            // The command does not exist
            if (cv == null) {
                throw new SecuboidCommandException(secuboid, "Command not existing", sender, "COMMAND.NOTEXIST",
                        "SECUBOID");
            }

            // Remove page from memory if needed
            if (cv != commandToClass.get("page")) {
                secuboid.getPlayerConf().get(sender).setChatPage(null);
            }

            // Do the command
            final InfoCommand ci = cv.getAnnotation(InfoCommand.class);
            final CommandExec ce = cv
                    .getConstructor(Secuboid.class, InfoCommand.class, CommandSender.class, ArgList.class)
                    .newInstance(secuboid, ci, sender, argList);
            if (ce.isExecutable()) {
                ce.commandExecute();
            }

            // a huge number of Exception to catch!
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender,
                    "GENERAL.ERROR");
        } catch (final InstantiationException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender,
                    "GENERAL.ERROR");
        } catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender,
                    "GENERAL.ERROR");
        } catch (final SecurityException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender,
                    "GENERAL.ERROR");
        } catch (final IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender,
                    "GENERAL.ERROR");
        } catch (final InvocationTargetException ex) {
            // Catched by SecuboidCommandException
        }
    }

    /**
     * Gets the info command.
     *
     * @param command the command
     * @return command information
     */
    public InfoCommand getInfoCommand(final String command) {

        final Class<?> infoClass = commandToClass.get(command.toLowerCase());

        if (infoClass == null) {
            return null;
        }

        return infoClass.getAnnotation(InfoCommand.class);
    }
}
