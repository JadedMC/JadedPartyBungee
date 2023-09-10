package net.jadedmc.jadedpartybungee.party;

import net.jadedmc.jadedpartybungee.JadedPartyBungee;
import net.jadedmc.jadedpartybungee.utils.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Represents a group of players playing together.
 */
public class Party {
    private final JadedPartyBungee plugin;
    private final Map<UUID, PartyRank> members = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();
    private final Map<UUID, String> invites = new HashMap<>();

    // Party Settings
    private boolean publicParty;

    public Party(JadedPartyBungee plugin, ProxiedPlayer leader) {
        this.plugin = plugin;
        members.put(leader.getUniqueId(), PartyRank.LEADER);

        // Sets the default settings of the party.
        publicParty = false;
    }

    /**
     * Add a player to the party.
     * @param player Player to add.
     */
    public void addPlayer(ProxiedPlayer player) {
        members.put(player.getUniqueId(), PartyRank.MEMBER);
        invites.remove(player.getUniqueId());

        syncData();
    }

    /**
     * Get all current invites.
     * @return All current invites.
     */
    public Collection<ProxiedPlayer> getInvites() {
        Collection<ProxiedPlayer> partyInvites = new ArrayList<>();

        invites.keySet().forEach(uuid -> {
            if(plugin.getProxy().getPlayer(uuid) != null) {
                partyInvites.add(plugin.getProxy().getPlayer(uuid));
            }
        });

        return partyInvites;
    }

    /**
     * Gets the leader of the party.
     * @return Party leader.
     */
    public ProxiedPlayer getLeader() {
        // Loops through all party members.
        for(ProxiedPlayer player : getMembers()) {
            // Checks if they are party leader.
            if(getRank(player) == PartyRank.LEADER) {
                return player;
            }
        }

        // If no party leader found, returns null.
        return null;
    }

    /**
     * Get all members in the party.
     * @return List of all party members.
     */
    public List<ProxiedPlayer> getMembers() {
        List<ProxiedPlayer> partyMembers = new ArrayList<>();

        members.keySet().forEach(uuid -> {
            if(plugin.getProxy().getPlayer(uuid) != null) {
                partyMembers.add(plugin.getProxy().getPlayer(uuid));
            }
        });

        return partyMembers;
    }

    /**
     * Get the rank of a player in the party.
     * Returns null if they are not in the party.
     * @param player Player to get rank of.
     * @return PartyRank of the player.
     */
    public PartyRank getRank(ProxiedPlayer player) {
        if(members.containsKey(player.getUniqueId())) {
            return members.get(player.getUniqueId());
        }

        return null;
    }

    /**
     * Get the UUID of the party.
     * Generated on party creation.
     * @return UUID of the party.
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Invites a player to the party.
     * @param player Player being invited to the party.
     */
    public void invitePlayer(ProxiedPlayer player) {
        invites.put(player.getUniqueId(), player.getName());

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if(!invites.containsKey(player.getUniqueId())) {
                return;
            }

            sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + invites.get(player.getUniqueId()) + "&a's invite has expired.");
            invites.remove(player.getUniqueId());
        }, 60, TimeUnit.SECONDS);
    }

    /**
     * Removes the invite to a player.
     * @param player Player to remove invite to.
     */
    public void removeInvite(ProxiedPlayer player) {
        invites.remove(player.getUniqueId());
    }

    /**
     * Removes a player from the party.
     * @param player Player to remove.
     */
    public void removePlayer(ProxiedPlayer player) {
        if(getRank(player) == PartyRank.LEADER) {
            plugin.partyManager().disbandParty(this);
            return;
        }

        members.remove(player.getUniqueId());
        syncData();
    }

    /**
     * Sends a chat message to all party members
     * @param message Message to send to party members.
     */
    public void sendMessage(String message) {
        for(ProxiedPlayer player : getMembers()) {
            ChatUtils.chat(player, message);
        }
    }

    /**
     * Chance a player's rank in the party.
     * @param player Player to change the rank of.
     * @param rank Rank to set the player to.
     */
    public void setRank(ProxiedPlayer player, PartyRank rank) {
        members.put(player.getUniqueId(), rank);
    }

    public void syncData() {
        String message = uuid + "~" + getLeader().getUniqueId();

        if(getMembers().size() > 1) {
            message += "~";

            int i = 0;
            for(ProxiedPlayer player : getMembers()) {
                if(getRank(player) == PartyRank.LEADER) {
                    continue;
                }

                message += player.getUniqueId().toString();
                i++;

                if(i < getMembers().size() - 1) {
                    message += ":";
                }
            }
        }

        String finalMessage = message;
        getMembers().forEach(player -> plugin.sendCustomData(player, "sync", finalMessage));
    }

    public void syncData(ProxiedPlayer player) {
        String message = uuid + "~" + getLeader().getUniqueId();

        if(getMembers().size() > 1) {
            message += "~";

            int i = 0;
            for(ProxiedPlayer member : getMembers()) {
                if(getRank(member) == PartyRank.LEADER) {
                    continue;
                }

                message += member.getUniqueId().toString();
                i++;

                if(i < getMembers().size() - 1) {
                    message += ":";
                }
            }
        }

        plugin.sendCustomData(player, "sync", message);
    }
}