package de.liquiddev.skidirc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.liquiddev.ircclient.players.IrcPlayer;
import de.liquiddev.ircclient.players.IrcRank;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

	@Inject(at = @At("HEAD"), method = "getPlayerName", cancellable = true)
	public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> info) {
		String name = playerListEntry.getProfile().getName();
		IrcPlayer player = IrcPlayer.getByIngameName(name);

		// is in IRC?
		if (player != null) {
			IrcRank rank = player.getRank();
			String color = rank == IrcRank.USER ? "§a" : rank.getChatColor();

			// prefix
			StringBuilder prefix = new StringBuilder();
			prefix.append("§8[");
			prefix.append(color);
			prefix.append("Skid-");
			prefix.append(rank.name().charAt(0));
			prefix.append(rank.name().substring(1).toLowerCase());
			prefix.append("§8]");

			// name color
			if (playerListEntry.getScoreboardTeam() != null) {
				prefix.append(playerListEntry.getScoreboardTeam().getColor());
				prefix.append(" ");
			} else
				prefix.append("§f ");

			// name
			if (playerListEntry.getDisplayName() != null) {
				info.setReturnValue(new LiteralText(prefix.toString()).append(playerListEntry.getDisplayName()));
			} else {
				prefix.append(name);
				info.setReturnValue(new LiteralText(prefix.toString()));
			}
		}
	}
}