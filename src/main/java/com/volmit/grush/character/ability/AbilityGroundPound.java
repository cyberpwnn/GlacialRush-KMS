package com.volmit.grush.character.ability;

import org.bukkit.util.Vector;

import com.volmit.grush.character.ICharacter;
import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundShieldColapse;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.FinalInteger;
import com.volmit.volume.math.M;

public class AbilityGroundPound extends BasicAbility
{
	public AbilityGroundPound()
	{
		super("Ground Pound");
	}

	@Override
	public void fire(ICharacter c)
	{
		if(!c.useEnergy(500))
		{
			return;
		}

		HealthLayer h = new HealthLayerBase("Shatter Armor");
		h.setColapseSound(new SoundShieldColapse());
		h.setDamageSound(new SoundArmorAbsorb());
		h.setColor(C.DARK_PURPLE);
		h.setUnlimited();
		h.setCurrent(5800);
		h.setResistance(1.25f);
		h.setPitch(0.25f);
		h.setRegenAmount(-265);
		h.setRegenInterval(25);
		h.setRegenDelay(35);
		h.setHealthPerDamagePoint(-1997);
		c.getHealthPool().addLayer(h);
		Vector vv = c.getPlayer().getLocation().getDirection().clone().multiply(3.35);
		vv.setY(1.96);
		c.getPlayer().setVelocity(vv);
		c.getPlayer().setAllowFlight(false);
		c.getPlayer().setFlying(false);
		h.markDamaged();
		checkForH(M.ms(), c);
	}

	private void checkForH(long ms, ICharacter c)
	{
		FinalInteger fn = new FinalInteger(-1);
		fn.set(S.m.syncRepeating(10, new Runnable()
		{

			@Override
			public void run()
			{
				if(c.getHealthPool().getLayer("shatter_armor") != null && c.getHealthPool().getLayer("shatter_armor").getCurrent() == 0)
				{
					c.getHealthPool().getLayers().remove(c.getHealthPool().getLayer("shatter_armor"));
				}

				if(c.getHealthPool().getLayer("shatter_armor") == null && M.ms() - ms > 4000)
				{
					c.getPlayer().setAllowFlight(true);
					S.m.cancel(fn.get());
				}
			}
		}));
	}
}
