package com.volmit.grush.projectile;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import com.volmit.volume.bukkit.task.SR;
import com.volmit.volume.bukkit.util.particle.ParticleEffect;
import com.volmit.volume.bukkit.util.physics.VectorMath;
import com.volmit.volume.bukkit.util.sound.GSound;
import com.volmit.volume.math.M;

public class TracerFireball
{
	private Vector compound;
	private int fireTicks;
	private int maxTimeAlive;
	private double gforce;
	private double speed;
	private LivingEntity shooter;
	private Location target;
	private SmallFireball fe;
	private Location initial;
	private boolean other;

	public TracerFireball(LivingEntity shooter, Location target)
	{
		this.initial = null;
		this.compound = new Vector();
		this.fireTicks = 10;
		this.maxTimeAlive = 45;
		this.gforce = 0.5;
		this.speed = 1.8;
		this.shooter = shooter;
		this.target = target;
	}

	public void setInitial(Location initial)
	{
		this.initial = initial;
	}

	public Location getInitial()
	{
		return initial;
	}

	public SmallFireball getFe()
	{
		return fe;
	}

	public Vector getCompound()
	{
		return compound;
	}

	public void setCompound(Vector compound)
	{
		this.compound = compound;
	}

	public int getFireTicks()
	{
		return fireTicks;
	}

	public void setFireTicks(int fireTicks)
	{
		this.fireTicks = fireTicks;
	}

	public int getMaxTimeAlive()
	{
		return maxTimeAlive;
	}

	public void setMaxTimeAlive(int maxTimeAlive)
	{
		this.maxTimeAlive = maxTimeAlive;
	}

	public double getGforce()
	{
		return gforce;
	}

	public void setGforce(double gforce)
	{
		this.gforce = gforce;
	}

	public double getSpeed()
	{
		return speed;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	public LivingEntity getShooter()
	{
		return shooter;
	}

	public void setShooter(LivingEntity shooter)
	{
		this.shooter = shooter;
	}

	public Location getTarget()
	{
		return target;
	}

	public void setTarget(Location target)
	{
		this.target = target;
	}

	public TracerFireball compound(Vector compound)
	{
		this.compound = compound;
		return this;
	}

	public TracerFireball fireTicks(int fireTicks)
	{
		this.fireTicks = fireTicks;
		return this;
	}

	public TracerFireball maxTime(int maxTimeAlive)
	{
		this.maxTimeAlive = maxTimeAlive;
		return this;
	}

	public TracerFireball gforce(double gforce)
	{
		this.gforce = gforce;
		return this;
	}

	public TracerFireball speed(double speed)
	{
		this.speed = speed;
		return this;
	}

	public void fire()
	{
		fireball();
	}

	private void fireball()
	{
		LivingEntity c = shooter;

		if(!c.getLocation().getWorld().equals(target.getWorld()))
		{
			return;
		}

		if(target.distance(c.getLocation()) < 6)
		{
			return;
		}

		if(c.isDead())
		{
			return;
		}

		target.setDirection(new Vector());

		Vector vx = Vector.getRandom().subtract(Vector.getRandom()).setY(0.3).normalize().clone().add(compound).normalize();
		fe = c.launchProjectile(SmallFireball.class, vx);
		fe.setIsIncendiary(fireTicks > 0);
		fe.setFireTicks(fireTicks);
		fe.setGravity(true);

		if(getInitial() != null)
		{
			fe.teleport(getInitial());
		}

		new SR()
		{
			@Override
			public void run()
			{
				if(fe.isDead())
				{
					new GSound(Sound.ENTITY_GENERIC_EXPLODE, 0.75f, 1.59f).play(fe.getLocation());
					cancel();
					return;
				}

				if(fe.getTicksLived() > maxTimeAlive)
				{
					fe.remove();
					return;
				}

				if(M.r(0.7))
				{
					if(other)
					{
						new GSound(Sound.AMBIENT_CAVE, 0.75f, (float) (Math.random() * 2)).play(fe.getLocation());
					}

					else
					{
						new GSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.15f, (float) (Math.random() * 2)).play(fe.getLocation());
						new GSound(Sound.ENTITY_ENDERDRAGON_FLAP, 1.15f, (float) (Math.random() * 2)).play(fe.getLocation());

					}
				}

				if(other)
				{
					ParticleEffect.CLOUD.display(fe.getVelocity(), 0.15f, fe.getLocation(), 100);
					ParticleEffect.CLOUD.display(fe.getVelocity(), 0.4f, fe.getLocation(), 100);
					ParticleEffect.CLOUD.display(fe.getVelocity(), 0.65f, fe.getLocation(), 100);
				}

				else
				{
					ParticleEffect.FLAME.display(fe.getVelocity(), 0.15f, fe.getLocation(), 100);
					ParticleEffect.FLAME.display(fe.getVelocity(), 0.4f, fe.getLocation(), 100);
					ParticleEffect.FLAME.display(fe.getVelocity(), 0.65f, fe.getLocation(), 100);
				}

				fe.setVelocity(fe.getVelocity().clone().add(VectorMath.reverse(VectorMath.direction(target, fe.getLocation())).clone().multiply(gforce)).clone().normalize().multiply(speed));
			}
		};

		ParticleEffect.LAVA.display(1.5f, 6, c.getLocation(), 32);
		new GSound(Sound.ENTITY_BLAZE_SHOOT, 1.7f, 1.9f + (float) (Math.random() * 0.3)).play(c.getLocation());
	}

	public void other()
	{
		other = true;
	}
}