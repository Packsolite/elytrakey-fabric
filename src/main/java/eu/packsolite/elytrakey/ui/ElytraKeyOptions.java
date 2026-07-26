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

public class ElytraKeyOptions extends Screen {

	Component autoEquipText = Component.translatable("elytrakey.options.auto_equip");
	Component easyTakeofTooltipText = Component.translatable("elytrakey.options.tooltip.easy_takeoff");
	Component autoUnequipTooltipText = Component.translatable("elytrakey.options.tooltip.auto_unequip");

	Checkbox fallWidget;
	Checkbox fireworkWidget;

	public ElytraKeyOptions() {
		super(Component.translatable("elytrakey.options.title"));
	}

	final int yOffset = 0;

	public void init() {
		this.addRenderableWidget(fallWidget = Checkbox.builder(
				Component.translatable("elytrakey.options.when_falling"),
				this.font)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 40)
			.selected(ElytraKey.getConfig().autoEquipFall())
			.build());

		this.addRenderableWidget(fireworkWidget = Checkbox.builder(
				Component.translatable("elytrakey.options.when_firework"),
				this.font)
			.pos(this.width / 2 - 75, this.height / 6 + yOffset + 60)
			.selected(ElytraKey.getConfig().autoEquipFirework())
			.build());

		this.addEasyTakeoffButton();
		this.addAutoUnequipButton();
	}

	private void addEasyTakeoffButton() {
		Supplier<Component> buttonText = () -> Component.translatable(
			"elytrakey.options.easy_takeoff",
			ElytraKey.getConfig().easyTakeoff()
				? Component.translatable("options.on")
				: Component.translatable("options.off"));
		var tooltipText = Tooltip.create(easyTakeofTooltipText);

		Button.OnPress action = button -> {
			ElytraKey.setConfig(ElytraKey.getConfig().withEasyTakeoff(!ElytraKey.getConfig().easyTakeoff()));
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
		Supplier<Component> buttonText = () -> Component.translatable(
			"elytrakey.options.auto_unequip",
			ElytraKey.getConfig().autoUnequip()
				? Component.translatable("options.on")
				: Component.translatable("options.off"));
		var tooltipText = Tooltip.create(autoUnequipTooltipText);

		Button.OnPress action = button -> {
			ElytraKey.setConfig(ElytraKey.getConfig().withAutoUnequip(!ElytraKey.getConfig().autoUnequip()));
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
		ElytraKey.setConfig(ElytraKey.getConfig().withAutoEquipFall(fallWidget.selected()).withAutoEquipFirework(fireworkWidget.selected()));
		new ConfigLoader().saveConfig(ElytraKey.getConfig());
		return b;
	}
}
