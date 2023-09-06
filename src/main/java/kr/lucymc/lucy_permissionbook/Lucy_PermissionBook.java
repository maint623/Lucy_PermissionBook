package kr.lucymc.lucy_permissionbook;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class Lucy_PermissionBook extends JavaPlugin {
    private static Lucy_PermissionBook INSTANCE;
    public static Lucy_PermissionBook getInstance() {
        return INSTANCE;
    }
    FileConfiguration config = this.getConfig();
    public static LuckPerms api;
    @Override
    public void onEnable() {
        INSTANCE = this;
        File ConfigFile = new File(getDataFolder(), "config.yml");
        if(!ConfigFile.isFile()) {
            config.addDefault("NamePrefix", "§f[ §b§l펄미션§f ] ");
            config.addDefault("BackNamePrefix", "§f[ §c§l페이북§f ] ");
            config.addDefault("ItemLore", "§f========[ §6정보§f ]========,§8»§f %ex%,§8»§f %Permission%,§8» §e좌클릭§f으로 §a사용,§f======================");
            config.addDefault("ExMatcher", "§8»§f (.*)");
            config.addDefault("ExIndex", 2);
            config.addDefault("UseIndex", 3);
            config.addDefault("message.addPermission", "§f[ §b§l펄미션§f ] %Permission%§f 펄미션를 획득하였습니다.");
            config.addDefault("message.havePermission", "§f[ §b§l펄미션§f ] 이미 %Permission%§f 펄미션이 있습니다. 페이북으로 지급합니다.");
            config.addDefault("message.noBookName", "§f[ §b§l펄미션§f ] 펄미션 북의 이름이 없습니다.");
            config.addDefault("message.noBookLore", "§f[ §b§l펄미션§f ] 펄미션 북의 설명이 없습니다.");
            config.addDefault("message.noPermission", "§f[ §b§l펄미션§f ] 펄미션이 없습니다.");
            config.options().copyDefaults(true);
            saveConfig();
        }
        getServer().getPluginManager().registerEvents(new PermissionBook_Event(), this);
        getCommand("펄미션북").setTabCompleter(new PermissionBook_TabCompleter());
        getCommand("펄미션북").setExecutor(this);
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            if(args[0].equalsIgnoreCase("관리자")) {
                if (p.hasPermission("lucypermissionbook.staff")) {
                    if (args[1].equalsIgnoreCase("발급")) {
                        if (args[2].isEmpty()) {
                            p.sendMessage(config.getString("message.noBookName"));
                        } else if (args[3].isEmpty()) {
                            p.sendMessage(config.getString("message.noBookLore"));
                        }else if (args[4].isEmpty()) {
                            p.sendMessage(config.getString("message.noPermission"));
                        } else {
                            ItemStack SUNFLOWER = new ItemStack(Material.BOOK, 1);
                            ItemMeta meta = SUNFLOWER.getItemMeta();
                            Objects.requireNonNull(meta).setDisplayName(config.getString("NamePrefix") + args[2].replaceAll("&", "§"));
                            meta.setLore(List.of((config.getString("ItemLore").replaceAll("%Permission%",args[4]).replaceAll("%ex%",args[3].replaceAll("&", "§"))).split(",")));
                            SUNFLOWER.setItemMeta(meta);
                            p.getInventory().addItem(SUNFLOWER);
                        }
                    }
                }
            }
        }
        return true;
    }
}
