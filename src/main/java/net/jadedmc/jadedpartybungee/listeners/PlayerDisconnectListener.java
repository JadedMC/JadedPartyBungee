package net.jadedmc.jadedpartybungee.listeners;

import net.jadedmc.jadedpartybungee.JadedPartyBungee;
import net.jadedmc.jadedpartybungee.party.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This listens to the PlayerDisconnectEvent event, which is called every time a player leaves the server.
 * We use this to announce when a staff member leaves to other staff members.
 */
public class PlayerDisconnectListener implements Listener {
    private final JadedPartyBungee plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * This is known as Dependency Injection.
     * @param plugin Instance of the plugin.
     */
    public PlayerDisconnectListener(JadedPartyBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerDisconnectEvent.
     */
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Party party = plugin.partyManager().getParty(player);
        if(party != null) {
            party.removePlayer(player);
            party.sendMessage("<green><bold>Party</bold> <dark_gray>» <white>" + player.getName() + " &adisconnected.");
        }
    }
}