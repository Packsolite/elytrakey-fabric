package eu.packsolite.elytrakey.options;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static eu.packsolite.elytrakey.ModConstants.MOD_ID;

@Slf4j(topic = MOD_ID)
public class ConfigLoader {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final File file = new File("config/elytrakey.json");

	public ConfigModel loadConfig() {
		if (!file.exists()) return ConfigModel.DEFAULT;
		try (FileReader fr = new FileReader(file)) {
			ConfigModel config = gson.fromJson(fr, ConfigModel.class);
			return config != null ? config : ConfigModel.DEFAULT;
		} catch (IOException ex) {
			log.warn("Failed to load config, using defaults", ex);
			return ConfigModel.DEFAULT;
		}
	}

	public void saveConfig(ConfigModel config) {
		try (FileWriter fw = new FileWriter(file)) {
			gson.toJson(config, fw);
		} catch (IOException ex) {
			log.error("Failed to save config", ex);
			throw new RuntimeException("Could not save ElytraKey config", ex);
		}
	}
}
