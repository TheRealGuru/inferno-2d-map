package com.github.therealguru;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class Inferno2dMapPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(Inferno2dMapPlugin.class);
		RuneLite.main(args);
	}
}