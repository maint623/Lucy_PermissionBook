package kr.lucymc.lucy_permissionbook;

import net.luckperms.api.messaging.MessagingService;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kr.lucymc.lucy_permissionbook.Lucy_PermissionBook.api;
import static org.bukkit.Bukkit.getLogger;

public class PermissionBook_Event implements Listener {
    FileConfiguration configs = Lucy_PermissionBook.getInstance().getConfig();
    @EventHandler
    public void PrefixBookClick(PlayerInteractEvent event) {
        if(!(event.getHand() == EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        if(player.getItemInHand().hasItemMeta()) {
            if (Objects.requireNonNull(player.getItemInHand().getItemMeta()).hasDisplayName()) {
                if (player.getItemInHand().getItemMeta().getDisplayName().startsWith(configs.getString("NamePrefix"))) {
                        String inputString = player.getItemInHand().getItemMeta().getLore().get(configs.getInt("ExIndex"));
                        String pattern = configs.getString("ExMatcher");
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(inputString);
                        String lore = null;
                        if (m.find()) {
                            lore = m.group(1).replaceAll("§0","").replaceAll("§1","").replaceAll("§2","").replaceAll("§3","").replaceAll("§4","").replaceAll("§5","").replaceAll("§6","").replaceAll("§7","").replaceAll("§8","").replaceAll("§9","").replaceAll("§a","").replaceAll("§b","") .replaceAll("§c","").replaceAll("§d","").replaceAll("§e","").replaceAll("§f","").replaceAll("§k","").replaceAll("§l","").replaceAll("§m","").replaceAll("§n","").replaceAll("§o","").replaceAll("§r","");;
                        } else {
                            getLogger().log(Level.SEVERE, "[ 펄미션 북 ] 패턴과 일치하는 부분을 찾을 수 없습니다. (config.yml에 ExMatcher를 수정 해주세요.)");
                        }
                    if (!player.hasPermission(lore)) {
                        User user = api.getUserManager().getUser(player.getUniqueId());
                        user.data().add(Node.builder(lore).build());
                        CompletableFuture<Void> future = api.getUserManager().saveUser(user);
                        future.thenRunAsync(() -> {
                            Optional<MessagingService> messagingService = api.getMessagingService();
                            if (messagingService.isPresent()) {
                                messagingService.get().pushUserUpdate(user);
                            }
                        });
                        if (player.getInventory().getItemInHand().getAmount() == 1) {
                            player.getInventory().setItemInHand(new ItemStack(Material.AIR));
                        } else {
                            player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount()-1);
                        }
                        player.sendMessage(configs.getString("message.addPermission").replace("%Permission%",lore));
                    } else {
                        player.sendMessage(configs.getString("message.havePermission").replace("%Permission%",lore));
                        ItemStack SUNFLOWER = new ItemStack(player.getItemInHand());
                        SUNFLOWER.setAmount(1);
                        ItemMeta meta = SUNFLOWER.getItemMeta();
                        List<String> lores = meta.getLore();
                        lores.remove(configs.getInt("UseIndex"));
                        meta.setLore(lores);
                        Objects.requireNonNull(meta).setDisplayName(configs.getString("BackNamePrefix") + (player.getItemInHand().getItemMeta().getDisplayName()).replace(configs.getString("NamePrefix"),""));
                        SUNFLOWER.setItemMeta(meta);
                        player.getInventory().addItem(SUNFLOWER);
                        if (player.getInventory().getItemInHand().getAmount() == 1) {
                            player.getInventory().setItemInHand(new ItemStack(Material.AIR));
                        } else {
                            player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount()-1);
                        }
                    }
                }
            }
        }
    }
}
