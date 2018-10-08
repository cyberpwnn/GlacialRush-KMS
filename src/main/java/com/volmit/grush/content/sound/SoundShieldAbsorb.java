package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundShieldAbsorb extends CustomSound
{
	public SoundShieldAbsorb()
	{
		super("grush.shield.absorb");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Shield absorbed damage");
		addSound("shield/absorb$", 1, 2);
	}
}
