package com.volmit.grush.content.item;

import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundArmorColapse;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.util.text.C;

public class PickupWard extends Pickup
{
	public PickupWard()
	{
		super("pickup_ward");
		setName("Ward Pickup");
		setStackSize(1);
	}

	@Override
	public HealthLayer getHealthLayer()
	{
		HealthLayer h = new HealthLayerBase(getName());
		h.setCurrent(75);
		h.setUnlimited();
		h.setColapseSound(new SoundArmorColapse());
		h.setDamageSound(new SoundArmorAbsorb());
		h.setColor(C.LIGHT_PURPLE);
		h.setPitch(1.65f);
		h.setResistance(0.25f);
		return h;
	}

	@Override
	public float getMax()
	{
		return 225;
	}
}
