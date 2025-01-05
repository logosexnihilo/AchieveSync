package net.len.achieveSync;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class AchieveSync extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register the event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("AchievementSyncPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AchievementSyncPlugin has been disabled!");
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();

        // Notify players about the synchronization
        Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + player.getName() + " earned an advancement, syncing to everyone!");

        // Grant the advancement to all other players
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.equals(player)) {
                grantAdvancement(onlinePlayer, advancement);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // Use Iterator explicitly to iterate through advancements
        Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();
        while (advancementIterator.hasNext()) {
            Advancement advancement = advancementIterator.next();

            // Check if any online player has completed the advancement
            boolean shouldGrant = Bukkit.getOnlinePlayers().stream()
                    .filter(onlinePlayer -> !onlinePlayer.equals(joiningPlayer))
                    .anyMatch(onlinePlayer -> onlinePlayer.getAdvancementProgress(advancement).isDone());

            // Grant the advancement to the joining player if it's completed by any other player
            if (shouldGrant) {
                grantAdvancement(joiningPlayer, advancement);
            }
        }
    }

    private void grantAdvancement(Player player, Advancement advancement) {
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        progress.getRemainingCriteria().forEach(progress::awardCriteria);
    }
}
