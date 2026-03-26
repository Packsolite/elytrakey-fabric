package eu.packsolite.elytrakey.ui;

import eu.packsolite.elytrakey.ElytraKey;
import eu.packsolite.elytrakey.options.ConfigLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

import static net.minecraft.network.chat.Component.literal;

public class ElytraKeyOptions extends Screen {

	Component autoEquipText = literal("Automatically equip Elytra:");
	Component easyTakeofTooltipText = literal(
		"Automatically jump, boost and equip Elytra when right clicking a firework.");
	Component autoUnequipTooltipText = literal("Automatically switch back to chestplate when landing.");

	Checkbox fallWidget;
	Checkbox fireworkWidget;

	public ElytraKeyOptions() {
		super(literal("ElytraKey options"));
	}

	final int yOffset = 0;

	public void init() {
		this.addRenderableWidget(fallWidget = Checkbox.builder(
				literal("when falling"),
				this.font)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 40)
			.selected(ElytraKey.AUTO_EQUIP_FALL)
			.build());

		this.addRenderableWidget(fireworkWidget = Checkbox.builder(
				literal("when holding Firework"),
				this.font)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 60)
			.selected(ElytraKey.AUTO_EQUIP_FIREWORKS)
			.build());

		this.addEasyTakeoffButton();
		this.addAutoUnequipButton();
	}

	private void addEasyTakeoffButton() {
		Supplier<Component> buttonText = () -> literal("Easy Take-off: " + (ElytraKey.EASY_TAKEOFF ? "On" : "Off"));
		var tooltipText = Tooltip.create(easyTakeofTooltipText);

		Button.OnPress action = button -> {
			ElytraKey.EASY_TAKEOFF = !ElytraKey.EASY_TAKEOFF;
			button.setMessage(buttonText.get());
		};

		var button = Button.builder(buttonText.get(), action)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset)
			.size(150, 20)
			.tooltip(tooltipText)
			.build();

		this.addRenderableWidget(button);
	}

	private void addAutoUnequipButton() {
		Supplier<Component> buttonText = () -> literal("Auto Unequip: " + (ElytraKey.AUTO_UNEQUIP ? "On" : "Off"));
		var tooltipText = Tooltip.create(autoUnequipTooltipText);

		Button.OnPress action = button -> {
			ElytraKey.AUTO_UNEQUIP = !ElytraKey.AUTO_UNEQUIP;
			button.setMessage(buttonText.get());
		};

		var button = Button.builder(buttonText.get(), action)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 90)
			.size(150, 20)
			.tooltip(tooltipText)
			.build();

		this.addRenderableWidget(button);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
		super.extractRenderState(extractor, mouseX, mouseY, delta);
		extractor.centeredText(font, title, width / 2, 15, -1);
		extractor.centeredText(font, autoEquipText, width / 2 - 10, height / 6 + yOffset + 30, -1);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		boolean b = super.mouseClicked(click, doubled);
		ElytraKey.AUTO_EQUIP_FALL = fallWidget.selected();
		ElytraKey.AUTO_EQUIP_FIREWORKS = fireworkWidget.selected();
		new ConfigLoader().saveConfig();
		return b;
	}
}
