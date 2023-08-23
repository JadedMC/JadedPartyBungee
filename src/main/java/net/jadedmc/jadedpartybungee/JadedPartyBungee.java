package net.jadedmc.jadedpartybungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.jadedmc.jadedpartybungee.commands.PartyCMD;
import net.jadedmc.jadedpartybungee.listeners.PlayerDisconnectListener;
import net.jadedmc.jadedpartybungee.listeners.ServerSwitchListener;
import net.jadedmc.jadedpartybungee.party.PartyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;

public final class JadedPartyBungee extends Plugin {
    private PartyManager partyManager;

    @Override
    public void onEnable() {
        partyManager = new PartyManager(this);

        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));
        getProxy().getPluginManager().registerListener(this, new ServerSwitchListener(this));

        getProxy().getPluginManager().registerCommand(this, new PartyCMD(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PartyManager partyManager() {
        return partyManager;
    }

    public void sendCustomData(ProxiedPlayer player, String subChannel, String message) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();

        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(message);

        player.getServer().getInfo().sendData( "jadedmc:party", out.toByteArray() );
    }
}