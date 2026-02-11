package eu.packsolite.elytrakey.ui;

import eu.packsolite.elytrakey.ElytraKey;
import eu.packsolite.elytrakey.options.ConfigLoader;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Supplier;

import static net.minecraft.text.Text.literal;

public class ElytraKeyOptions extends Screen {

	Text autoEquipText = literal("Automatically equip Elytra:");
	Text easyTakeofTooltipText = literal(
		"Automatically jump, boost and equip Elytra when right clicking a firework.");
	Text autoUnequipTooltipText = literal("Automatically switch back to chestplate when landing.");

	CheckboxWidget fallWidget;
	CheckboxWidget fireworkWidget;

	public ElytraKeyOptions() {
		super(literal("ElytraKey options"));
	}

	final int yOffset = 0;

	public void init() {
		this.addDrawableChild(fallWidget = CheckboxWidget.builder(
				literal("when falling"),
				this.textRenderer)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 40)
			.checked(ElytraKey.AUTO_EQUIP_FALL)
			.build());

		this.addDrawableChild(fireworkWidget = CheckboxWidget.builder(
				literal("when holding Firework"),
				this.textRenderer)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 60)
			.checked(ElytraKey.AUTO_EQUIP_FIREWORKS)
			.build());

		this.addEasyTakeoffButton();
		this.addAutoUnequipButton();
	}

	private void addEasyTakeoffButton() {
		Supplier<Text> buttonText = () -> literal("Easy Take-off: " + (ElytraKey.EASY_TAKEOFF ? "On" : "Off"));
		var tooltipText = Tooltip.of(easyTakeofTooltipText);

		ButtonWidget.PressAction action = button -> {
			ElytraKey.EASY_TAKEOFF = !ElytraKey.EASY_TAKEOFF;
			button.setMessage(buttonText.get());
		};

		var button = ButtonWidget.builder(buttonText.get(), action)
			.position(this.width / 2 - 75, this.height / 6 + yOffset)
			.size(150, 20)
			.tooltip(tooltipText)
			.build();

		this.addDrawableChild(button);
	}

	private void addAutoUnequipButton() {
		Supplier<Text> buttonText = () -> literal("Auto Unequip: " + (ElytraKey.AUTO_UNEQUIP ? "On" : "Off"));
		var tooltipText = Tooltip.of(autoUnequipTooltipText);

		ButtonWidget.PressAction action = button -> {
			ElytraKey.AUTO_UNEQUIP = !ElytraKey.AUTO_UNEQUIP;
			button.setMessage(buttonText.get());
		};

		var button = ButtonWidget.builder(buttonText.get(), action)
			.position(this.width / 2 - 75, this.height / 6 + yOffset + 90)
			.size(150, 20)
			.tooltip(tooltipText)
			.build();

		this.addDrawableChild(button);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, -1);
		context.drawCenteredTextWithShadow(textRenderer, autoEquipText, width / 2 - 10, height / 6 + yOffset + 30, -1);
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		boolean b = super.mouseClicked(click, doubled);
		ElytraKey.AUTO_EQUIP_FALL = fallWidget.isChecked();
		ElytraKey.AUTO_EQUIP_FIREWORKS = fireworkWidget.isChecked();
		new ConfigLoader().saveConfig();
		return b;
	}
}
