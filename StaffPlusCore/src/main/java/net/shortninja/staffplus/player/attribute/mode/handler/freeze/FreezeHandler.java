package net.shortninja.staffplus.player.attribute.mode.handler.freeze;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.common.BusinessException;
import net.shortninja.staffplus.common.CommandPermissionValidator;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.player.attribute.gui.FreezeGui;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.unordered.IUser;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FreezeHandler implements CommandPermissionValidator {
    private static Map<UUID, Location> lastFrozenLocations = new HashMap<>();
    private static Set<UUID> loggedOut = new HashSet<UUID>();
    private PermissionHandler permission = IocContainer.getPermissionHandler();
    private MessageCoordinator message = IocContainer.getMessage();
    private Options options = IocContainer.getOptions();
    private Messages messages = IocContainer.getMessages();
    private UserManager userManager = IocContainer.getUserManager();

    public void execute(FreezeRequest freezeRequest) {
        validatePermissions(freezeRequest.getCommandSender(), freezeRequest.getPlayer());
        if (freezeRequest.isEnableFreeze()) {
            addFreeze(freezeRequest.getCommandSender(), freezeRequest.getPlayer(), true);
        } else {
            removeFreeze(freezeRequest.getCommandSender(), freezeRequest.getPlayer(), true);
        }
    }

    public boolean isFrozen(UUID uuid) {
        IUser user = userManager.get(uuid);
        if (user == null || userManager == null)
            return false;
        return lastFrozenLocations.containsKey(uuid) || user.isFrozen();
    }

    public boolean isLoggedOut(UUID uuid) {
        return loggedOut.contains(uuid);
    }

    private void addFreeze(CommandSender sender, Player player, boolean shouldMessage) {
        UUID uuid = player.getUniqueId();
        if (shouldMessage) {
            if (options.modeFreezePrompt) {
                new FreezeGui(player, options.modeFreezePromptTitle);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128));
            } else message.sendCollectedMessage(player, messages.freeze, messages.prefixGeneral);

            message.send(sender, messages.staffFroze.replace("%target%", player.getName()), messages.prefixGeneral);
        } else loggedOut.add(uuid);

        userManager.get(player.getUniqueId()).setFrozen(true);
        lastFrozenLocations.put(uuid, player.getLocation());
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128));
        options.modeFreezeSound.play(player);

    }

    public void removeFreeze(CommandSender sender, Player player, boolean shouldMessage) {
        UUID uuid = player.getUniqueId();
        IUser user = userManager.get(uuid);

        if (permission.has(player, options.permissionFreezeBypass) && shouldMessage) {
            message.send(sender, messages.bypassed, messages.prefixGeneral);
            return;
        }

        if (shouldMessage) {
            if (options.modeFreezePrompt && user.getCurrentGui().isPresent()) {
                if (user.getCurrentGui().get() instanceof FreezeGui) {
                    player.closeInventory();
                }

                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }

            message.send(sender, messages.staffUnfroze.replace("%target%", player.getName()), messages.prefixGeneral);
            message.sendCollectedMessage(player, messages.unfrozen, messages.prefixGeneral);
            ;
        } else loggedOut.remove(uuid);

        user.setFrozen(false);
        lastFrozenLocations.remove(uuid);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.SLOW);

    }

    public void checkLocations() {
        for (UUID uuid : lastFrozenLocations.keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                Location playerLocation = player.getLocation();
                Location lastLocation = lastFrozenLocations.get(uuid).setDirection(playerLocation.getDirection());

                if (compareLocations(playerLocation, lastLocation)) {
                    continue;
                }

                player.teleport(lastLocation);
            }
        }
    }

    /*
     * Only making this method because Location#equals checks if direction is the
     * same, which I really don't care for.
     */
    private boolean compareLocations(Location previous, Location current) {
        return previous.getBlockX() == current.getBlockX() && previous.getBlockY() == current.getBlockY() && previous.getBlockZ() == current.getBlockZ();
    }

    @Override
    public void validatePermissions(CommandSender commandSender, Player target) {
        if (permission.has(target, options.permissionFreezeBypass)) {
            throw new BusinessException(messages.bypassed, messages.prefixGeneral);
        }
    }
}