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
package net.jadedmc.jadedparty.party;

import net.jadedmc.jadedparty.JadedPartyPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all existing party.
 */
public class PartyManager {
    private final JadedPartyPlugin plugin;
    private final List<Party> parties = new ArrayList<>();

    /**
     * Creates the party manager.
     * @param plugin Instance of the plugin.
     */
    public PartyManager(JadedPartyPlugin plugin) {
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