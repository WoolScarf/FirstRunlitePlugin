package com.ReVerb;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "ReVerb"
)

public class ReVerb extends Plugin
{
	Pattern p = Pattern.compile("[^a-zA-z:;\\s]");

	@Inject
	private Client client;

	@Inject
	private ReVerbConfig config;

	boolean sentInputError = false;
	boolean sentEmptyStringError = false;
	boolean sentBadFormatError = false;
	boolean sentEmptyInputError = false;
	boolean wellWrittenInput = true;
	boolean sentWellWritten = false;

	// The meat of the plugin
	@Subscribe
	public void onMenuEntryAdded( MenuEntryAdded menuEntryAdded ) {
		// Get raw user input
		wellWrittenInput = true;
		String userInput = config.inputString();

		// Confirm only intended chars exist
		// Notify player, only once until the problem is fixed. Once fixed, another mistake will give another error message
		if ( containsNonAlpha(userInput) ){
			if (!sentInputError) {
				client.addChatMessage(ChatMessageType.PLAYERRELATED, "", "ReVerb plugin disabled, you can only input English letters, ': (colon)' or '; (semicolon)'", "ReVerb Plugin:");
				sentInputError = true;
				sentWellWritten = false;
			}
			return;
		}
		sentInputError = false;


		// Find pairs between semicolons, split into distinct strings
		String[] inputParsedIntoPairs = userInput.split(";");

		// Check fo
		if ( inputParsedIntoPairs.length == 0)
		{
			if (!sentEmptyInputError) {
				client.addChatMessage(ChatMessageType.PLAYERRELATED, "", "ReVerb plugin disabled, please add something to replace!", "ReVerb Plugin:");
				sentEmptyInputError = true;
				wellWrittenInput = false;
				sentWellWritten = false;
			}
			return;
		}


		// Parse pair into search and replace strings
		for (String parsedPairs : inputParsedIntoPairs)
		{
			// Find and isolate search:replace strings
			String[] pairStrings = parsedPairs.split(":");

			// If format doesn't fit, skip
			if (pairStrings.length > 2)
			{
				if (!sentBadFormatError)
				{
					client.addChatMessage(
							ChatMessageType.PLAYERRELATED,
							"",
							"ReVerb skipped a replacement, make sure to separate pairs with a semicolon (;)!",
							""
					);
					sentBadFormatError = true;
					wellWrittenInput = false;
					sentWellWritten = false;
				}

			} else if (pairStrings.length == 0 || pairStrings.length == 1) {
				if (!sentEmptyStringError)
				{
					client.addChatMessage(
							ChatMessageType.PLAYERRELATED,
							"",
							"ReVerb skipped a replacement, there might be a stray colon (:) or an incomplete pair somewhere!",
							""
					);
					sentEmptyStringError = true;
					wellWrittenInput = false;
					sentWellWritten = false;
				}
			} else {
				if (menuEntryAdded.getOption().equals(pairStrings[0].trim())) {
					menuEntryAdded.getMenuEntry().setOption(pairStrings[1].trim());
				}
				if (wellWrittenInput)
				{
					sentEmptyStringError = false;
					sentBadFormatError = false;
					sentInputError = false;
					if (!sentWellWritten) {
						client.addChatMessage(
								ChatMessageType.PLAYERRELATED,
								"",
								"ReVerb is working! If something doesn't look right. make sure capitalizations are correct!",
								""
						);
						sentWellWritten = true;
					}
				}
			}
		}
	}


	public boolean containsNonAlpha(String name) {
		return p.matcher(name).find();
	}


	@Provides
	ReVerbConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ReVerbConfig.class);
	}
}
