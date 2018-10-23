package com.volmit.grush.health;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.S;
import com.volmit.grush.damage.DamageType;
import com.volmit.grush.sched.Ticked;
import com.volmit.grush.util.Writable;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.json.JSONArray;
import com.volmit.volume.lang.json.JSONObject;

public class HealthPool implements Ticked, Writable
{
	private LivingEntity p;
	private GList<HealthLayer> layers;

	public HealthPool()
	{
		layers = new GList<HealthLayer>();
	}

	public LivingEntity getP()
	{
		return p;
	}

	public void setP(LivingEntity p)
	{
		this.p = p;
	}

	public void setLayers(GList<HealthLayer> layers)
	{
		this.layers = layers;
	}

	public HealthPool(JSONObject jsonObject)
	{
		this();
		fromJSON(jsonObject);
	}

	public GList<HealthLayer> getLayers()
	{
		return layers;
	}

	public void clearLayers()
	{
		layers.clear();
	}

	public void addLayer(HealthLayer l)
	{
		layers.add(0, l);
	}

	@Override
	public void tick(long delta)
	{
		for(HealthLayer i : layers)
		{
			if(((HealthLayerBase) i).getP() == null && getP() != null)
			{
				((HealthLayerBase) i).setP(getP());
			}

			if(i.isRegenerative())
			{
				i.tick(delta);
			}
		}

		if(getP() != null)
		{
			if(getP() instanceof Player)
			{
				Fulcrum.adapter.sendActionBar(displayBar(92), (Player) p);
			}

			else if(getP() instanceof LivingEntity)
			{
				new S()
				{
					@Override
					public void run()
					{
						getP().setCustomNameVisible(true);
						getP().setCustomName(displayBar(32));
					}
				};
			}
		}
	}

	private String displayBar(int maxCharx)
	{
		float maxChar = maxCharx;
		int charAlloc = (int) maxChar;
		float maxPoints = Math.max(getEffectiveHealthPoints(), getMaximumHealthPoints());
		float pointsPerChar = maxPoints / maxChar;
		String cbase = "|";
		String s = "";

		for(int i = layers.size() - 1; i >= 0; i--)
		{
			HealthLayer l = layers.get(i);
			int chars = (int) (l.getCurrent() / pointsPerChar);
			C c = l.getColor();
			charAlloc -= chars;

			if(chars > 0 && l.getDamageSince() < 7 && l.getDamageSince() % 2 == 0)
			{
				c = C.BLACK;
			}

			if(chars > 0 && l.isRegenerating())
			{
				int ca = (int) (l.getDamageSince() % chars);
				s += c + F.repeat(cbase, ca) + C.STRIKETHROUGH + F.repeat(cbase, 1) + C.RESET + c + F.repeat(cbase, chars - ca);
			}

			else if(chars > 0 && l.isDecaying())
			{
				int ca = (int) ((Long.MAX_VALUE - l.getDamageSince()) % chars);
				s += c + F.repeat(cbase, ca) + C.STRIKETHROUGH + F.repeat(cbase, 1) + C.RESET + c + F.repeat(cbase, chars - ca);
			}

			else
			{
				s += c + "" + C.STRIKETHROUGH + F.repeat(cbase, chars);
			}
		}

		if(charAlloc > 0 && !isFull())
		{
			s += C.BLACK + "" + C.STRIKETHROUGH + F.repeat(cbase, charAlloc);
			charAlloc = 0;
		}

		return s;
	}

	public boolean isFull()
	{
		return getEffectiveHealthPoints() == getMaximumHealthPoints();
	}

	public float getEffectiveHealthPoints()
	{
		float total = 0f;

		for(HealthLayer i : layers)
		{
			total += i.getCurrent();
		}

		return total;
	}

	public float getMaximumHealthPoints()
	{
		float total = 0f;

		for(HealthLayer i : layers)
		{
			if(i.hasMax())
			{
				total += i.getMax();
			}
		}

		return total;
	}

	public float getEffectiveHealth()
	{
		float total = 0f;

		for(HealthLayer i : layers)
		{
			total += i.getCurrent() / i.getResistance();
		}

		return total;
	}

	public float getMaximumHealth()
	{
		float total = 0f;

		for(HealthLayer i : layers)
		{
			if(i.hasMax())
			{
				total += i.getMax() / i.getResistance();
			}
		}

		return total;
	}

	public float getTotalHealth()
	{
		float total = 0f;

		for(HealthLayer i : layers)
		{
			total += i.getCurrent();
		}

		return total;
	}

	public float damage(float damage, DamageType x, float b, String... layerids)
	{
		float d = damage;
		GList<String> ld = new GList<String>(layerids);

		for(HealthLayer i : layers)
		{
			if(ld.contains(i.getId()))
			{
				d = damageLayerOnly(d, i, x, b);
			}
		}

		return d;
	}

	public float damage(float damage, DamageType dt, float b)
	{
		float d = damage;
		float bonus = b;
		float k = 1;
		for(HealthLayer i : layers)
		{
			float bx = i.computeBonusResistance(bonus, dt) / 3;
			bonus -= bx;

			if(bonus <= 0)
			{
				bonus = 0;
			}

			d = damageLayerOnly(d, i, dt, bonus / k);
			k += 0.25;
		}

		return d;
	}

	public float damageLayerOnly(float damage, String type, DamageType d, float b)
	{
		return damageLayerOnly(damage, getLayer(type), d, b);
	}

	public float damageLayerOnly(float damage, HealthLayer type, DamageType d, float b)
	{
		if(damage != 0)
		{
			type.markDamaged();
		}

		float dx = type.computeBonusResistance(b, d);
		return type.mod(-(damage + dx));
	}

	public HealthLayer getLayer(String id)
	{
		for(HealthLayer i : layers)
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
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
		JSONArray a = new JSONArray();

		for(HealthLayer i : layers)
		{
			a.put(i.toJSON());
		}

		j.put("layers", a);
	}

	@Override
	public void fromJSON(JSONObject j)
	{
		JSONArray a = j.getJSONArray("layers");
		layers.clear();

		for(int i = 0; i < a.length(); i++)
		{
			HealthLayer h = new HealthLayerBase(a.getJSONObject(i));
			layers.add(h);
		}
	}
}
