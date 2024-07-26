package de.liquiddev.skidirc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.liquiddev.skidirc.SkidIrc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

	@Shadow
	protected TextFieldWidget chatField;

	@Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		String message = this.chatField.getText().trim();

		if (message.startsWith(".")) {
			if (keyCode == 257 || keyCode == 335) {

				// split message
				String cmd = message.substring(1, message.split(" ")[0].length());
				String[] args = message.length() > cmd.length() + 1
						? message.substring(message.split(" ")[0].length() + 1).split(" ")
						: new String[0];

				// handle command
				boolean command = SkidIrc.getInstance().getCommandHandler().handleCommand(cmd, args);

				if (command) {
					// cancel chat message
					MinecraftClient.getInstance().openScreen(null);
					info.setReturnValue(true);
				}
			}
		}
	}
}
