/*
 * This file is part of JadedParty, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.jadedparty.commands;

import net.jadedmc.jadedparty.JadedPartyPlugin;
import net.jadedmc.jadedparty.party.Party;
import net.jadedmc.jadedparty.party.PartyRank;
import net.jadedmc.jadedparty.utils.ChatUtils;
import net.jadedmc.jadedparty.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class runs the /party command, which controls everything about parties.
 */
public class PartyCMD extends Command implements TabExecutor {
    private final JadedPartyPlugin plugin;

    /**
     * Executes the command.
     */
    public PartyCMD(JadedPartyPlugin plugin) {
        super("party", "", "p");
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if(args.length == 0) {
            helpCMD(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> createCMD(player);
            case "disband" -> disbandCMD(player);
            case "invite", "i" -> inviteCMD(player, args);
            case "leave" -> leaveCMD(player);
            case "list", "l" -> listCMD(player);
            case "promote", "p" -> promoteCMD(player, args);
            case "accept", "a" -> acceptCMD(player, args);
            case "decline", "d" -> declineCMD(player, args);
            case "kick" -> kickCMD(player, args);
            case "summon" -> summonCMD(player);
            case "help", "?" -> helpCMD(player);
            default -> inviteCMD(player, new String[]{"invite", args[0]});
        }
    }

    /**
     * Runs the /party accept command.
     * @param player Player who ran the command.
     * @param args Command arguments.
     */
    private void acceptCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party accept [player]");
            return;
        }

        // Gets the target player.
        ProxiedPlayer target =  plugin.getProxy().getPlayer(args[1]);

        // Makes sure the target player is online.
        if(target == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not online");
            return;
        }

        // Gets the target party.
        Party party = plugin.partyManager().getParty(target);

        // Makes sure the player is in a party.
        if(party == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not in a party");
            return;
        }

        // Makes sure the player has an invitation to the party.
        if(!party.getInvites().contains(player)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You do not have an invite to that party.");
            return;
        }

