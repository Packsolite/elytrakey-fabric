package de.liquiddev.elytrakey.ui;

import de.liquiddev.elytrakey.ElytraKey;
import de.liquiddev.elytrakey.options.ConfigLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.TooltipSupplier;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ElytraKeyOptions extends Screen {

	Text autoEquipText = new LiteralText("Automatically equip Elytra:");
	Text easyTakeofTooltipText = new LiteralText(
			"Automatically jump, boost and equip Elytra when right clicking a firework.");
	Text autoUnequipTooltipText = new LiteralText("Automatically switch back to chestplate when landing.");

	CheckboxWidget fallWidget;
	CheckboxWidget fireworkWidget;

	public ElytraKeyOptions() {
		super(new LiteralText("ElytraKey options"));
	}

	final int yOffset = 0;

	public void init() {

		this.addButton(fallWidget = new CheckboxWidget(this.width / 2 - 75, this.height / 6 + yOffset + 40, 150, 20,
				new LiteralText("when falling"), ElytraKey.AUTO_EQUIP_FALL, true));
		this.addButton(fireworkWidget = new CheckboxWidget(this.width / 2 - 75, this.height / 6 + yOffset + 60, 150, 20,
				new LiteralText("when holding Firework"), ElytraKey.AUTO_EQUIP_FIREWORKS, true));

		TooltipSupplier autoUnequipTooltip = (button, matrices, mouseX, mouseY) -> {
			drawTextWithShadow(matrices, textRenderer, autoUnequipTooltipText, mouseX - 50, mouseY + 20, 16777215);
		};
		this.addButton(new ButtonWidget(this.width / 2 - 75, this.height / 6 + yOffset + 90, 150, 20,
				new LiteralText("Auto Unequip: " + (ElytraKey.AUTO_UNEQUIP ? "On" : "Off")), button -> {
					ElytraKey.AUTO_UNEQUIP = !ElytraKey.AUTO_UNEQUIP;
					button.setMessage(new LiteralText("Auto Unequip: " + (ElytraKey.AUTO_UNEQUIP ? "On" : "Off")));
				}, autoUnequipTooltip));

		TooltipSupplier easyTakeoffTooltip = (button, matrices, mouseX, mouseY) -> {
			drawTextWithShadow(matrices, textRenderer, easyTakeofTooltipText, mouseX - 50, mouseY - 20, 16777215);
		};

		this.addButton(new ButtonWidget(this.width / 2 - 75, this.height / 6 + yOffset, 150, 20,
				new LiteralText("Easy Take-off: " + (ElytraKey.EASY_TAKEOFF ? "On" : "Off")), button -> {
					ElytraKey.EASY_TAKEOFF = !ElytraKey.EASY_TAKEOFF;
					button.setMessage(new LiteralText("Easy Take-Off: " + (ElytraKey.EASY_TAKEOFF ? "On" : "Off")));
				}, easyTakeoffTooltip));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);

		drawCenteredText(matrices, this.textRenderer, autoEquipText, this.width / 2 - 10,
				this.height / 6 + yOffset + 30, 16777215);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean b = super.mouseClicked(mouseX, mouseY, button);
		ElytraKey.AUTO_EQUIP_FALL = fallWidget.isChecked();
		ElytraKey.AUTO_EQUIP_FIREWORKS = fireworkWidget.isChecked();
		new ConfigLoader().saveConfig();
		return b;
	}
}
