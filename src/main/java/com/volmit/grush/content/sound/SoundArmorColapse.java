package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundArmorColapse extends CustomSound
{
	public SoundArmorColapse()
	{
		super("grush.armor.colapse");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Armor colapsed");
		addSound("armor/colapse$", 1, 2);
	}
}
