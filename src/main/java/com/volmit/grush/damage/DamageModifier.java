package com.volmit.grush.damage;

import com.volmit.grush.util.Writable;
import com.volmit.volume.lang.json.JSONObject;

public class DamageModifier implements Writable
{
	private DamageType type;
	private float factor;

	public DamageModifier(JSONObject j)
	{
		this(DamageType.SLASH, 1);
		fromJSON(j);
	}

	public DamageModifier(DamageType type, float factor)
	{
		this.type = type;
		this.factor = factor;
	}

	public DamageType getType()
	{
		return type;
	}

	public void setType(DamageType type)
	{
		this.type = type;
	}

	public float getFactor()
	{
		return factor;
	}

	public void setFactor(float factor)
	{
		this.factor = factor;
	}

	@Override
	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();
		toJSON(j);
		return j;
	}

	@Override
	public void toJSON(JSONObject j)
	{
		j.put("type", getType().name());
		j.put("factor", getFactor());
	}

	@Override
	public void fromJSON(JSONObject j)
	{
		setType(DamageType.valueOf(j.getString("type")));
		setFactor((float) j.getDouble("factor"));
	}
}
