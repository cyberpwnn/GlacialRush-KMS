package com.volmit.grush.character;

public class CharacterBrute extends BaseCharacter
{
	public CharacterBrute()
	{
		super();

		health().setMax(1000f);
		healthDelay().setCurrent(250);
		healthRegen().setCurrent(0.15f);
		energy().setMax(750f);
		energyDelay().setCurrent(120);
		energyRegen().setCurrent(1f);
		shield().setMax(250f);
		shieldDelay().setCurrent(60);
		shieldRegen().setCurrent(0.83f);
		armor().setCurrent(500f);
		speed().setCurrent(0.85f);
		visibility().setCurrent(2.5f);
	}
}
