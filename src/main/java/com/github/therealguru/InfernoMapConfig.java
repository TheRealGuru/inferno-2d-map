package com.github.therealguru;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("inferno-map-config")
public interface InfernoMapConfig extends Config
{
	@ConfigItem(
		keyName = "batColour",
		name = "Bat Colour",
		description = "The colour for bats in the renderer"
	)
	default Color batColour()
	{
		return Color.PINK;
	}

	@ConfigItem(
			keyName = "blobColour",
			name = "Blob Colour",
			description = "The colour for blobs in the renderer"
	)
	default Color blobColour()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
			keyName = "meleeColour",
			name = "Melee Colour",
			description = "The colour for meleers in the renderer"
	)
	default Color meleeColour()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "rangeColour",
			name = "Range Colour",
			description = "The colour for rangers in the renderer"
	)
	default Color rangeColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "mageColour",
			name = "Mage Colour",
			description = "The colour for magers in the renderer"
	)
	default Color mageColour()
	{
		return Color.BLUE;
	}

	@ConfigItem(
			keyName = "pillarColour",
			name = "Pillar Colour",
			description = "The colour for pillars in the renderer"
	)
	default Color pillarColour()
	{
		return Color.BLACK;
	}

	@ConfigItem(
			keyName = "emptyColour",
			name = "Empty Colour",
			description = "The colour for a blank space"
	)
	default Color emptyColour()
	{
		return Color.WHITE;
	}
}
