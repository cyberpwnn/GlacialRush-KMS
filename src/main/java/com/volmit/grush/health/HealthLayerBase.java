package com.volmit.grush.health;

import java.awt.Color;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.custom.CustomSound;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleRedstone;
import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.TemporaryDescriptor;
import com.volmit.grush.damage.DamageModifier;
import com.volmit.grush.damage.DamageType;
import com.volmit.volume.bukkit.pawn.Async;
import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.util.data.Edgy;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.json.JSONArray;
import com.volmit.volume.lang.json.JSONObject;
import com.volmit.volume.math.M;

public class HealthLayerBase implements HealthLayer
{
	private LivingEntity p;
	private String name;
	private boolean regenerative;
	private int regenInterval;
	private float regenAmount;
	private float max;
	private float current;
	private float resistance;
	private int regenDelay;
	private long lastDamage;
	private String colapseSound;
	private String damageSound;
	private CustomSound colapse;
	private CustomSound damage;
	private C color;
	private float pitch;
	private GList<DamageModifier> modifiers;
	private float healthPerDamagePoint;

	public HealthLayerBase(String name)
	{
		this.name = name;
		modifiers = new GList<DamageModifier>();
		regenAmount = 0;
		regenInterval = 1;
		regenerative = false;
		max = -1;
		current = 0;
		resistance = 1;
		regenDelay = 0;
		colapseSound = "none";
		damageSound = "none";
		markDamaged();
		pitch = 1f;
		healthPerDamagePoint = 0;
		color = C.WHITE;

		for(CustomSound i : new GList<>(Fulcrum.contentRegistry.getSounds()))
		{
			if(i.getNode().equals(colapseSound))
			{
				colapse = i;
			}

			if(i.getNode().equals(damageSound))
			{
				damage = i;
			}
		}
	}

	public LivingEntity getP()
	{
		return p;
	}

	public void setP(LivingEntity p)
	{
		this.p = p;
	}

	public HealthLayerBase(JSONObject jsonObject)
	{
		this("<loading>");
		fromJSON(jsonObject);
	}

	@Override
	public void toJSON(JSONObject j)
	{
		j.put("color", color.name());
		j.put("name", getName());
		j.put("current", getCurrent());
		j.put("regenerative", isRegenerative());
		j.put("regen-interval", getRegenInterval());
		j.put("regen-amount", getRegenAmount());
		j.put("max", getMax());
		j.put("pitch", pitch);
		j.put("resistance", getResistance());
		j.put("regen-delay", getRegenDelay());
		j.put("colapse-sound", colapseSound);
		j.put("damage-sound", damageSound);
		j.put("vampiric", healthPerDamagePoint);

		JSONArray ja = new JSONArray();

		for(DamageModifier i : getModifiers())
		{
			ja.put(i.toJSON());
		}

		j.put("modifiers", ja);
	}

	@Override
	public void fromJSON(JSONObject j)
	{
		name = j.getString("name");
		regenerative = j.getBoolean("regenerative");
		regenInterval = j.getInt("regen-interval");
		regenAmount = (float) j.getDouble("regen-amount");
		max = (float) j.getDouble("max");
		resistance = (float) j.getDouble("resistance");
		regenDelay = j.getInt("regen-delay");
		colapseSound = j.getString("colapse-sound");
		damageSound = j.getString("damage-sound");
		current = (float) j.getDouble("current");
		color = C.valueOf(j.getString("color"));
		System.out.println("Color is " + color.name());
		pitch = (float) j.getDouble("pitch");
		healthPerDamagePoint = (float) j.getDouble("vampiric");

		for(CustomSound i : Fulcrum.contentRegistry.getSounds())
		{
			if(i.getNode().equals(colapseSound))
			{
				colapse = i;
			}

			if(i.getNode().equals(damageSound))
			{
				damage = i;
			}
		}

		JSONArray ja = j.getJSONArray("modifiers");

		for(int i = 0; i < ja.length(); i++)
		{
			getModifiers().add(new DamageModifier(ja.getJSONObject(i)));
		}
	}

