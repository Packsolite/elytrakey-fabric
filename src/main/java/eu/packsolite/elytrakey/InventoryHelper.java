package eu.packsolite.elytrakey;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static net.minecraft.world.item.Items.*;

public class InventoryHelper {

	private static final int CHEST_ARMOR_SLOT = EquipmentSlot.CHEST.getIndex(36);
	private static final int CHEST_CONTAINER_SLOT = 6;
	private static final List<Item> CHESTPLATE_PRIORITY = List.of(
		NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, IRON_CHESTPLATE,
		CHAINMAIL_CHESTPLATE, GOLDEN_CHESTPLATE, COPPER_CHESTPLATE, LEATHER_CHESTPLATE
	);

	public static boolean isElytraEquipped(Minecraft mc) {
		return mc.player.getInventory().getItem(CHEST_ARMOR_SLOT).getItem() == Items.ELYTRA;
	}

	public static boolean equipElytra(Minecraft mc) {
		if (isElytraEquipped(mc)) return true;
		int slot = searchItem(mc, Items.ELYTRA);
		if (slot == -1) return false;
		swapToChest(mc, slot);
		return true;
	}

	public static boolean equipChestplate(Minecraft mc) {
		int slot = findBestChestplate(mc);
		if (slot == -1) return false;
		swapToChest(mc, slot);
		return true;
	}

	public static void swapElytra(Minecraft mc) {
		if (isElytraEquipped(mc)) {
			if (!equipChestplate(mc)) {
				int emptySlot = mc.player.getInventory().getFreeSlot();
				if (emptySlot < 0) {
					print(mc, "elytrakey.chat.full_inventory");
				} else {
					swapToChest(mc, emptySlot);
				}
			}
		} else {
			if (!equipElytra(mc)) {
				print(mc, "elytrakey.chat.no_elytra");
			}
		}
	}

	private static void swapToChest(Minecraft mc, int slotIndex) {
		int containerId = mc.player.inventoryMenu.containerId;
		if (slotIndex < 9) {
			mc.gameMode.handleContainerInput(containerId, CHEST_CONTAINER_SLOT, slotIndex, ContainerInput.SWAP, mc.player);
		} else {
			mc.gameMode.handleContainerInput(containerId, slotIndex, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(containerId, CHEST_CONTAINER_SLOT, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(containerId, slotIndex, 0, ContainerInput.PICKUP, mc.player);
		}
	}

	private static int findBestChestplate(Minecraft mc) {
		for (var chestplate : CHESTPLATE_PRIORITY) {
			int slot = searchItem(mc, chestplate);
			if (slot != -1) return slot;
		}
		return -1;
	}

	private static int searchItem(Minecraft mc, Item item) {
		NonNullList<ItemStack> container = mc.player.getInventory().getNonEquipmentItems();
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i).getItem() == item) return i;
		}
		return -1;
	}

	public static void print(Minecraft mc, String translationKey) {
		if (mc.player != null) {
			mc.player.sendOverlayMessage(Component.translatable(translationKey));
		}
	}
}
