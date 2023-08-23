package net.jadedmc.jadedpartybungee.listeners;

import net.jadedmc.jadedpartybungee.JadedPartyBungee;
import net.jadedmc.jadedpartybungee.party.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchListener implements Listener {
    private final JadedPartyBungee plugin;

    public ServerSwitchListener(JadedPartyBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        Party party = plugin.partyManager().getParty(player);
        if(party == null) {
            return;
        }

        party.syncData(player);
    }
}