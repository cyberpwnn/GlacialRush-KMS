package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundShieldBuild extends CustomSound
{
	public SoundShieldBuild()
	{
		super("grush.shield.build");
		setSuggestedVolume(2.5f);
		setSuggestedPitch(1.3f);
		setSubtitle("Shield Constructed");
		addSound("shield/build$", 1, 1);
	}
}
