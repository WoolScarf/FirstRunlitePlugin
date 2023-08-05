package com.ReVerb;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("reverbVars")
public interface reverbconfig extends Config {


	@ConfigItem(
			position = 1,
			keyName = "replaceList",
			name = "Replacements list",
			description = "Format: 'Option:Replacement text', pairs seperated by semicolons (;)."
	)
	default String inputString() {
		return "";
	}

	@ConfigItem(
			position = 2,
			keyName = "colorReplacement",
			name = "Highlight replacement color",
			description = "You can choose to recolor replaced phrases!"
	)
	default Color highlightColor(){
		return new Color(255,255,255);
	}


}
