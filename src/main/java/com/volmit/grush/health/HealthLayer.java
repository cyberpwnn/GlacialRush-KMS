package com.volmit.grush.health;

import com.volmit.fulcrum.custom.CustomSound;
import com.volmit.grush.damage.DamageModifier;
import com.volmit.grush.damage.DamageType;
import com.volmit.grush.sched.Ticked;
import com.volmit.grush.util.Writable;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.GList;

public interface HealthLayer extends Ticked, Writable
{
	public String getName();

	public String getId();

	public void setPitch(float p);

	public int getRegenDelay();

	public void validate(float last);

	public void setRegenDelay(int ticks);

	public void setColor(C c);

	public void setDamageSound(CustomSound cs);

	public void setColapseSound(CustomSound cs);

	public void markDamaged();

	public float getEffective();

	public long getLastDamage();

	public C getColor();

	public long getDamageSince();

	public boolean isRegenerative();

	public float getMax();

	public void setResistance(float f);

	public float getCurrent();

	public void setCurrent(float current);

	public void setMax(float max);

	public boolean hasMax();

	public void setUnlimited();

	public float getResistance();

	public float mod(float f);

	public void onColapse();

	public void onDamaged(float p);

	public double getPercent();

	public void setRegenAmount(float amt);

	public void setRegenInterval(int ticks);

	public float getRegenAmount();

	public boolean isFull();

	public boolean isColapsed();

	public int getRegenInterval();

	public void onHealed(float p);

	public String getColapseSound();

	public void setColapseSound(String colapseSound);

	public String getDamageSound();

	public void setDamageSound(String damageSound);

	public float getRegenPerSecond();

	public boolean isRegenerating();

	public boolean isDecaying();

	public float getHealthPerDamagePoint();

	public void setHealthPerDamagePoint(float healthPerDamagePoint);

	public boolean isVampiric();

	public boolean hasType(DamageType type);

	public float computeBonusResistance(float bonus, DamageType dt);

	public float mod(float g, boolean capture);

	public void vampiricDamage(double damage);

	public GList<DamageModifier> getModifiers();

	public void addModifier(DamageType type, float factor);
}
