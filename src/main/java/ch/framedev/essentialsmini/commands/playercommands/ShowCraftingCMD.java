package ch.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 16.08.2020 18:22
 */

import ch.framedev.essentialsmini.managers.MaterialManager;
import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowCraftingCMD extends CommandBase {

    private final Main plugin;

    public ShowCraftingCMD(Main plugin) {
        super(plugin, "showcrafting");
        this.plugin = plugin;
        setupTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(plugin.getPermissionBase() + "showcrafting")) {
                ItemStack item = new ItemStack(new MaterialManager().getMaterial(args[0].toLowerCase()));
                for (Recipe recipe : Bukkit.getRecipesFor(item)) {
                    if (recipe.getResult().equals(item)) {
                        if (recipe instanceof FurnaceRecipe) {
                            player.sendMessage("Recipe für " + item.getType().name() + " Furnace");
                            FurnaceRecipe recipes = (FurnaceRecipe) recipe;
                            player.sendMessage(recipes.getInput().getType().name() + "");
                        }
                        if (recipe instanceof ShapedRecipe) {
                            ShapedRecipe recipeShape = (ShapedRecipe) recipe;
                            player.sendMessage("Recipe für " + item.getType().name() + " Crafting Table Shaped");
                            player.sendMessage(recipeShape.getShape());
                            recipeShape.getIngredientMap().forEach((character, itemStack) -> {
                                if (itemStack != null) {
                                    player.sendMessage(character + " = " + itemStack.getType().name());
                                }
                            });
                        }
                        if (recipe instanceof ShapelessRecipe) {
                            player.sendMessage("Recipe für " + item.getType().name() + " Crafting Table ShapeLess");
                            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                            shapelessRecipe.getIngredientList().forEach(items -> {
                                player.sendMessage(items.getType().name());
                            });
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("essentialsmini.showcrafting")) {
                ArrayList<String> empty = new ArrayList<>();
                ArrayList<Material> materials = new MaterialManager().getMaterials();
                for (Material material : materials) {
                    if (material.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        empty.add(material.name());
                    }
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }
}
