package com.volmit.grush.character.ability;

import org.bukkit.Sound;
import org.bukkit.util.Vector;

import com.volmit.grush.character.ICharacter;
import com.volmit.grush.damage.DamageType;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.util.ShockEffect;
import com.volmit.volume.bukkit.util.sound.GSound;
import com.volmit.volume.bukkit.util.world.Impulse;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.math.M;

public class AbilityCharge extends BasicAbility
{
	public AbilityCharge()
	{
		super("Charge");
	}

	@Override
	public void fire(ICharacter c)
	{
		double power = 0;
		HealthLayer ll = null;
		for(HealthLayer i : c.getHealthPool().getLayers())
		{
			if(i.isVampiric())
			{
				power += i.getCurrent() / 12;
				ll = i;
			}
		}

		if(ll != null && power > 8.5 && c.useEnergy(350))
		{
			new GSound(Sound.ENTITY_GENERIC_EXPLODE, 6f, (float) (power / 6f)).play(c.getPlayer().getLocation());

			for(int i = 0; i < 3; i++)
			{
				new GSound(Sound.ENTITY_LIGHTNING_THUNDER, 6f, (float) (power / (float) i / 3f)).play(c.getPlayer().getLocation());
				new ShockEffect((float) (power / 4f)).play(c.getPlayer().getEyeLocation(), Vector.getRandom().subtract(Vector.getRandom()));
			}

			c.getPlayer().sendMessage("Power: " + F.f(M.clip(power, 0, 24), 1));
			c.getHealthPool().damageLayerOnly(10000, ll, DamageType.BLUNT, 1);
			new Impulse(power).force(18f, 1.5f).damage(19.5, 0.1).ignore(c.getPlayer()).punch(c.getPlayer().getLocation().clone().add(0, -0.7, 0));
		}
	}
}