	@Override
	public C getColor()
	{
		return color;
	}

	@Override
	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();
		toJSON(j);
		return j;
	}

	@Override
	public void markDamaged()
	{
		lastDamage = TICK.tick;
	}

	@Override
	public void tick(long delta)
	{
		if(isRegenerative() && delta % regenInterval == 0 && getDamageSince() > regenDelay)
		{
			mod(regenAmount, false);
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getId()
	{
		return getName().toLowerCase().replaceAll(" ", "_");
	}

	@Override
	public boolean isRegenerative()
	{
		return regenerative;
	}

	@Override
	public float getMax()
	{
		return max;
	}

	@Override
	public float getCurrent()
	{
		return current;
	}

	@Override
	public void setCurrent(float current)
	{
		this.current = current;
	}

	@Override
	public void setMax(float max)
	{
		this.max = max;
	}

	@Override
	public boolean hasMax()
	{
		return getMax() != -1;
	}

	@Override
	public void setUnlimited()
	{
		setMax(-1);
	}

	@Override
	public float getResistance()
	{
		return resistance;
	}

	@Override
	public float mod(float g)
	{
		return mod(g, true);
	}

	@Override
	public float mod(float g, boolean capture)
	{
		float f = g * getResistance();
		float c = current;

		// Damage fully absorbed
		if(f < 0 && current + f > 0)
		{
			current = current + f;
			validate(c, capture);
			return 0;
		}

		// Damage colapsed layer
		else if(f < 0 && current + f <= 0)
		{
			current = 0;
			f = -(current + f);
		}

		// Heals
		else if(f > 0)
		{
			current += f;
			validate(c, capture);
			return 0;
		}

		validate(c, capture);
		return f / getResistance();
	}

	@Override
	public void validate(float last)
	{
		validate(last, true);
	}

	private void validate(float last, boolean capture)
	{
		if(hasMax())
		{
			if(getCurrent() > getMax())
			{
				setCurrent(getMax());
			}
		}

		if(getCurrent() < 0)
		{
			setCurrent(0);
		}

		if(last > getCurrent() && capture)
		{
			if(last > 0 && getCurrent() <= 0)
			{
				onColapse();
			}

			else
			{
				onDamaged(last - getCurrent());
			}
		}

		else if(last < getCurrent() && last > 0 && capture)
		{
			onHealed(current - last);
		}
	}

	@Override
	public void onColapse()
	{
		if(colapse != null && getP() != null)
		{
			new Audio(colapse).v(1.5f).p(pitch).play(getP().getLocation());
			pulseFX(52, 4.6);
		}
	}

	@Override
	public void onHealed(float p)
	{
		// TODO why the hell is this here?
	}

	@Override
	public void onDamaged(float amt)
	{
		if(damage != null && getP() != null)
		{
			new Audio(damage).v(1.5f).p(pitch).play(getP().getLocation());
			new A()
			{
				@Override
				public void run()
				{
					damaged(getColor().toString() + "&l&g" + (F.f((int) amt)), getP());
					pulseFX(22, 0.7);
				}
			};
		}
	}

	private void pulseFX(int amt, double dist)
	{
		for(int i = 0; i < amt; i++)
		{
			new ParticleRedstone().setColor(new Color(color.dye().getColor().asRGB())).play(getP().getLocation().clone().add(new Vector(0, 1.5, 0)).add(Vector.getRandom().subtract(Vector.getRandom()).multiply(dist)), 42.5);
		}
	}

	@Edgy
	@Async
	private void damaged(String c, Entity e)
	{
		Location initial = e.getLocation().clone().add(new Vector(0, 0.7, 0));
		TemporaryDescriptor d = GLOSS.getSourceLibrary().createTemporaryDescriptor("dmg-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, 1000);
		d.addLine((c));
		Vector mot = Vector.getRandom().subtract(Vector.getRandom()).multiply(0.08);
		mot.setY(0.43);

		d.bindPosition(() ->
		{

			if(TICK.tick % 4 == 0)
			{
				mot.setY(mot.getY() - 0.0183);
				initial.add(mot);
			}

			return initial;
		});

		d.setLocation(initial);
		GLOSS.getSourceLibrary().register(d);
	}

	@Override
	public double getPercent()
	{
		return hasMax() ? getCurrent() / getMax() : 1f;
	}

	@Override
	public void setRegenAmount(float amt)
	{
		regenAmount = amt;
		regenerative = true;
	}

	@Override
	public void setRegenInterval(int ticks)
	{
		regenInterval = ticks;
		regenerative = true;

		if(regenInterval <= 0)
		{
			regenInterval = 1;
		}
	}

	@Override
	public float getRegenAmount()
	{
		return regenAmount;
	}

	@Override
	public int getRegenInterval()
	{
		return regenInterval;
	}

	@Override
	public int getRegenDelay()
	{
		return regenDelay;
	}

	@Override
	public void setRegenDelay(int ticks)
	{
		regenDelay = ticks;
		regenerative = true;
	}

	@Override
	public long getLastDamage()
	{
		return lastDamage;
	}

	@Override
	public long getDamageSince()
	{
		return TICK.tick - getLastDamage();
	}

	@Override
	public String getColapseSound()
	{
		return colapseSound;
	}

	@Override
	public void setColapseSound(String colapseSound)
	{
		this.colapseSound = colapseSound;
	}

	@Override
	public String getDamageSound()
	{
		return damageSound;
	}

	@Override
	public void setDamageSound(String damageSound)
	{
		this.damageSound = damageSound;
	}

	@Override
	public void setResistance(float f)
	{
		resistance = f;
	}

	@Override
	public float getEffective()
	{
		return getCurrent() / getResistance();
	}

	@Override
	public float getRegenPerSecond()
	{
		return (getRegenAmount() / (float) getRegenInterval()) * 20;
	}

	@Override
	public boolean isRegenerating()
	{
		if(!isFull() && isRegenerative() && getRegenAmount() > 0 && getRegenDelay() < getDamageSince())
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean isDecaying()
	{
		if(!isColapsed() && isRegenerative() && getRegenAmount() < 0 && getRegenDelay() < getDamageSince())
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean isFull()
	{
		return hasMax() ? getCurrent() == getMax() : false;
	}

	@Override
	public boolean isColapsed()
	{
		return getCurrent() == 0;
	}

	@Override
	public float getHealthPerDamagePoint()
	{
		return healthPerDamagePoint;
	}

	@Override
	public void setHealthPerDamagePoint(float healthPerDamagePoint)
	{
		this.healthPerDamagePoint = healthPerDamagePoint;
	}

	@Override
	public boolean isVampiric()
	{
		return getHealthPerDamagePoint() != 0;
	}

	@Override
	public void vampiricDamage(double damage)
	{
		if(isVampiric())
		{
			mod((float) (getHealthPerDamagePoint() * damage));
		}
	}

	@Override
	public void setColor(C c)
	{
		this.color = c;
	}

	@Override
	public void setDamageSound(CustomSound cs)
	{
		damage = cs;
	}

	@Override
	public void setColapseSound(CustomSound cs)
	{
		colapse = cs;
	}

	@Override
	public void setPitch(float p)
	{
		this.pitch = p;
	}

	@Override
	public GList<DamageModifier> getModifiers()
	{
		return modifiers;
	}

	@Override
	public void addModifier(DamageType type, float factor)
	{
		getModifiers().add(new DamageModifier(type, factor));
	}

	@Override
	public boolean hasType(DamageType type)
	{
		for(DamageModifier i : getModifiers())
		{
			if(i.getType().equals(type))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public float computeBonusResistance(float bonus, DamageType dt)
	{
		return 0;
	}
}
