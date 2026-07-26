package eu.packsolite.elytrakey.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class InventoryHelper {

	public static final int OFF_HAND_SLOT = 40;
	private static final int CHEST_ARMOR_SLOT = EquipmentSlot.CHEST.getIndex(36);
	private static final int CHEST_CONTAINER_SLOT = 6;

	public static boolean isElytraEquipped() {
		var mc = Minecraft.getInstance();
		return mc.player.getInventory().getItem(CHEST_ARMOR_SLOT).getItem() == Items.ELYTRA;
	}

	public static boolean equipElytra() {
		if (isElytraEquipped()) return true;
		int slot = searchItem(Items.ELYTRA);
		if (slot == -1) return false;
		swapToChest(slot);
		return true;
	}

	public static boolean equipChestplate() {
		int slot = findBestChestplate();
		if (slot == -1) return false;
		swapToChest(slot);
		return true;
	}

	public static void swapElytra() {
		if (isElytraEquipped()) {
			if (!equipChestplate()) {
				var mc = Minecraft.getInstance();
				int emptySlot = mc.player.getInventory().getFreeSlot();
				if (emptySlot < 0) {
					print("elytrakey.chat.full_inventory");
				} else {
					swapToChest(emptySlot);
				}
			}
		} else {
			if (!equipElytra()) {
				print("elytrakey.chat.no_elytra");
			}
		}
	}

	private static void swapToChest(int slotIndex) {
		var mc = Minecraft.getInstance();
		int containerId = mc.player.inventoryMenu.containerId;
		if (slotIndex < 9) {
			mc.gameMode.handleContainerInput(containerId, CHEST_CONTAINER_SLOT, slotIndex, ContainerInput.SWAP, mc.player);
		} else {
			mc.gameMode.handleContainerInput(containerId, slotIndex, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(containerId, CHEST_CONTAINER_SLOT, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(containerId, slotIndex, 0, ContainerInput.PICKUP, mc.player);
		}
	}

	private static int findBestChestplate() {
		var mc = Minecraft.getInstance();
		var container = mc.player.getInventory().getNonEquipmentItems();
		int bestSlot = -1;
		int bestScore = -1;
		for (int i = 0; i < container.size(); i++) {
			int score = scoreChestplate(container.get(i));
			if (score > bestScore) {
				bestScore = score;
				bestSlot = i;
			}
		}
		return bestSlot;
	}

	private static int scoreChestplate(ItemStack stack) {
		ItemAttributeModifiers attrs = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (attrs == null) return -1;

		double armor = 0;
		double toughness = 0;
		boolean isChestplate = false;
		for (var entry : attrs.modifiers()) {
			if (entry.slot() != EquipmentSlotGroup.CHEST) continue;
			isChestplate = true;
			if (entry.attribute() == Attributes.ARMOR) {
				armor = entry.modifier().amount();
			} else if (entry.attribute() == Attributes.ARMOR_TOUGHNESS) {
				toughness = entry.modifier().amount();
			}
		}
		if (!isChestplate) return -1;

		int enchantLevels = 0;
		ItemEnchantments enchants = stack.get(DataComponents.ENCHANTMENTS);
		if (enchants != null) {
			for (var entry : enchants.entrySet()) {
				enchantLevels += entry.getIntValue();
			}
		}

		return (int) (armor * 1000 + toughness * 100 + enchantLevels);
	}

	private static int searchItem(Item item) {
		var mc = Minecraft.getInstance();
		NonNullList<ItemStack> container = mc.player.getInventory().getNonEquipmentItems();
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i).getItem() == item) return i;
		}
		return -1;
	}

	public static void print(String translationKey) {
		var mc = Minecraft.getInstance();
		if (mc.player != null) {
			mc.player.sendOverlayMessage(Component.translatable(translationKey));
		}
	}
}
