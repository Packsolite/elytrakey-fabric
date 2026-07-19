package eu.packsolite.elytrakey;

import com.mojang.blaze3d.platform.InputConstants;
import eu.packsolite.elytrakey.options.ConfigLoader;
import eu.packsolite.elytrakey.ui.ElytraKeyOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

import java.util.List;

import static net.minecraft.world.item.Items.*;

public class ElytraKey implements ModInitializer {

	private static final int OFF_HAND_SLOT_ID = 40;
	private static final int CHEST_PLATE_SLOT_ID = EquipmentSlot.CHEST.getIndex(36);
	private static final List<Item> CHESTPLATE_PRIORITY = List.of(NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, IRON_CHESTPLATE, CHAINMAIL_CHESTPLATE, GOLDEN_CHESTPLATE, COPPER_CHESTPLATE, LEATHER_HELMET);

	public static boolean AUTO_EQUIP_FALL = true;
	public static boolean AUTO_EQUIP_FIREWORKS = false;
	public static boolean AUTO_UNEQUIP = true;
	public static boolean EASY_TAKEOFF = true;
	public static double AUTO_EQUIP_FALL_VELOCITY;

	private Minecraft mc = Minecraft.getInstance();

	private static KeyMapping swapElytraKeyBinding;
	private static KeyMapping elytraOptionsKeyBinding;

	private boolean wasAutoEquipped = false;
	private boolean startFlying = false;
	private boolean boostNextTick = false;

	@Override
	public void onInitialize() {
		new ConfigLoader().loadConfig();
		KeyMapping.Category cat = KeyMapping.Category.register(Identifier.parse("elytrakey"));
		swapElytraKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("Swap Elytra", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, cat));
		elytraOptionsKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("ElytraKey Options", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, cat));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (swapElytraKeyBinding.consumeClick()) {
				swapElytra();
			}

			while (elytraOptionsKeyBinding.consumeClick()) {
				mc.setScreenAndShow(new ElytraKeyOptions());
			}

			if (mc.player == null) {
				return;
			}

			boolean fireworksInMainHand = mc.player.getInventory().getSelectedItem().getItem() == Items.FIREWORK_ROCKET;
			boolean fireworksInOffHand = mc.player.getInventory().getItem(OFF_HAND_SLOT_ID).getItem() == Items.FIREWORK_ROCKET;
			boolean isFalling = !mc.player.onGround() && mc.player.getDeltaMovement().y() < AUTO_EQUIP_FALL_VELOCITY;
			boolean hasLanded = mc.player.onGround() || mc.player.isInWater();

			if ((AUTO_EQUIP_FIREWORKS && fireworksInMainHand) || (AUTO_EQUIP_FALL && isFalling)) {
				boolean elytraEquipped = isElytraEquipped();
				if (!elytraEquipped) {
					equipElytra();
					wasAutoEquipped = true;
				}
			} else {
				boolean unEquip = AUTO_UNEQUIP && wasAutoEquipped && hasLanded;
				if (unEquip && isElytraEquipped()) {
					wasAutoEquipped = false;
					equipChestplate();
				}
			}

			// Equip elytra, start gliding and boost with fireworks when right-clicking with a firework
			if (EASY_TAKEOFF && (fireworksInMainHand || fireworksInOffHand)) {
				updateEasyTakeoff(fireworksInMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			}
		});
	}

	private void updateEasyTakeoff(InteractionHand fireworkHand) {
		if (mc.player.isFallFlying()) {
			if (boostNextTick) {
				boostNextTick = false;
				mc.options.keyJump.setDown(false);
				mc.gameMode.useItem(mc.player, fireworkHand);
				mc.player.swing(InteractionHand.MAIN_HAND);
			}
		} else { // Not flying
			if (startFlying) {
				// Press space to switch to flying state
				mc.options.keyJump.setDown(true);
				boostNextTick = true;
				startFlying = false;

			} else if (mc.options.keyUse.isDown()) {

				// Clicked with fireworks in air?
				if (mc.hitResult instanceof BlockHitResult && mc.hitResult.getType() == Type.MISS) {

					// Elytra already equipped?
					if (!isElytraEquipped()) {
						if (!equipElytra()) {
							return;
						}
						wasAutoEquipped = true;
					}

					// Jump if on ground
					if (mc.player.onGround()) {
						mc.player.jumpFromGround();
					}

					// Start takeoff
					startFlying = true;
					mc.options.keyJump.setDown(false);
				}
			}
		}
	}

	public boolean isElytraEquipped() {
		ItemStack chestPlate = mc.player.getInventory().getItem(CHEST_PLATE_SLOT_ID);
		return chestPlate.getItem() == Items.ELYTRA;
	}

	public boolean equipElytra() {
		ItemStack chest = mc.player.getInventory().getItem(CHEST_PLATE_SLOT_ID);

		if (chest.getItem() != Items.ELYTRA) {
			int elytraSlot = searchItem(Items.ELYTRA);

			if (elytraSlot == -1) {
				return false;
			}

			if (elytraSlot < 9) {
				mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 6, elytraSlot, ContainerInput.SWAP, mc.player);
			} else {
				mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, elytraSlot, 0, ContainerInput.PICKUP, mc.player);
				mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 6, 0, ContainerInput.PICKUP, mc.player);
				mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, elytraSlot, 0, ContainerInput.PICKUP, mc.player);
			}
		}
		return true;
	}

	public boolean equipChestplate() {
		int chestSlot = findChestplate();

		if (chestSlot == -1) {
			return false;
		}

		if (chestSlot < 9) {
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 6, chestSlot, ContainerInput.SWAP, mc.player);
		} else {
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, chestSlot, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 6, 0, ContainerInput.PICKUP, mc.player);
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, chestSlot, 0, ContainerInput.PICKUP, mc.player);
		}
		return true;
	}

	private void swapElytra() {
		if (isElytraEquipped()) {
			boolean equipped = equipChestplate();

			// No chestplate found?
			if (!equipped) {
				int emptySlot = mc.player.getInventory().getFreeSlot();

				if (emptySlot < 0) {
					print("elytrakey.chat.full_inventory");
				} else {
					mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 6, emptySlot, ContainerInput.SWAP, mc.player);
				}
			}
		} else {
			boolean equipped = equipElytra();

			if (!equipped) {
				print("elytrakey.chat.no_elytra");
			}
		}
	}

	private int findChestplate() {
		// Netherite chestplate
		for (var chestplate : CHESTPLATE_PRIORITY) {
			int slot = searchItem(chestplate);
			if (slot != -1) {
				return slot;
			}
		}
		return -1;
	}

	private int searchItem(Item item) {
		NonNullList<ItemStack> container = mc.player.getInventory().getNonEquipmentItems();
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	public void print(String key) {
		if (mc.player != null) {
			mc.player.sendOverlayMessage(Component.translatable(key));
		}
	}
}
