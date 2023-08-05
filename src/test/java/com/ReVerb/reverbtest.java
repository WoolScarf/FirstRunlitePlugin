package com.ReVerb;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class reverbtest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(reverb.class);
		RuneLite.main(args);
	}
}