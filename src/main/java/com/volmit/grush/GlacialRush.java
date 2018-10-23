package com.volmit.grush;

import java.io.IOException;

import org.bukkit.event.EventHandler;

import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.grush.content.item.PickupArmor;
import com.volmit.grush.content.item.PickupMana;
import com.volmit.grush.content.item.PickupShield;
import com.volmit.grush.content.item.PickupSuperArmor;
import com.volmit.grush.content.item.PickupWard;
import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundArmorColapse;
import com.volmit.grush.content.sound.SoundHealthAbsorb;
import com.volmit.grush.content.sound.SoundShieldAbsorb;
import com.volmit.grush.content.sound.SoundShieldBuild;
import com.volmit.grush.content.sound.SoundShieldColapse;
import com.volmit.grush.service.CharacterSVC;
import com.volmit.grush.service.GameSVC;
import com.volmit.volume.bukkit.U;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;

@CommandTag("&b[&8GR&b]&7: ")
public class GlacialRush extends VolumePlugin
{
	public int a = 0;

	@Start
	public void start()
	{
		kickFulcrum();
		startServices();
	}

	@Stop
	public void stop()
	{

	}

	@EventHandler
	public void on(ContentRegistryEvent e)
	{
		// Sounds
		e.register(new SoundShieldAbsorb());
		e.register(new SoundShieldColapse());
		e.register(new SoundShieldBuild());
		e.register(new SoundHealthAbsorb());
		e.register(new SoundArmorAbsorb());
		e.register(new SoundArmorColapse());

		// Items - Pickups
		e.register(new PickupArmor());
		e.register(new PickupSuperArmor());
		e.register(new PickupWard());
		e.register(new PickupShield());
		e.register(new PickupMana());
	}

	private void startServices()
	{
		U.getService(CharacterSVC.class);
		U.getService(GameSVC.class);
	}

	private void kickFulcrum()
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
}
