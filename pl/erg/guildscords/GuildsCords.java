package pl.erg.guildscords;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;

import java.util.regex.Pattern;

public class GuildsCords extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("kupkordy").setExecutor(this);
        getCommand("reload-config-cords").setExecutor(this);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("kupkordy")) {
            Player player = (Player) commandSender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.getMaterial(getConfig().getString("item"))), getConfig().getInt("amount"))) {
                if (strings.length < 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("usage")));
                    return false;
                }
                if (isInteger(strings[0])) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("tag-number")));
                    return false;
                }
                if (strings[0].length() < getConfig().getInt("tag-short") || strings[0].length() > getConfig().getInt("tag-long")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("tag-short-long").replace("{TAG-SHORT}", getConfig().getString("tag-short")).replace("{TAG-LONG}", getConfig().getString("tag-long"))));
                    return false;
                }
                if (GuildUtils.getByTag(strings[0]) == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("error-tag-guild").replace("{GUILD}", strings[0])));
                    return false;
                }
                player.getInventory().removeItem((new ItemStack(Material.getMaterial(getConfig().getString("item")), getConfig().getInt("amount"))));
                for (String buycords : getConfig().getStringList("buy-cords")) {
                    buycords = buycords.replace("&", "§");
                    buycords = buycords.replace("{X}", String.valueOf(GuildUtils.getByTag(strings[0]).getRegion().getHeart().getBlockX()));
                    buycords = buycords.replace("{Z}", String.valueOf(GuildUtils.getByTag(strings[0]).getRegion().getHeart().getBlockZ()));
                    buycords = buycords.replace("{GUILD}", strings[0].toUpperCase());
                    player.sendMessage(buycords);
                }
                return false;
            }
            else {
                for (String itemlack : getConfig().getStringList("item-lack")) {
                    itemlack = itemlack.replace("&", "§");
                    itemlack = itemlack.replace("{AMOUNT}", String.valueOf(getConfig().getInt("amount")));
                    player.sendMessage(itemlack);
                }
                return false;
            }
        }
        if (command.getName().equalsIgnoreCase("reload-config-cords")) {
            reloadConfig();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("reload")));
        }
        return false;
    }
    public static boolean isInteger(final String string) {
        return Pattern.matches("-?[0-9]+", string.subSequence(0, string.length()));
    }
}