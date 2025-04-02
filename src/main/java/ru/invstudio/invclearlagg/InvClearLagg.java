package ru.invstudio.invclearlagg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class InvClearLagg extends JavaPlugin {

    private int dropTime;
    private String message;
    private String warningMessage;
    private Sound warningSound;
    private boolean playSound;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        dropTime = getConfig().getInt("drop-time");
        message = getConfig().getString("message");
        warningMessage = getConfig().getString("warning-message");

        String soundName = getConfig().getString("warning-sound");
        warningSound = Sound.valueOf(soundName);

        playSound = getConfig().getBoolean("play-sound");

        getLogger().info(color("&b[InvClearLagg] Успешно запущен!"));
        getLogger().info(color("&b[InvClearLagg] t.me/invstudio"));

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(color(warningMessage));
                if(playSound) {
                    playWarningSound();
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cleanDrops();
                    }
                }.runTaskLater(InvClearLagg.this, 20 * 30);
            }
        }.runTaskTimer(this, 0, 20 * dropTime);
    }

    private void playWarningSound() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), warningSound, 1.0f, 1.0f);
        }
    }

    private void cleanDrops() {
        List<Item> items = Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntitiesByClass(Item.class).stream())
                .collect(Collectors.toList());

        for (Item item : items) {
            item.remove();
        }

        Bukkit.broadcastMessage(color(message));
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("iclear")) {
            if (sender.hasPermission("invclearlagg.admin")) {
                cleanDrops();
                sender.sendMessage(color("&bПредметы удалены!"));
            } else {
                sender.sendMessage(color("&cУ вас нет прав для выполнения этой команды."));
            }
            return true;
        }
        return false;
    }
}