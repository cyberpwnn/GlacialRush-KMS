package com.volmit.grush.content.item;

import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundArmorColapse;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.util.text.C;

public class PickupArmor extends Pickup
{
	public PickupArmor()
	{
		super("pickup_armor");
		setName("Armor Pickup");
		setStackSize(1);
	}

	@Override
	public HealthLayer getHealthLayer()
	{
		HealthLayer h = new HealthLayerBase(getName());
		h.setCurrent(150);
		h.setUnlimited();
		h.setColapseSound(new SoundArmorColapse());
		h.setDamageSound(new SoundArmorAbsorb());
		h.setColor(C.GOLD);
		h.setPitch(1.25f);
		h.setResistance(0.5f);
		return h;
	}

	@Override
	public float getMax()
	{
		return 450;
	}
}
