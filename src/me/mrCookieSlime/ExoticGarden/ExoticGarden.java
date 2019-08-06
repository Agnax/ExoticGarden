package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class ExoticGarden extends JavaPlugin {

	static List<Berry> berries = new ArrayList<Berry>();
	static List<Tree> trees = new ArrayList<Tree>();
	static Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	Category category_main, category_food, category_drinks, category_magic;
	Config cfg;

	private static boolean skullitems;

	public static ItemStack KITCHEN = new CustomItem(Material.CAULDRON, "&eCocina", new String[] {"", "&a&oPuedes crear diferentes platillos!", "&a&oEl resultado llegará al horno"});

	@Override
	public void onEnable() {
		if (!new File("plugins/ExoticGarden").exists()) new File("plugins/ExoticGarden").mkdirs();
    	if (!new File("plugins/ExoticGarden/schematics").exists()) new File("plugins/ExoticGarden/schematics").mkdirs();
		PluginUtils utils = new PluginUtils(this);
		utils.setupConfig();
		cfg = utils.getConfig();
		utils.setupMetrics();
		utils.setupUpdater(88425, getFile());

		skullitems = cfg.getBoolean("options.item-heads");

		category_main = new Category(new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&aJardín Exótico - Plantas y frutas", "", "&a> Click para abrir"));
		category_food = new Category(new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&aJardín Exótico - Comida", "", "&a> Click para abrir"));
		category_drinks = new Category(new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&aJardín Exótico - Bebidas", "", "&a> Click para abrir"));
		category_magic = new Category(new CustomItem(Material.BLAZE_POWDER, "&5Jardín exótico - Plantas mágicas", "", "&a> Click para abrir"));

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bCubo de hielo"), "ICE_CUBE", RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bCubo de hielo"), 4))
		.register();

		Kitchen.registerKitchen(this);

		registerBerry("Uvas", "&c", Color.RED, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ=="));
		registerBerry("Moras", "&9", Color.BLUE, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="));
		registerBerry("Bayas del ender", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ=="));
		registerBerry("Frambuesa", "&d", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ=="));
		registerBerry("Zarzamora", "&8", Color.GRAY, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
		registerBerry("Arándano", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0="));
		registerBerry("Airela", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19"));
		registerBerry("Fresa", "&4", Color.FUCHSIA, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));

		registerPlant("Tomate", "&4", Material.APPLE, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19"));
		registerPlant("Lechuga", "&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0="));
		registerPlant("Hoja de té", "&a", Material.OAK_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0="));
		registerPlant("Repollo", "&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ=="));
		registerPlant("Camote", "&6", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0="));
		registerPlant("Semilla de mostaza", "&e", Material.GOLD_NUGGET, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ=="));

		registerPlant("Mazorca", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0="));
		registerPlant("Piña", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19"));

		registerTree("Manzano", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "OAK_APPLE", "&c", Color.FUCHSIA, "Jugo de manzana de roble", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Coco", Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "COCONUT", "&6", Color.MAROON, "Leche de coco", false, Material.SAND);
		registerTree("Cereza", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "CHERRY", "&c", Color.FUCHSIA, "Zumo de cereza", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Granada", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "POMEGRANATE", "&4", Color.RED, "Jugo de granada", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Limon", Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "LEMON", "&e", Color.YELLOW, "Jugo de limon", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Ciruela", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "PLUM", "&5", Color.RED, "Jugo de ciruela", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lima", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "LIME", "&a", Color.LIME, "Jugo de lima", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Naranja", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "ORANGE", "&6", Color.ORANGE, "Zumo de naranja", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Durazno", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "PEACH", "&5", Color.RED, "Jugo de durazno", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pera", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "PEAR", "&a", Color.LIME, "Jugo de pera", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Fruta de dragón", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "DRAGON_FRUIT", "&d", Color.FUCHSIA, "Jugo con fruta de dragón", true, Material.DIRT, Material.GRASS_BLOCK);

		registerDishes();

		registerMagicalPlant("Carbón", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("Hierro", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("Oro", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});
		
		registerMagicalPlant("Cobre", new CustomItem(SlimefunItems.COPPER_DUST, 8),  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkyM2RiZmQ4ZjMxMTI2OTBiYjVhNjE2OGE4ZDNjYTVhYjllN2Q0M2IxZDExY2ZjYjY0M2RlN2RmZTIxIn19fQ==",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("Redstone", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("Lapis", new ItemStack(Material.LAPIS_LAZULI, 16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("Ender", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("Cuarzo", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("Diamante", new ItemStack(Material.DIAMOND), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg4Y2Q2ZGQ1MDM1OWM3ZDU4OThjN2M3ZTNlMjYwYmZjZDNkY2IxNDkzYTg5YjllODhlOWNiZWNiZmU0NTk0OSJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("Esmeralda", new ItemStack(Material.EMERALD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZjNDk1ZDFlNmViNTRhMzg2MDY4YzZjYjEyMWM1ODc1ZTAzMWI3ZjYxZDcyMzZkNWYyNGI3N2RiN2RhN2YifX19",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("Glowstone", new ItemStack(Material.GLOWSTONE_DUST, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2JlZDhkZjcxNGNlYTA2M2U0NTdiYTVlODc5MzExNDFkZTI5M2RkMWQ5YjkxNDZiMGY1YWIzODM4NjYifX19",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("Obsidiana", new ItemStack(Material.OBSIDIAN, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("Slime", new ItemStack(Material.SLIME_BALL, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNjVlNmU1MTEzYTUxODdkYWQ0NmRmYWQzZDNiZjg1ZThlZjgwN2Y4MmFhYzIyOGE1OWM0YTk1ZDZmNmEifX19",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		final ItemStack grass_seeds = new CustomItem(Material.PUMPKIN_SEEDS, "&rSemillas de césped", "", "&7&oPuede plantarse en tierra");

		final SlimefunItem crook = new SlimefunItem(Categories.TOOLS, new CustomItem(Material.WOODEN_HOE, "&rCrook", "", "&7+ &b25% &7de dropeo de retoños"), "CROOK", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null});
		crook.register(false, new BlockBreakHandler() {
			@Override
			public boolean onBlockBreak(BlockBreakEvent e, ItemStack i, int fortune, List<ItemStack> drops) {
				if (SlimefunManager.isItemSimiliar(i, crook.getItem(), true)) {
					PlayerInventory.damageItemInHand(e.getPlayer());
					if (Tag.LEAVES.isTagged(e.getBlock().getType()) && CSCoreLib.randomizer().nextInt(100) < 25) {
						ItemStack sapling = new ItemStack(e.getBlock().getType());
						drops.add(sapling);
					}
					return true;
				}
				return false;
			}
		});

		new SlimefunItem(category_main, grass_seeds, "GRASS_SEEDS", new RecipeType(new CustomItem(Material.GRASS, "&7Rompiendo césped")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(false, new ItemInteractionHandler() {
			@Override
			public boolean onRightClick(ItemUseEvent e, Player p, ItemStack i) {
				if (SlimefunManager.isItemSimiliar(i, grass_seeds, true)) {
					Block b = e.getClickedBlock();
					if (b != null && b.getType() == Material.DIRT) {
						PlayerInventory.consumeItemInHand(p);
						b.setType(Material.GRASS_BLOCK);
						if (b.getRelative(BlockFace.UP).getType() == Material.AIR || b.getRelative(BlockFace.UP).getType() == Material.CAVE_AIR)
							b.getRelative(BlockFace.UP).setType(Material.GRASS);
						b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
					}
					return true;
				}
				else return false;
			}
		});

		new PlantsListener(this);
		new FoodListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		items.put("OAK_SAPLING", new ItemStack(Material.OAK_SAPLING));
		items.put("SPRUCE_SAPLING", new ItemStack(Material.SPRUCE_SAPLING));
		items.put("BIRCH_SAPLING", new ItemStack(Material.BIRCH_SAPLING));
		items.put("JUNGLE_SAPLING", new ItemStack(Material.JUNGLE_SAPLING));
		items.put("ACACIA_SAPLING", new ItemStack(Material.ACACIA_SAPLING));
		items.put("DARK_OAK_SAPLING", new ItemStack(Material.DARK_OAK_SAPLING));
		items.put("GRASS_SEEDS", grass_seeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		cfg.save();
	}

	private void registerDishes() {
		new Juice(category_drinks, new CustomPotion("&aLicuado de lima", Color.LIME, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "LIME_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("JUGO_DE_LIMA"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4Jugo de tomate", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestaura &b&o" + "3.0" + " &7&ode hambre"), "TOMATO_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATE"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&cVino", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "WINE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("UVAS"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&eTe de limón", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "LEMON_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIMON"), getItem("ICE_CUBE"), getItem("HOJA_DE_TÉ"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&dTé helado de frambuesa", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "RASPBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("FRAMBUESA"), getItem("ICE_CUBE"), getItem("HOJA_DE_TÉ"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&dTe helado de Durazno", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "PEACH_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("DURAZNO"), getItem("ICE_CUBE"), getItem("HOJA_DE_TÉ"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4Té helado de fresa", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "STRAWBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("FRESA"), getItem("ICE_CUBE"), getItem("HOJA_DE_TÉ"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&cTé helado de cereza", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "CHERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CEREZA"), getItem("ICE_CUBE"), getItem("HOJA_DE_TÉ"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6Te tailandes", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&oRestaura &b&o" + "7.0" + " &7&ode hambre"), "THAI_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("HOJA_DE_TÉ"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("LECHE_DE_COCO"), null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0="), "&rPan de calabaza", "", "&7&oRestaura &b&o" + "4.0" + " &7&ode hambre"), "PUMPKIN_BREAD",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ=="), "&rMayonsa"), "MAYO", RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ=="), "&eMostaza"), "MOSTAZA", RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("SEMILLA_DE_MOSTAZA"), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0="), "&cSalsa BBQ"), "BBQ_SAUCE", RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATE"), getItem("MOSTAZA"), getItem("SAL"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(Material.SUGAR, "&rHarina de maíz"), "HARINA_DE_MAÍZ", RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("MAÍZ"), null, null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ=="), "&rBarra de chocolate", "", "&7&oRestaura &b&o" + "1.5" + " &7&ode hambre"), "CHOCOLATE_BAR",
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&rEnsalada de patatas", new String[] {"", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"}), "POTATO_SALAD",
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSándwich de pollo", "", "&7&oRestaura &b&o" + "5.5" + " &7&ode hambre"), "CHICKEN_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSándwich de pescado", "", "&7&oRestaura &b&o" + "5.5" + " &7&ode hambre"), "FISH_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&rEnsalada de huevo", new String[] {"", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"}), "EGG_SALAD",
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&4Sopa de tomate", new String[] {"", "&7&oRestaura &b&o" + "5.5" + " &7&ode hambre"}), "TOMATO_SOUP",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATE"), null, null, null, null, null, null, null},
		5)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&cEnsalada de fresas", new String[] {"", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"}), "STRAWBERRY_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("FRESA"), null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&cEnssalada de uvas", new String[] {"", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"}), "GRAPE_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("UVAS"), null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&rTarta de queso", "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), "CHEESECAKE",
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&cCake de queso y cerezas", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "CHERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CEREZA"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&9Tarta de queso con arándanos", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "BLUEBERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("ARÁNDANO"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6Pastel De Calabaza", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "PUMPKIN_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6Pastel de queso de pera azucarada", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "SWEETENED_PEAR_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PERA"), null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&6Bizcocho", new String[] {"", "&7&oRestaura &b&o" + "2.0" + " &7&ode hambre"}), "BISCUIT",
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null}, 
		2)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0="), "&8Postre de zarzamoras", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "BLACKBERRY_COBBLER",
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("MORAS"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&rPavlova", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "PAVLOVA",
		new ItemStack[] {getItem("LIMON"), getItem("FRESA"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.GOLDEN_CARROT, "&6Maíz en la mazorca", new String[] {"", "&7&oRestaura &b&o" + "4.5" + " &7&ode hambre"}), "CORN_ON_THE_COB",
		new ItemStack[] {SlimefunItems.BUTTER, getItem("MAÍZ"), null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&rCrema de maíz", new String[] {"", "&7&oRestaura &b&o" + "4.0" + " &7&ode hambre"}), "CREAMED_CORN",
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("MAÍZ"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		2)
		.register();
		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKED_PORKCHOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ=="), "&rTocino", "", "&7&oRestaura &b&o" + "1.5" + " &7&ode hambre"), "BACON",
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSandwich", "", "&7&oRestaura &b&o" + "9.5" + " &7&ode hambre"), "SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATE"), getItem("LECHUGA"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rBLT", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "BLT",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATE"), getItem("LECHUGA"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSandwich De Pollo Frondoso", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "LEAFY_CHICKEN_SANDWICH",
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LECHUGA"), null, null, null, null, null, null, null},
		1)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSandwich De Pescado Frondoso", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "LEAFY_FISH_SANDWICH",
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LECHUGA"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa", "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "HAMBURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa con queso", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "CHEESEBURGER",
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa con queso y tocino", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "BACON_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa de queso de lujo", "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), "DELUXE_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LECHUGA"), getItem("TOMATE"), null, null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19"), "&rPastel de zanahoria", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "CARROT_CAKE",
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa de pollo", "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "CHICKEN_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa de pollo con queso", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "CHICKEN_CHEESEBURGER",
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburguesa de tocino", "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "BACON_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSandwich con tocino", "", "&7&oRestaura &b&o" + "9.5" + " &7&ode hambre"), "BACON_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATE"), getItem("LECHUGA"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&rTaco", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "TACO",
		new ItemStack[] {getItem("HARINA_DE_MAÍZ"), new ItemStack(Material.COOKED_BEEF), getItem("LECHUGA"), getItem("TOMATE"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&rTaco de pescado", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "FISH_TACO",
		new ItemStack[] {getItem("HARINA_DE_MAÍZ"), new ItemStack(Material.COOKED_COD), getItem("LECHUGA"), getItem("TOMATE"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&cJammy Dodger", new String[] {"", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"}), "JAMMY_DODGER",
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null}, 
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rPanqueques", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "PANCAKES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rPanqueques de arándano", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "BLUEBERRY_PANCAKES",
		new ItemStack[] {getItem("PANCAKES"), getItem("ARÁNDANO"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ=="), "&rPapas fritas", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "FRIES",
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SAL"), null, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPalomitas de maiz", "", "&7&oRestaura &b&o" + "4.0" + " &7&ode hambre"), "POPCORN",
		new ItemStack[] {getItem("MAÍZ"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPalomitas de maiz &7(Dulces)", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "SWEET_POPCORN",
		new ItemStack[] {getItem("MAÍZ"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPalomitas de maiz &7(Saladas)", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "SALTY_POPCORN",
		new ItemStack[] {getItem("MAÍZ"), getItem("BUTTER"), getItem("SAL"), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rPastel de carne", "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), "SHEPARDS_PIE",
		new ItemStack[] {getItem("REPOLLO"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATE"), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rPastel de pollo", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "CHICKEN_POT_PIE",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rPastel de cocholate", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "CHOCOLATE_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&rGalleta De Crema", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "CREAM_COOKIE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rMuffin de arándanos", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "BLUEBERRY_MUFFIN",
		new ItemStack[] {getItem("ARÁNDANO"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rMuffin De Calabaza", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "PUMPKIN_MUFFIN",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rMuffin De Chocolate", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "CHOCOLATE_CHIP_MUFFIN",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&rTarta de Crema de Boston", "", "&7&oRestaura &b&o" + "4.5" + " &7&ode hambre"), "BOSTON_CREAM_PIE",
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rHot Dog", "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "HOT_DOG",
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rHot Dog envuelto en queso y tocino", "&7&o\"Cuando yo cocino\" - @Eyamaz", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "BACON_WRAPPED_CHEESE_FILLED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rHot Dog envuelto en tocino y BBQ", "&7&o\"¿Quieres hablar de Hot Dogs?\" - @Pahimar", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "BBQ_BACON_WRAPPED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rHot Dog de doble tocino con BBQ envuelto en una tortilla con queso ", "&7&o\"Cuando yo cocino\" - @Eyamaz", "", "&7&oRestaura &b&o" + "10.0" + " &7&ode hambre"), "BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE",
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("HARINA_DE_MAÍZ"), getItem("CHEESE"), getItem("HARINA_DE_MAÍZ")},
		20)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0="), "&aTé endulzado", "", "&7&oRestaura &b&o" + "3.0" + " &7&ode hambre"), "SWEETENED_TEA",
		new ItemStack[] {getItem("HOJA_DE_TÉ"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19"), "&6Chocolate caliente", "", "&7&oRestaura &b&o" + "4.0" + " &7&ode hambre"), "HOT_CHOCOLATE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&6Piña colada", "", "&7&oRestaura &b&o" + "7.0" + " &7&ode hambre"), "PINACOLADA",
		new ItemStack[] {getItem("PIÑA"), getItem("ICE_CUBE"), getItem("LECHE_DE_COCO"), null, null, null, null, null, null},
		14)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ=="), "&cFresca con chocolate", "", "&7&oRestaura &b&o" + "2.5" + " &7&ode hambre"), "CHOCOLATE_STRAWBERRY",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("FRESA"), null, null, null, null, null, null, null},
		5)
		.register();

		new Juice(category_drinks, new CustomPotion("&eLimonada", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&oRestaura &b&o" + "4.0" + " &7&ode hambre"), "LEMONADE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rPay de camote", "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), "SWEET_POTATO_PIE",
		new ItemStack[] {getItem("CAMOTE"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rPastelillo Lamington", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "LAMINGTON",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCO"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rWaffles", "", "&7&oRestaura &b&o" + "6.0" + " &7&ode hambre"), "WAFFLES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rClub Sandwich", "", "&7&oRestaura &b&o" + "9.5" + " &7&ode hambre"), "CLUB_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATE"), getItem("LECHUGA"), getItem("MOSTAZA"), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&rBurrito", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "BURRITO",
		new ItemStack[] {getItem("HARINA_DE_MAÍZ"), new ItemStack(Material.COOKED_BEEF), getItem("LECHUGA"), getItem("TOMATE"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&rBurrito de pollo", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "CHICKEN_BURRITO",
		new ItemStack[] {getItem("HARINA_DE_MAÍZ"), new ItemStack(Material.COOKED_CHICKEN), getItem("LECHUGA"), getItem("TOMATE"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ=="), "&rSandwich a la parrilla", "", "&7&oRestaura &b&o" + "5.5" + " &7&ode hambre"), "GRILLED_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0="), "&rLasagna", "", "&7&oRestaura &b&o" + "8.5" + " &7&ode hambre"), "LASAGNA",
		new ItemStack[] {getItem("TOMATE"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATE"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19"), "&rHelado", "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), "ICE_CREAM",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("FRESA"), null, null, null, null},
		16)
		.register();

		new Juice(category_drinks, new CustomPotion("&6Jugo de piñacolada", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestaura &b&o" + "3.0" + " &7&ode hambre"), "PINEAPPLE_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("PIÑA"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6Licuado de piñacolada", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), "PINEAPPLE_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisú", "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), "TIRAMISU",
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisú con fresas", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "TIRAMISU_WITH_STRAWBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("FRESA"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisú con frambuesas", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "TIRAMISU_WITH_RASPBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("FRAMBUESA"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisú con zarzamoras", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "TIRAMISU_WITH_BLACKBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("MORAS"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rPastel de chocolate y pera", "", "&7&oRestaura &b&o" + "9.5" + " &7&ode hambre"), "CHOCOLATE_PEAR_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PERA"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&cPastel de manzana y pera", "", "&7&oRestaura &b&o" + "9.0" + " &7&ode hambre"), "APPLE_PEAR_CAKE",
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PERA"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		berries = null;
		trees = null;
		items = null;
	}

	public void registerTree(String name, Material material, String texture, String fruitName, String color, Color pcolor, String juice, boolean pie, Material... soil) {
		String id = fruitName;
		Tree tree = new Tree(id, fruitName, texture, soil);
		trees.add(tree);

		items.put(id + "_SAPLING", new CustomItem(Material.OAK_SAPLING, color + "Retoño de " + name));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + "Retoño de " + name), id + "_SAPLING", new RecipeType(new CustomItem(Material.GRASS, "&7Rompiendo césped")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		try {
			new EGPlant(category_main, new CustomItem(getSkull(material, texture), color + name), fruitName, new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtenible del árbol específico")), true,
			new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
			.register();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (pcolor != null) {
			new Juice(category_drinks, new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestaura &b&o" + "3.0" + " &7&ode hambre"), juice.toUpperCase().replace(" ", "_"), RecipeType.JUICER,
			new ItemStack[] {getItem(fruitName), null, null, null, null, null, null, null, null})
			.register();
		}

		if (pie) {
			try {
				new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + "Pay de " + name, "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), fruitName + "_PIE",
				new ItemStack[] {getItem(fruitName), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
				13)
				.register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!new File("plugins/ExoticGarden/schematics", id + "_TREE.schematic").exists())
			saveResource("schematics/" + id + "_TREE.schematic", false);
	}

	public void registerBerry(String name, String color, Color pcolor, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase(), type, data);
		berries.add(berry);

		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + "Arbusto de "+ name));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + "Arbusto de "+ name), name.toUpperCase() + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7Rompiendo Césped")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(Material.NETHER_WART, data.getTexture()), color + name), name.toUpperCase(), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtenido en un arbusto especifico")), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase() + "_BUSH"), null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + "Jugo de " + name, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestaura &b&o" + "3.0" + " &7&ode hambre"), name.toUpperCase() + "_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem(name.toUpperCase()), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + "Licuado de " + name, pcolor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestaura &b&o" + "5.0" + " &7&ode hambre"), name.toUpperCase() + "_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(name.toUpperCase() + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		try {
			new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19"), color + "Sandwich con jalea de " + name, "", "&7&oRestaura &b&o" + "8.0" + " &7&ode hambre"), name.toUpperCase() + "_JELLY_SANDWICH",
			new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(name.toUpperCase() + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
			16)
			.register();

			new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + "Pay de " + name, "", "&7&oRestaura &b&o" + "6.5" + " &7&ode hambre"), name.toUpperCase() + "_PIE",
			new ItemStack[] {getItem(name.toUpperCase()), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem(): null;
	}

	public static ItemStack getSkull(Material material, String texture) {
		try {
			return skullitems && !texture.equals("NO_SKULL_SPECIFIED") ? CustomSkull.getItem(texture) : new ItemStack(material);
		} catch (Exception e) {
			e.printStackTrace();
			return new ItemStack(material);
		}
	}

	public void registerPlant(String name, String color, Material material, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase().replace(" ", "_"), type, data);
		berries.add(berry);

		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + "&rPlanta de " + name));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + "&rPlanta de " + name), name.toUpperCase().replace(" ", "_") + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7Rompiendo césped")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(material, data.getTexture()), color + name), name.toUpperCase().replace(" ", "_"), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtenible del arbusto en específico")), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase().replace(" ", "_") + "_BUSH"), null, null, null, null})
		.register();
	}

	public void registerMagicalPlant(String name, ItemStack item, String skull, ItemStack[] recipe) {
		ItemStack essence = new CustomItem(Material.BLAZE_POWDER, "&rEsencia mágica", "", "&7" + name);

		Berry berry = new Berry(essence, name.toUpperCase() + "_ESSENCE", PlantType.ORE_PLANT, new PlantData(skull));
		berries.add(berry);

		new SlimefunItem(category_magic, new CustomItem(Material.OAK_SAPLING, "&rPlanta de " + name), name.toUpperCase().replace(" ", "_") + "_PLANT", RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();

		HandledBlock plant = new HandledBlock(category_magic, essence, name.toUpperCase().replace(" ", "_") + "_ESSENCE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});

		plant.setRecipeOutput(item.clone());
		plant.register();
	}

	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item != null && item instanceof HandledBlock) {
			for (Berry berry : ExoticGarden.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		return null;
	}

	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		if (item != null) {
			for (Berry berry : berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							Block plant;
							if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
								plant = block;
								BlockStorage.retrieve(block.getRelative(BlockFace.UP));
								block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.getRelative(BlockFace.UP).setType(Material.AIR);;
							}
							else {
								plant = block.getRelative(BlockFace.DOWN);
								BlockStorage.retrieve(block);
								block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.setType(Material.AIR);;
							}
							plant.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage.store(plant, getItem(berry.toBush()));
							break;
						}
						default: {
							block.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage.store(block, getItem(berry.toBush()));
							break;
						}
					}
				}
			}
		}
		return itemstack;
	}

}
