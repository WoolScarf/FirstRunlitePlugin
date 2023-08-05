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
	Pattern p = Pattern.compile("[^-a-zA-z:;0-9\\s]");

	@Inject
	private Client client;

	@Inject
	private ReVerbConfig config;

	String[] forbiddenChanges = {"Walk here", "Cancel"};

	boolean sentNonAlphaInput = false;
	boolean sentEmptyStringError = false;
	boolean sentBadFormatError = false;
	boolean sentEmptyInputError = false;
	boolean sentFoundForbiddenPhrase = false;
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
		if (containsNonAlpha(userInput)) {
			if (!sentNonAlphaInput) {
				client.addChatMessage(ChatMessageType.PLAYERRELATED, "", "ReVerb only works with English letters, '-' (dash) ':' (colon) or ';' (semicolon)", "ReVerb Plugin:");
				sentNonAlphaInput = true;
			}
			wellWrittenInput = false;
			sentWellWritten = false;
			return;
		}
		sentNonAlphaInput = false;

		// Find pairs between semicolons, split into distinct strings
		String[] inputParsedIntoPairs = userInput.split(";");

		// Check fo
		if (inputParsedIntoPairs.length == 1 && inputParsedIntoPairs[0].isEmpty()) {
			if (!sentEmptyInputError) {
				client.addChatMessage(ChatMessageType.PLAYERRELATED, "", "ReVerb found nothing to replace!", "");
				sentEmptyInputError = true;
			}
			wellWrittenInput = false;
			sentWellWritten = false;
			return;
		}
		sentEmptyInputError = false;


		// Parse pair into search and replace strings
		for (String parsedPairs : inputParsedIntoPairs)
		{
			// Find and isolate search:replace strings
			String[] pairStrings = parsedPairs.split(":");

			// If format doesn't fit, skip
			if (pairStrings.length != 2){
				if (!sentBadFormatError) {
					client.addChatMessage(
							ChatMessageType.PLAYERRELATED,
							"",
							"ReVerb found an invalid pair! make sure to separate pairs with a semicolon (;)!",
							""
					);
					sentBadFormatError = true;
				}
				wellWrittenInput = false;
				sentWellWritten = false;
				continue;
			}

			// At this point we know there is a "good" pair
			pairStrings[0] = pairStrings[0].trim();
			pairStrings[1] = pairStrings[1].trim();


			if (pairStrings[0].length() == 0 || pairStrings[1].length() == 0)
			{
				if (!sentEmptyStringError) {
					client.addChatMessage(
							ChatMessageType.PLAYERRELATED,
							"",
							"ReVerb found an empty phrase! There might be a misplaced colon (:) or an incomplete pair somewhere!",
							""
					);
					sentEmptyStringError = true;
				}
				wellWrittenInput = false;
				sentWellWritten = false;
				continue;
			}

			for (String forbiddenPhrase : forbiddenChanges){
				if (forbiddenPhrase.equals(pairStrings[0])){
					if (!sentFoundForbiddenPhrase) {
						client.addChatMessage(
								ChatMessageType.PLAYERRELATED,
								"",
								"ReVerb found '" + forbiddenPhrase + "' as a phrase. This messes with things!",
								""
						);
						sentFoundForbiddenPhrase = true;
					}
					wellWrittenInput = false;
					sentWellWritten = false;
					continue;
				}
			}

			// Passed all checks, apply change
			if (menuEntryAdded.getOption().equals(pairStrings[0]))
			{
				String color = String.format("%02x%02x%02x",
						config.highlightColor().getRed(),
						config.highlightColor().getGreen(),
						config.highlightColor().getBlue());
				menuEntryAdded.getMenuEntry().setOption("<col=" + color + ">" + pairStrings[1]);
			}
		}

		if (wellWrittenInput)
		{
			sentEmptyStringError = false;
			sentBadFormatError = false;
			sentNonAlphaInput = false;
			sentFoundForbiddenPhrase = false;

			if (!sentWellWritten)
			{
				client.addChatMessage(
						ChatMessageType.PLAYERRELATED,
						"",
						"ReVerb is working! If something doesn't look right. Make sure capitalizations are correct!",
						""
				);
				sentWellWritten = true;
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
