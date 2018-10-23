package com.volmit.grush.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.entity.Player;

import com.volmit.grush.character.BaseCharacter;
import com.volmit.grush.character.ICharacter;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.pawn.Async;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.pawn.Tick;
import com.volmit.volume.bukkit.service.IService;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.json.JSONObject;

public class CharacterSVC implements IService
{
	private GMap<String, ICharacter> characters;
	private int l;

	@Start
	public void start()
	{
		l = 0;
		characters = new GMap<String, ICharacter>();

		try
		{
			reload();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void reload() throws IOException
	{
		getFolder().mkdirs();

		if(getFolder().listFiles().length == 0)
		{
			generateDefaults();
		}

		characters.clear();

		for(File i : getFolder().listFiles())
		{
			if(i.getName().endsWith(".json"))
			{
				ICharacter c = new BaseCharacter(null, read(i));
				characters.put(c.getCharacterType(), c);
			}
		}

		l = getFolder().listFiles().length;
	}

	private JSONObject read(File i) throws IOException
	{
		BufferedReader bu = new BufferedReader(new FileReader(i));
		String c = "";
		String l = "";

		while((l = bu.readLine()) != null)
		{
			c += l;
		}

		bu.close();

		return new JSONObject(c);
	}

	private void generateDefaults() throws IOException
	{
		HealthLayer hp = new HealthLayerBase("Health");
		hp.setMax(1000);
		hp.setRegenInterval(10);
		hp.setRegenAmount(7);
		hp.setRegenDelay(100);
		hp.setCurrent(1000);

		HealthLayer sp = new HealthLayerBase("Shield");
		sp.setMax(250);
		sp.setRegenInterval(10);
		sp.setRegenAmount(27);
		sp.setRegenDelay(100);
		sp.setCurrent(250);

		HealthLayer ap = new HealthLayerBase("Armor");
		ap.setCurrent(100);
		ap.setResistance(0.25f);

		BaseCharacter bc = new BaseCharacter(null, "default");
		bc.getHealthPool().addLayer(hp);
		bc.getHealthPool().addLayer(ap);
		bc.getHealthPool().addLayer(sp);

		PrintWriter pw = new PrintWriter(new FileWriter(new File(getFolder(), "default.json")));
		pw.println(bc.toJSON().toString(4));
		pw.close();
	}

	@Stop
	public void stop()
	{

	}

	@Async
	@Tick(100)
	public void tick()
	{
		if(l != getFolder().listFiles().length)
		{
			try
			{
				reload();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public File getFolder()
	{
		return VolumePlugin.vpi.getDataFolder("characters");
	}

	public ICharacter get(Player p, String c)
	{
		if(characters.containsKey(c))
		{
			return new BaseCharacter(p, characters.get(c).toJSON());
		}

		return null;
	}

	public ICharacter anyCharacterType(Player p)
	{
		return get(p, characters.k().get(0));
	}
}
