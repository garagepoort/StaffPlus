package net.shortninja.staffplus.core.domain.staff.mode.item;

import net.shortninja.staffplus.core.common.gui.confirmation.ConfirmationConfig;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CustomModuleConfiguration extends ModeItemConfiguration {
    private ModuleType moduleType;
    private String action;

    private String enchantment;
    private int level;

    private ConfirmationConfig confirmationConfig;

    private boolean requireInput;
    private String inputPrompt;

    public CustomModuleConfiguration(boolean enabled, String identifier, ModuleType moduleType, ItemStack item, String action, ConfirmationConfig confirmationConfig, boolean requireInput, String inputPrompt) {
        super(identifier);
        this.confirmationConfig = confirmationConfig;
        this.requireInput = requireInput;
        this.inputPrompt = inputPrompt;
        this.moduleType = moduleType;
        this.action = action;
        this.enchantment = "";
        this.level = 0;
        setEnabled(enabled);
        setItem(item);
    }

    public ModuleType getType() {
        return moduleType;
    }

    public String getAction() {
        return action;
    }

    public String getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    public enum ModuleType {
        COMMAND_STATIC, COMMAND_DYNAMIC, COMMAND_CONSOLE, ITEM;
    }

    public Optional<ConfirmationConfig> getConfirmationConfig() {
        return Optional.ofNullable(confirmationConfig);
    }

    public boolean isRequireInput() {
        return requireInput;
    }

    public String getInputPrompt() {
        return inputPrompt;
    }
}