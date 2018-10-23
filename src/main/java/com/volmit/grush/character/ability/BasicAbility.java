package com.volmit.grush.character.ability;

import com.volmit.grush.character.ICharacter;
import com.volmit.volume.lang.json.JSONObject;

public abstract class BasicAbility implements IAbility
{
	private String name;

	public BasicAbility(String name)
	{
		this.name = name;
	}

	public BasicAbility(JSONObject j)
	{
		fromJSON(j);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public abstract void fire(ICharacter c);

	@Override
	public JSONObject toJSON()
	{
		JSONObject ja = new JSONObject();
		toJSON(ja);
		return ja;
	}

	@Override
	public void toJSON(JSONObject j)
	{
		j.put("name", getName());
	}

	@Override
	public void fromJSON(JSONObject j)
	{
		name = j.getString("name");
	}
}
