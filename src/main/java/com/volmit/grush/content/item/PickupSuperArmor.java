package com.volmit.grush.content.item;

import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundArmorColapse;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.util.text.C;

public class PickupSuperArmor extends Pickup
{
	public PickupSuperArmor()
	{
		super("pickup_super_armor");
		setName("Super Armor Pickup");
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
		h.setColor(C.GREEN);
		h.setPitch(0.55f);
		h.setResistance(0.25f);
		return h;
	}

	@Override
	public float getMax()
	{
		return 225;
	}
}
