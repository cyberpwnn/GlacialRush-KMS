package com.volmit.grush.content.item;

import com.volmit.grush.health.HealthLayer;

public class PickupMana extends Pickup
{
	public PickupMana()
	{
		super("pickup_mana");
		setName("Mana Pickup");
		setStackSize(64);
	}

	@Override
	public HealthLayer getHealthLayer()
	{
		return null;
	}

	@Override
	public float getMax()
	{
		return 1250;
	}
}
