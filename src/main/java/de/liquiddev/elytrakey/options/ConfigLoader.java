package de.liquiddev.elytrakey.options;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.liquiddev.elytrakey.ElytraKey;

public class ConfigLoader {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final String configPath = "config/elytrakey.json";

	public void loadConfig() {

		File file = new File(configPath);
		ConfigModel config;

		if (file.exists()) {
			try (FileReader fr = new FileReader(file)) {
				config = gson.fromJson(fr, ConfigModel.class);

			} catch (IOException ex) {
				config = new ConfigModel();
			}
		} else {
			config = new ConfigModel();
		}

		// load config
		ElytraKey.AUTO_EQUIP_FALL = config.autoEquipFall;
		ElytraKey.AUTO_EQUIP_FIREWORKS = config.autoEquipFirework;
		ElytraKey.AUTO_UNEQUIP = config.autoUnequip;
		ElytraKey.EASY_TAKEOFF = config.easyTakeoff;
	}

	public void saveConfig() {
		ConfigModel config = new ConfigModel();

		// set config
		config.autoEquipFall = ElytraKey.AUTO_EQUIP_FALL;
		config.autoEquipFirework = ElytraKey.AUTO_EQUIP_FIREWORKS;
		config.autoUnequip = ElytraKey.AUTO_UNEQUIP;
		config.easyTakeoff = ElytraKey.EASY_TAKEOFF;

		File file = new File(configPath);

		try (FileWriter fw = new FileWriter(file)) {
			gson.toJson(config, fw);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Could not save ElytraKey config", ex);
		}
	}
}
