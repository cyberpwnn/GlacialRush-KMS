package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundHealthAbsorb extends CustomSound
{
	public SoundHealthAbsorb()
	{
		super("grush.health.absorb");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Health absorbed damage");
		addSound("health/absorb$", 1, 3);
	}
}
