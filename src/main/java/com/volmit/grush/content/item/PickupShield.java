package com.volmit.grush.content.item;

import com.volmit.grush.content.sound.SoundShieldAbsorb;
import com.volmit.grush.content.sound.SoundShieldColapse;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.util.text.C;

public class PickupShield extends Pickup
{
	public PickupShield()
	{
		super("pickup_shield");
		setName("Shield Pickup");
		setStackSize(1);
	}

	@Override
	public HealthLayer getHealthLayer()
	{
		HealthLayer h = new HealthLayerBase(getName());
		h.setCurrent(300);
		h.setUnlimited();
		h.setColapseSound(new SoundShieldColapse());
		h.setDamageSound(new SoundShieldAbsorb());
		h.setColor(C.AQUA);
		h.setPitch(1.25f);
		h.setResistance(1f);
		return h;
	}

	@Override
	public float getMax()
	{
		return 900;
	}
}
