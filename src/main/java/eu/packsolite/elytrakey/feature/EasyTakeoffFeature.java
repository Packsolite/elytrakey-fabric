package eu.packsolite.elytrakey.feature;

import eu.packsolite.elytrakey.ElytraKey;
import eu.packsolite.elytrakey.util.InventoryHelper;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

@RequiredArgsConstructor
public class EasyTakeoffFeature {

	private static final Minecraft mc = Minecraft.getInstance();

	private final AutoSwapFeature autoSwapFeature;
	private boolean startFlying = false;
	private boolean boostNextTick = false;

	public void update(boolean fireworksInMainHand, boolean fireworksInOffHand) {
		var config = ElytraKey.getConfig();

		if (config.easyTakeoff() && (fireworksInMainHand || fireworksInOffHand)) {
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
		} else {
			if (startFlying) {
				mc.options.keyJump.setDown(true);
				boostNextTick = true;
				startFlying = false;
			} else if (mc.options.keyUse.isDown()) {
				if (mc.hitResult instanceof BlockHitResult && mc.hitResult.getType() == Type.MISS) {
					if (!InventoryHelper.isElytraEquipped()) {
						if (!InventoryHelper.equipElytra()) {
							return;
						}
						autoSwapFeature.markAutoEquipped();
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
