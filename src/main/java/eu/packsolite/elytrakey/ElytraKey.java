package eu.packsolite.elytrakey;

import com.mojang.blaze3d.platform.InputConstants;
import eu.packsolite.elytrakey.options.ConfigLoader;
import eu.packsolite.elytrakey.ui.ElytraKeyOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ElytraKey implements ModInitializer {

	private static final int OFF_HAND_SLOT_ID = 40;

	public static boolean AUTO_EQUIP_FALL = true;
	public static boolean AUTO_EQUIP_FIREWORKS = false;
	public static boolean AUTO_UNEQUIP = true;
	public static boolean EASY_TAKEOFF = true;
	public static double AUTO_EQUIP_FALL_VELOCITY;

	private final Minecraft mc = Minecraft.getInstance();

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
		ClientTickEvents.END_CLIENT_TICK.register(_ -> tick());
	}

	private void tick() {
		while (swapElytraKeyBinding.consumeClick()) {
			InventoryHelper.swapElytra(mc);
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
			if (!InventoryHelper.isElytraEquipped(mc)) {
				InventoryHelper.equipElytra(mc);
				wasAutoEquipped = true;
			}
		} else {
			boolean unEquip = AUTO_UNEQUIP && wasAutoEquipped && hasLanded;
			if (unEquip && InventoryHelper.isElytraEquipped(mc)) {
				wasAutoEquipped = false;
				InventoryHelper.equipChestplate(mc);
			}
		}

		if (EASY_TAKEOFF && (fireworksInMainHand || fireworksInOffHand)) {
			updateEasyTakeoff(fireworksInMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
		}
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
				if (mc.hitResult instanceof BlockHitResult && mc.hitResult.getType() == Type.MISS) {
					if (!InventoryHelper.isElytraEquipped(mc)) {
						if (!InventoryHelper.equipElytra(mc)) {
							return;
						}
						wasAutoEquipped = true;
					}
					if (mc.player.onGround()) {
						mc.player.jumpFromGround();
					}
					startFlying = true;
					mc.options.keyJump.setDown(false);
				}
			}
		}
	}
}
