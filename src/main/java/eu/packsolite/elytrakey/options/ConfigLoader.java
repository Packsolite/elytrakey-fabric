package eu.packsolite.elytrakey.options;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final File file = new File("config/elytrakey.json");

	public ConfigModel loadConfig() {
		if (!file.exists()) return ConfigModel.DEFAULT;
		try (FileReader fr = new FileReader(file)) {
			ConfigModel config = gson.fromJson(fr, ConfigModel.class);
			return config != null ? config : ConfigModel.DEFAULT;
		} catch (IOException ex) {
			return ConfigModel.DEFAULT;
		}
	}

	public void saveConfig(ConfigModel config) {
		try (FileWriter fw = new FileWriter(file)) {
			gson.toJson(config, fw);
		} catch (IOException ex) {
			throw new RuntimeException("Could not save ElytraKey config", ex);
		}
	}
}
