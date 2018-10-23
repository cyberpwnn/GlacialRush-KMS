package com.volmit.grush.character;

import org.bukkit.entity.Player;

import com.volmit.grush.health.HealthPool;
import com.volmit.grush.sched.Ticked;
import com.volmit.grush.util.Writable;

public interface ICharacter extends Ticked, Writable
{
	public Player getPlayer();

	public float getEnergy();

	public void setEnergy(float energy);

	public boolean useEnergy(float energy);

	public void destroy();

	public HealthPool getHealthPool();

	public String getCharacterType();

	public void tickEnergy();
}
