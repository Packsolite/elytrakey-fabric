package eu.packsolite.elytrakey.feature;

import eu.packsolite.elytrakey.ElytraKey;
import eu.packsolite.elytrakey.util.InventoryHelper;
import net.minecraft.client.Minecraft;

public class AutoSwapFeature {

	private static final Minecraft mc = Minecraft.getInstance();
	private boolean wasAutoEquipped = false;

	public void update(boolean fireworksInMainHand) {
		var config = ElytraKey.getConfig();

		boolean isFalling = !mc.player.onGround() && mc.player.getDeltaMovement().y() < config.autoEquipFallVelocity();
		boolean hasLanded = mc.player.onGround() || mc.player.isInWater();

		if ((config.autoEquipFirework() && fireworksInMainHand) || (config.autoEquipFall() && isFalling)) {
			if (!InventoryHelper.isElytraEquipped()) {
				InventoryHelper.equipElytra();
				wasAutoEquipped = true;
			}
		} else {
			boolean unEquip = config.autoUnequip() && wasAutoEquipped && hasLanded;
			if (unEquip && InventoryHelper.isElytraEquipped()) {
				wasAutoEquipped = false;
				InventoryHelper.equipChestplate();
			}
		}
	}

	public void markAutoEquipped() {
		wasAutoEquipped = true;
	}
}
