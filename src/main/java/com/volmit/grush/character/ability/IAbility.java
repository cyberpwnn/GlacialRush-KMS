package com.volmit.grush.character.ability;

import com.volmit.grush.character.ICharacter;
import com.volmit.grush.util.Writable;

public interface IAbility extends Writable
{
	public String getName();

	public void fire(ICharacter c);
}
