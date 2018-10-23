package com.volmit.grush.character.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.P;
import com.volmit.grush.character.ICharacter;
import com.volmit.grush.projectile.TracerFireball;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.world.Area;
import com.volmit.volume.lang.collections.FinalInteger;
import com.volmit.volume.lang.collections.GList;

public class AbilityFireBall extends BasicAbility
{
	public AbilityFireBall()
	{
		super("Fire Ball");
	}

	@Override
	public void fire(ICharacter c)
	{
		boolean shotgun = c.getPlayer().isSneaking();

		for(int i = 0; i < (shotgun ? 6 : 1); i++)
		{
			if(!c.useEnergy(150))
			{
				break;
			}

			new S(0)
			{
				@Override
				public void run()
				{
					TracerFireball tf = new TracerFireball(c.getPlayer(), P.targetBlock(c.getPlayer(), 41));
					FinalInteger fn = new FinalInteger(0);
					tf.setMaxTimeAlive(200);
					tf.setFireTicks(0);
					tf.speed(1.1).gforce(0.46).compound(c.getPlayer().getLocation().getDirection()).fire();

					fn.set(S.m.syncRepeating(3, new Runnable()
					{
						@Override
						public void run()
						{
							if(tf.getFe() == null || tf.getFe().isDead())
							{
								S.m.cancel(fn.get());
								return;
							}

							Area a = new Area(tf.getFe().getLocation(), 9);

							for(Entity i : new GList<Entity>(a.getNearbyEntities()).shuffleCopy())
							{
								if(i instanceof Player && !i.equals(c.getPlayer()) && !i.isDead())
								{
									tf.setTarget(((LivingEntity) i).getEyeLocation());
									break;
								}
							}
						}
					}));

				}
			};
		}
	}
}
