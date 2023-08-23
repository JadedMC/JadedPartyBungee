package net.jadedmc.jadedpartybungee.party;

import net.jadedmc.jadedpartybungee.JadedPartyBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all existing party.
 */
public class PartyManager {
    private final JadedPartyBungee plugin;
    private final List<Party> parties = new ArrayList<>();

    /**
     * Creates the party manager.
     * @param plugin Instance of the plugin.
     */
    public PartyManager(JadedPartyBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * Create a new party.
     * @param leader Leader of the party,
     * @return Party that was created.
     */
    public Party createParty(ProxiedPlayer leader) {
        Party party = new Party(plugin, leader);
        parties.add(party);
        party.syncData();
        return party;
    }

    /**
     * Disbands an active party.
     * @param party Party to disband.
     */
    public void disbandParty(Party party) {
        getParties().remove(party);

        // Make sure spigot servers know the party was disbanded.
        for(ProxiedPlayer player : party.getMembers()) {
            plugin.sendCustomData(player, "disband", party.getUUID().toString());
        }
    }

    /**
     * Get the party a player is in.
     * Returns null if not in a party.
     * @param player Player to get party of.
     * @return Party the player is in.
     */
    public Party getParty(ProxiedPlayer player) {
        for(Party party : getParties()) {
            if(party.getMembers().contains(player)) {
                return party;
            }
        }

        return null;
    }

    /**
     * Get a party based off it's uuid.
     * @param uuid UUID of the party.
     * @return Party object.
     */
    public Party getParty(String uuid) {
        for(Party party : getParties()) {
            if(party.getUUID().toString().equalsIgnoreCase(uuid)) {
                return party;
            }
        }

        return null;
    }

    /**
     * Get all current parties.
     * @return All current parties.
     */
    public List<Party> getParties() {
        return parties;
    }
}