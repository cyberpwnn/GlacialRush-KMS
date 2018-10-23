package com.volmit.grush.util;

import com.volmit.volume.lang.json.JSONObject;

public interface Writable
{
	public JSONObject toJSON();

	public void toJSON(JSONObject j);

	public void fromJSON(JSONObject j);
}
