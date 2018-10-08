package com.volmit.grush.content.sound;

import com.volmit.fulcrum.custom.CustomSound;

public class SoundShieldColapse extends CustomSound
{
	public SoundShieldColapse()
	{
		super("grush.shield.colapse");
		setSuggestedVolume(1f);
		setSuggestedPitch(1f);
		setSubtitle("Shield colapsed");
		addSound("shield/colapse$", 1, 1);
	}
}
