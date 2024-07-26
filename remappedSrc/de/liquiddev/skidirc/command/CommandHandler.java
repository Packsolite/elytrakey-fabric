package de.liquiddev.skidirc.command;

import de.liquiddev.ircclient.IrcClient;
import de.liquiddev.skidirc.SkidIrc;

public class CommandHandler {

	private SkidIrc irc;

	public CommandHandler(SkidIrc irc) {
		this.irc = irc;
	}

	/**
	 * Handles a command and returns true, if the message should be intercepted.
	 * 
	 * @param command
	 * @param args
	 * @return
	 */
	public boolean handleCommand(String command, String[] args) {
		IrcClient client = irc.getClient();

		switch (command.toLowerCase()) {
		case "irc":
			client.executeCommand(String.join(" ", args));
			break;
		case "c":
			if (args.length < 1) {
				irc.print("§c.c [message]");
				return true;
			}
			client.sendChatMessage(String.join(" ", args));
			break;
		case "l":
			if (args.length < 1) {
				irc.print("§c.l [message]");
				return true;
			}
			client.sendLocalChatMessage(String.join(" ", args));
			break;
		case "r":
			if (args.length < 1) {
				irc.print("§c.r [message]");
				return true;
			}
			client.sendWhisperMessage(irc.getLastWhisperTarget(), String.join(" ", args));
			break;
		case "msg":
			if (args.length < 2) {
				irc.print("§c.msg <player> [message]");
				return true;
			}
			client.sendWhisperMessage(args[0], String.join(" ", args).substring(args[0].length() + 1));
			break;
		default:
			return false;
		}
		return true;
	}
}
