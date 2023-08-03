package com.ReVerb;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("reverbVars")
public interface ReVerbConfig extends Config {
	@ConfigItem(
			keyName = "replaceList",
			name = "Replacements list",
			description = "Format: 'Example Option:Replacement text' pairs, seperated by semicolons (;)"
	)
	default String inputString() {
		return "";
	}
}
