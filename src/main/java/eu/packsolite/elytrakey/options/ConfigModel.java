package eu.packsolite.elytrakey.options;

import lombok.With;

@With
public record ConfigModel(
	boolean autoEquipFall,
	double autoEquipFallVelocity,
	boolean easyTakeoff,
	boolean autoEquipFirework,
	boolean autoUnequip
) {
	public static final ConfigModel DEFAULT = new ConfigModel(true, -0.65, true, false, true);
}
