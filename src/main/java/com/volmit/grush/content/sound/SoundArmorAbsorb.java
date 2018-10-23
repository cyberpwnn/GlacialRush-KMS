package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundArmorAbsorb extends CustomSound
{
	public SoundArmorAbsorb()
	{
		super("grush.armor.absorb");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Armor absorbed damage");
		addSound("armor/absorb$", 1, 3);
	}
}
