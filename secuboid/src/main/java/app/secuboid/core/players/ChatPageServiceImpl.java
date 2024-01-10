/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.core.players;

import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.players.ChatPageService;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.core.messages.MessagePaths;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import java.util.*;
import java.util.function.IntFunction;

public class ChatPageServiceImpl implements ChatPageService {

    private static final String RUN_COMMAND_PAGE_PREFIX = "/sd page ";
    private static final int PAGE_WIDTH = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH;
    private static final int PAGE_HEIGHT = ChatPaginator.OPEN_CHAT_PAGE_HEIGHT - 2;

    private final MessageManagerService messageManagerService;

    private final Map<CommandSenderInfo, ChatPage> commandSenderInfoToChatPage;

    public ChatPageServiceImpl(MessageManagerService messageManagerService) {
        this.messageManagerService = messageManagerService;

        commandSenderInfoToChatPage = new HashMap<>();
    }

    @Override
    public void show(CommandSenderInfo commandSenderInfo, String subject, String text) {
        ChatPaginator.ChatPage page = ChatPaginator.paginate(text, 1, PAGE_WIDTH, PAGE_HEIGHT);
        int totalPages = page.getTotalPages();
        ChatPage chatPage = new ChatPage(subject, text, totalPages);
        commandSenderInfoToChatPage.put(commandSenderInfo, chatPage);
        show(commandSenderInfo.sender(), chatPage, page, totalPages);
    }

    @Override
    public void show(CommandSenderInfo commandSenderInfo, int pageNumber) {
        CommandSender sender = commandSenderInfo.sender();
        ChatPage chatPage = commandSenderInfoToChatPage.get(commandSenderInfo);

        if (chatPage == null) {
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.chatPageNotAvailable());
            return;
        }

        ChatPaginator.ChatPage page = ChatPaginator.paginate(chatPage.getText(), pageNumber, PAGE_WIDTH, PAGE_HEIGHT);
        show(sender, chatPage, page, pageNumber);
    }

    @Override
    public int getTotalPages(CommandSenderInfo commandSenderInfo) {
        return Optional.ofNullable(commandSenderInfoToChatPage.get(commandSenderInfo)).map(ChatPage::getTotalPages).orElse(0);
    }

    @Override
    public void remove(CommandSenderInfo commandSenderInfo) {
        commandSenderInfoToChatPage.remove(commandSenderInfo);
    }

    private void show(CommandSender sender, ChatPage chatPage, ChatPaginator.ChatPage page
            , int pageNumber) {
        int totalPages = chatPage.getTotalPages();

        if (pageNumber < 1 || pageNumber > totalPages) {
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.chatPageNotFound(1, totalPages));
            return;
        }

        messageManagerService.sendMessage(sender, MessageType.TITLE, MessagePaths.chatPageHeader(chatPage.getSubject(),
                pageNumber, totalPages));
        sender.sendMessage(page.getLines());

        if (totalPages > 1) {
            showClickableText(sender, pageNumber, totalPages);
        }

    }

    private void showClickableText(CommandSender sender, int pageNumber, int totalPages) {
        List<TextComponent> textComponents = new ArrayList<>();

        if (pageNumber > 1) {
            TextComponent textComponent = addPageTextClickable(MessagePaths::chatPageFooterLeftActive, pageNumber - 1);
            textComponents.add(textComponent);
        }

        if (pageNumber < totalPages) {
            if (!textComponents.isEmpty()) {
                textComponents.add(new TextComponent(" "));
            }
            TextComponent textComponent = addPageTextClickable(MessagePaths::chatPageFooterRightActive, pageNumber + 1);
            textComponents.add(textComponent);
        }

        sender.spigot().sendMessage(textComponents.toArray(TextComponent[]::new));
    }

    private TextComponent addPageTextClickable(IntFunction<MessagePath> messagePathFunc,
                                               int targetPageNumber) {
        MessagePath messagePath = messagePathFunc.apply(targetPageNumber);
        TextComponent textComponent = messageManagerService.getTextComponent(MessageType.CLICKABLE, messagePath);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                RUN_COMMAND_PAGE_PREFIX + targetPageNumber);
        textComponent.setClickEvent(clickEvent);

        return textComponent;
    }
}
