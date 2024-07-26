package de.liquiddev.elytrakey;

import org.lwjgl.glfw.GLFW;

import de.liquiddev.elytrakey.options.ConfigLoader;
import de.liquiddev.elytrakey.ui.ElytraKeyOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class ElytraKey implements ModInitializer {

	public static boolean AUTO_EQUIP_FALL = true;
	public static boolean AUTO_EQUIP_FIREWORKS = false;
	public static boolean AUTO_UNEQUIP = true;
	public static boolean EASY_TAKEOFF = true;

	private static ElytraKey instance;
	private MinecraftClient mc = MinecraftClient.getInstance();

	private static KeyBinding swapElytraKeyBinding;
	private static KeyBinding elytraOptionsKeyBinding;

	public static ElytraKey getInstance() {
		return instance;
	}

	private boolean wasAutoEquipped = false;
	private boolean startFlying = false;
	private boolean boostNextTick = false;

	@Override
	public void onInitialize() {
		instance = this;
		new ConfigLoader().loadConfig();
		swapElytraKeyBinding = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("Swap Elytra", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.misc"));
		elytraOptionsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("ElytraKey Options",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "key.categories.misc"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (swapElytraKeyBinding.wasPressed()) {
				swapElytra();
			}

			while (elytraOptionsKeyBinding.wasPressed()) {
				mc.openScreen(new ElytraKeyOptions());
			}

			if (mc.world == null) {
				return;
			}

			boolean fireworksInHand = mc.player.inventory.getMainHandStack().getItem() == Items.FIREWORK_ROCKET;
			boolean isFalling = mc.player.fallDistance > 3;

			if ((AUTO_EQUIP_FIREWORKS && fireworksInHand) || (AUTO_EQUIP_FALL && isFalling)) {
				boolean elytraEquipped = isElytraEquipped();
				if (!elytraEquipped) {
					equipElytra();
					wasAutoEquipped = true;
				}
			} else {
				boolean unequip = AUTO_UNEQUIP && wasAutoEquipped && mc.player.isOnGround();
				if (unequip && isElytraEquipped()) {
					wasAutoEquipped = false;
					equipChestplate();
				}
			}

			if (EASY_TAKEOFF && fireworksInHand) {
				updateEasyTakeoff();
			}
		});

		System.out.println("ElytraKey mod initialized!");

	}

	private void updateEasyTakeoff() {
		
		if (mc.player.isFallFlying()) {
			if (boostNextTick) {
				boostNextTick = false;
				mc.options.keyJump.setPressed(false);
				mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
				mc.player.swingHand(Hand.MAIN_HAND);
			}
		} else { // not flying

			if (startFlying) {
				// press space to switch to flying state
				mc.options.keyJump.setPressed(true);
				boostNextTick = true;
				startFlying = false;

			} else if (mc.options.keyUse.isPressed()) {

				// clicked with fireworks in air?
				if ((mc.crosshairTarget instanceof BlockHitResult)
						&& ((BlockHitResult) mc.crosshairTarget).getType() == Type.MISS) {

					// elytra already equipped?
					if (!isElytraEquipped()) {
						if (!equipElytra()) {
							return;
						}
						wasAutoEquipped = true;
					}

					// jump if onground
					if (mc.player.isOnGround()) {
						mc.player.jump();
					}

					// start takeoff
					startFlying = true;
					mc.options.keyJump.setPressed(false);
				}
			}
		}
	}

	public boolean isElytraEquipped() {
		ItemStack chest = mc.player.inventory.getArmorStack(2);
		return chest.getItem() == Items.ELYTRA;
	}

	public boolean equipElytra() {
		ItemStack chest = mc.player.inventory.getArmorStack(2);

		if (chest.getItem() != Items.ELYTRA) {
			int elytraSlot = searchItem(Items.ELYTRA);

			if (elytraSlot == -1) {
				return false;
			}

			mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, elytraSlot, SlotActionType.SWAP,
					mc.player);
		}
		return true;
	}

	public boolean equipChestplate() {
		int chestSlot = findChestplate();

		if (chestSlot == -1) {
			return false;
		}

		mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, chestSlot, SlotActionType.SWAP,
				mc.player);
		return true;
	}

	private void swapElytra() {
		if (isElytraEquipped()) {
			boolean equipped = equipChestplate();

			// No chestplate found?
			if (!equipped) {
				int emptySlot = mc.player.inventory.getEmptySlot();

				if (emptySlot < 0) {
					print("Inventory full!");
				} else {
					mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, emptySlot,
							SlotActionType.SWAP, mc.player);
				}
			}
		} else {
			boolean equipped = equipElytra();

			if (!equipped) {
				print("No Elytra found in main inventory.");
			}
		}
	}

	private int findChestplate() {
		// Netherite chestplate
		int chestSlot = searchItem(Items.NETHERITE_CHESTPLATE);
		if (chestSlot != -1) {
			return chestSlot;
		}

		// Diamond chestplate
		chestSlot = searchItem(Items.DIAMOND_CHESTPLATE);
		if (chestSlot != -1) {
			return chestSlot;
		}

		// Iron chestplate
		chestSlot = searchItem(Items.IRON_CHESTPLATE);
		if (chestSlot != -1) {
			return chestSlot;
		}

		return -1;
	}

	private int searchItem(Item item) {
		DefaultedList<ItemStack> container = mc.player.inventory.main;
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	public void print(String message) {
		mc.player.sendMessage(new LiteralText(message), false);
	}
}
