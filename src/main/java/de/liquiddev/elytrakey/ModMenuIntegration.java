package de.liquiddev.elytrakey;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.liquiddev.elytrakey.ui.ElytraKeyOptions;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ElytraKeyOptions();
	}
}