        party.addPlayer(player);
        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + player.getName() + " &ahas joined the party.");
    }

    /**
     * Runs the /party create command.
     * @param player Player who ran the command.
     */
    private void createCMD(ProxiedPlayer player) {
        // Makes sure the player is not already in a party.
        if(plugin.partyManager().getParty(player) != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are already in a party.");
            return;
        }

        // Creates the party.
        plugin.partyManager().createParty(player);
        ChatUtils.chat(player, "<green><bold>Party</bold> <dark_gray>» <green>Party as been created.");
    }

    /**
     * Runs the /party decline command.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void declineCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party decline [player]");
            return;
        }

        // Gets the target player.
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[1]);

        // Makes sure the target player is online.
        if(target == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not online");
            return;
        }

        // Gets the target player's party.
        Party party = plugin.partyManager().getParty(target);

        // Makes sure the party exists.
        if(party == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not in a party");
            return;
        }

        // Makes sure the player has an invite to the party.
        if(!party.getInvites().contains(player)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You do not have an invite to that party.");
            return;
        }

        // Declines the invite.
        party.removeInvite(player);
        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + player.getName() + " &ahas declined the invite.");
        ChatUtils.chat(player, "<green><bold>Party</bold> <dark_gray>» <green>You have declined the invite.");
    }

    /**
     * Runs the /party disband command.
     * @param player Player who ran the command.
     */
    private void disbandCMD(ProxiedPlayer player) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Makes sure the player has permission to disband the party.
        if(party.getRank(player) != PartyRank.LEADER) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can disband the party!");
            return;
        }

        // Disbands the party.
        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <green>Party has been disbanded.");
        plugin.partyManager().disbandParty(party);
    }

    /**
     * Runs the /party help command.
     * @param player Player who ran the command.
     */
    private void helpCMD(ProxiedPlayer player) {
        ChatUtils.chat(player, "&8&m+-----------------------***-----------------------+");
        ChatUtils.centeredChat(player, "&a&lParty Commands");
        ChatUtils.chat(player, "&a  /party chat");
        ChatUtils.chat(player, "&a  /party create");
        ChatUtils.chat(player, "&a  /party disband");
        ChatUtils.chat(player, "&a  /party invite [player]");
        ChatUtils.chat(player, "&a  /party leave");
        ChatUtils.chat(player, "&a  /party list");
        ChatUtils.chat(player, "&a  /party promote");
        ChatUtils.chat(player, "&8&m+-----------------------***-----------------------+");
    }

    /**
     * Runs the /player invite command.
     * @param player Player who ran the command.
     * @param args Command arguments.
     */
    private void inviteCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player has enterted a username to invite.
        if(args.length != 2) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party invite [player]");
            return;
        }

        // Makes sure the target is online.
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[1]);
        if(target == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That person is not online.");
            return;
        }

        // Makes sure they are not already in a party.
        if(plugin.partyManager().getParty(target) != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>They are already in a party.");
            return;
        }

        // Makes sure the player is not trying to invite themselves.
        if(target.equals(player)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You cannot invite yourself.");
            return;
        }

        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            // Creates the party.
            plugin.partyManager().createParty(player);
            ChatUtils.chat(player, "<green><bold>Party</bold> <dark_gray>» <green>Party as been created.");
        }

        // Gets the player's party,
        Party party = plugin.partyManager().getParty(player);

        // Makes sure the player has permission to invite someone to the party.
        if(!(party.getRank(player) == PartyRank.LEADER || party.getRank(player) == PartyRank.MODERATOR)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not allowed to invite people to the party.");
            return;
        }

        // Makes sure the player wasn't already invited.
        if(party.getInvites().contains(target)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You already have a pending invite to that person.");
            return;
        }

        party.invitePlayer(target);

        ChatUtils.chat(target, "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        ChatUtils.centeredChat(target, "&a&lParty Invite");
        ChatUtils.chat(target, "");
        ChatUtils.centeredChat(target, "&f" + player.getName() + " &7has invited you to join their party!");
        ChatUtils.chat(target, "");
        ChatUtils.chat(target, "  <dark_gray>» <click:suggest_command:'/party accept " + player.getName() + "'><hover:show_text:'<green>Click to accept'><green>/party accept " + player.getName() + "</hover></click>");
        ChatUtils.chat(target, "  <dark_gray>» <click:suggest_command:'/party deny " + player.getName() + "'><hover:show_text:'<red>Click to deny'><red>/party deny " + player.getName() + "</hover></click>");
        ChatUtils.chat(target, "");
        ChatUtils.chat(target, "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + target.getName() + " &ahas been invited to the party.");
    }

    public void joinCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player has entered a username to invite.
        if(args.length != 2) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party join [player]");
            return;
        }

        // Makes sure the target is online.
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[1]);
        if(target == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That person is not online.");
            return;
        }

        // Makes sure the target is in a party.
        if(plugin.partyManager().getParty(target) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>They are not in a party.");
            return;
        }

        // Make sure the sender isn't in a party.
        if(plugin.partyManager().getParty(player) != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are already in a party.");
            return;
        }

        Party party = plugin.partyManager().getParty(target);

        // Make sure the party is public.
        if(!party.isPublic()) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That party is not public.");
            return;
        }

        party.addPlayer(player);
        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + player.getName() + " &ahas joined the party.");
    }

    public void kickCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party kick [player]");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Makes sure they have permission.
        if(party.getRank(player) != PartyRank.LEADER && party.getRank(player) != PartyRank.MODERATOR) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>Only party moderators can kick members!");
            return;
        }

        // Get the player who the player is trying to promote.
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[1]);

        // Makes sure the target is in the party.
        if(target == null || !party.getMembers().contains(target)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not in your party!");
            return;
        }

        if(party.getRank(player) == party.getRank(target) || party.getRank(target) == PartyRank.LEADER) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You cannot kick that player!!");
            return;
        }

        party.removePlayer(target);
        party.sendMessage("&a&lParty &8» &f" + target.getName() + " &awas kicked from the party by &f" + player.getName() + "&a.");
        ChatUtils.chat(target, "<green><bold>Party</bold> <dark_gray>» <green>You have been kicked from the party.");
    }

    /**
     * Runs the /party leave command.
     * @param player Player who ran the command.
     */
    private void leaveCMD(ProxiedPlayer player) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Removes the player
        party.removePlayer(player);
        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + player.getName() + " &ahas left the party.");
        ChatUtils.chat(player, "<green><bold>Party</bold> <dark_gray>» <green>You have left the party.");
    }

    /**
     * Runs the /party list command.
     * @param player Player who ran the command.
     */
    private void listCMD(ProxiedPlayer player) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Gets all the party members and their roles.
        String leader = "";
        List<String> moderators = new ArrayList<>();
        List<String> members = new ArrayList<>();
        for(ProxiedPlayer member : party.getMembers()) {
            switch (party.getRank(member)) {
                case LEADER -> leader = member.getName();
                case MODERATOR -> moderators.add(member.getName());
                case MEMBER -> members.add(member.getName());
            }
        }

        // Displays the list
        ChatUtils.chat(player, "&8&m+-----------------------***-----------------------+");
        ChatUtils.centeredChat(player, "&a&lParty Members");
        ChatUtils.chat(player, "");
        ChatUtils.chat(player, "&aLeader &7» &f" + leader);

        // Only show moderators if there are any.
        if(moderators.size() > 0) {
            ChatUtils.chat(player, "&aModerators &7[" + moderators.size() + "] » &f" + StringUtils.join(moderators, ", "));
        }

        ChatUtils.chat(player, "&aMembers &7[" + members.size() + "] » &f" + StringUtils.join(members, ", "));
        ChatUtils.chat(player, "");
        ChatUtils.chat(player, "&8&m+-----------------------***-----------------------+");
    }

    /**
     * Runs the /party promote command.
     * @param player Player to promote.
     */
    private void promoteCMD(ProxiedPlayer player, String[] args) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/party promote [player]");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Makes sure they have permission.
        if(party.getRank(player) != PartyRank.LEADER) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can promote members!");
            return;
        }

        // Get the player who the player is trying to promote.
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[1]);

        // Makes sure the target is in the party.
        if(target == null || !party.getMembers().contains(target)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not in your party!");
            return;
        }

        // Makes sure the player isn't trying to promote themselves.
        if(target.equals(player)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are already the party leader!");
            return;
        }

        // Promotes the player.
        if(party.getRank(target) == PartyRank.MEMBER) {
            party.setRank(target, PartyRank.MODERATOR);
            party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + target.getName() + " &ahas been promoted to moderator.");
        }
        else {
            party.setRank(player, PartyRank.MODERATOR);
            party.setRank(target, PartyRank.LEADER);
            party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + target.getName() + " &ahas been promoted to party leader.");
        }
    }

    /**
     * Summons all members of the party to the leader's server.
     * @param player Player using the command.
     */
    private void summonCMD(ProxiedPlayer player) {
        // Makes sure the player is in a party.
        if(plugin.partyManager().getParty(player) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Gets the player's party.
        Party party = plugin.partyManager().getParty(player);

        // Makes sure they have permission.
        if(party.getRank(player) != PartyRank.LEADER) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can summon players!");
            return;
        }

        party.sendMessage("<green><bold>Party</bold> <dark_gray>» <green>You have been summoned to <white>" + player.getName() + "<green>'s server.");
        for(ProxiedPlayer member : party.getMembers()) {
            if(member.equals(player)) {
                continue;
            }

            member.connect(player.getServer().getInfo());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer player)) {
          return null;
        }

        if(args.length == 1) {
            List<String> suggestions = new ArrayList<>();

            for(ProxiedPlayer serverPlayer : player.getServer().getInfo().getPlayers()) {
                if(serverPlayer.getName().startsWith(args[0])) {
                    suggestions.add(serverPlayer.getName());
                }
            }

            return suggestions;
        }


        return Collections.emptyList();
    }
}