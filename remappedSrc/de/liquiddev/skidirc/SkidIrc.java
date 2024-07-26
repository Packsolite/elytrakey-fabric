package de.liquiddev.skidirc;

import java.io.InputStream;

import javax.net.ssl.SSLContext;

import de.liquiddev.ircclient.IrcClient;
import de.liquiddev.ircclient.api.SimpleIrcApi;
import de.liquiddev.ircclient.players.ClientType;
import de.liquiddev.ircclient.utils.IrcClientFactory;
import de.liquiddev.ircclient.utils.IrcServers;
import de.liquiddev.ircclient.utils.IrcUuid;
import de.liquiddev.skidirc.command.CommandHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class SkidIrc extends SimpleIrcApi implements ModInitializer {

	private static SkidIrc instance;
	private IrcClient client;
	private MinecraftClient mc = MinecraftClient.getInstance();
	private String lastWhisperTarget = "";
	private CommandHandler commandHandler;

	public static SkidIrc getInstance() {
		return instance;
	}

	@Override
	public void onInitialize() {
		instance = this;
		this.commandHandler = new CommandHandler(this);
		System.out.println("SkidIrc mod initialized!");
	}

	public void enable() {
		String ign = mc.getSession().getUsername();
		Identifier identifier = new Identifier("skidirc", "cacerts.jks");

		try (InputStream storeStream = MinecraftClient.getInstance().getResourceManager().getResource(identifier)
				.getInputStream()) {
			SSLContext sslc = IrcClientFactory.getDefault().createSslContext("JKS", "SSL", storeStream);

			this.client = new IrcClient(sslc, IrcServers.getDefaultServers(), IrcServers.getDefaultPort(),
					IrcUuid.getUuid(ClientType.SKID), ClientType.SKID, "zy3gHnVQj3sUAa69", ign);
			this.client.getApiManager().registerApi(this);

		} catch (Exception e) {
			System.out.println("Could not get IRC client ssl context: ");
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Skid irc enabled!");
	}

	public void print(String message) {
		this.onChatMessage(message);
	}

	@Override
	public void addChat(String message) {
		mc.player.sendMessage(new LiteralText(message), false);
	}

	@Override
	public void onWhisperMessage(String player, String message, boolean isFromMe) {
		if (!isFromMe) {
			lastWhisperTarget = player;
		}
		super.onWhisperMessage(player, message, isFromMe);
	}

	public IrcClient getClient() {
		return client;
	}

	public String getLastWhisperTarget() {
		return lastWhisperTarget;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
}
