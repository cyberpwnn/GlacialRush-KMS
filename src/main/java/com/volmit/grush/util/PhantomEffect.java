package com.volmit.grush.util;

import org.bukkit.Location;

import com.volmit.volume.lang.collections.GList;

/**
 * An effect that can be played
 * @author cyberpwn
 *
 */
public abstract class PhantomEffect implements VisualEffect
{
	@Override
	public GList<VisualEffect> getEffects()
	{
		return new GList<VisualEffect>();
	}

	@Override
	public abstract void play(Location l);

	@Override
	public void addEffect(VisualEffect e)
	{

	}
}
