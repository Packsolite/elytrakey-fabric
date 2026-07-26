package eu.packsolite.elytrakey;

import com.mojang.blaze3d.platform.InputConstants;
import eu.packsolite.elytrakey.feature.AutoSwapFeature;
import eu.packsolite.elytrakey.feature.EasyTakeoffFeature;
import eu.packsolite.elytrakey.options.ConfigLoader;
import eu.packsolite.elytrakey.options.ConfigModel;
import eu.packsolite.elytrakey.ui.ElytraKeyOptions;
import eu.packsolite.elytrakey.util.InventoryHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import static eu.packsolite.elytrakey.ModConstants.MOD_ID;

@Slf4j(topic = MOD_ID)
public class ElytraKey implements ModInitializer {

	@Setter
	@Getter
	private static ConfigModel config;

	private final Minecraft mc = Minecraft.getInstance();

	private static KeyMapping swapElytraKeyBinding;
	private static KeyMapping elytraOptionsKeyBinding;

	private final AutoSwapFeature autoSwapFeature = new AutoSwapFeature();
	private final EasyTakeoffFeature easyTakeoffFeature = new EasyTakeoffFeature(autoSwapFeature);

	@Override
	public void onInitialize() {
		config = new ConfigLoader().loadConfig();
		KeyMapping.Category cat = KeyMapping.Category.register(Identifier.parse(MOD_ID));
		swapElytraKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elytrakey.swap", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, cat));
		elytraOptionsKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elytrakey.options", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, cat));
		ClientTickEvents.END_CLIENT_TICK.register(_ -> tick());
		log.info("Initialized");
	}

	private void tick() {
		if (mc.player == null) return;

		while (swapElytraKeyBinding.consumeClick()) {
			InventoryHelper.swapElytra();
		}

		while (elytraOptionsKeyBinding.consumeClick()) {
			mc.setScreenAndShow(new ElytraKeyOptions());
		}

		boolean fireworksInMainHand = mc.player.getInventory().getSelectedItem().getItem() == Items.FIREWORK_ROCKET;
		boolean fireworksInOffHand = mc.player.getInventory().getItem(InventoryHelper.OFF_HAND_SLOT).getItem() == Items.FIREWORK_ROCKET;

		autoSwapFeature.update(fireworksInMainHand);
		easyTakeoffFeature.update(fireworksInMainHand, fireworksInOffHand);
	}
}
