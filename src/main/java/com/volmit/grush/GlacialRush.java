package com.volmit.grush;

import java.io.IOException;

import org.bukkit.event.EventHandler;

import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.event.ContentRecipeRegistryEvent;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.grush.content.sound.SoundShieldAbsorb;
import com.volmit.grush.content.sound.SoundShieldColapse;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;

@CommandTag("&b[&8GR&b]&7: ")
public class GlacialRush extends VolumePlugin
{
	@Start
	public void start()
	{
		try
		{
			ContentManager.cacheResources(this);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		ContentManager.reloadContentManager();
	}

	@Stop
	public void stop()
	{

	}

	@EventHandler
	public void on(ContentRegistryEvent e)
	{
		e.register(new SoundShieldAbsorb());
		e.register(new SoundShieldColapse());
	}

	@EventHandler
	public void on(ContentRecipeRegistryEvent e)
	{

	}
}
