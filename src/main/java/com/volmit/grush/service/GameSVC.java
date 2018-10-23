package com.volmit.grush.service;

import java.awt.Color;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleRedstone;
import com.volmit.gloss.api.glow.GlowManager;
import com.volmit.gloss.api.glow.Glower;
import com.volmit.gloss.api.util.VectorMath;
import com.volmit.grush.Gate;
import com.volmit.grush.character.ICharacter;
import com.volmit.grush.content.sound.SoundArmorAbsorb;
import com.volmit.grush.content.sound.SoundArmorColapse;
import com.volmit.grush.content.sound.SoundHealthAbsorb;
import com.volmit.grush.content.sound.SoundShieldAbsorb;
import com.volmit.grush.content.sound.SoundShieldColapse;
import com.volmit.grush.damage.DamageType;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.health.HealthLayerBase;
import com.volmit.grush.health.HealthPool;
import com.volmit.grush.projectile.TracerFireball;
import com.volmit.grush.util.ShockEffect;
import com.volmit.volume.bukkit.U;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.pawn.Tick;
import com.volmit.volume.bukkit.service.IService;
import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.particle.ParticleEffect;
import com.volmit.volume.bukkit.util.world.Area;
import com.volmit.volume.lang.collections.FinalInteger;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.math.M;

public class GameSVC implements IService
{
	private GMap<LivingEntity, HealthPool> entities;
	private GMap<Player, ICharacter> players;
	private GList<Projectile> proj;

	@Start
	public void start()
	{
		proj = new GList<Projectile>();
		players = new GMap<Player, ICharacter>();
		entities = new GMap<LivingEntity, HealthPool>();

		for(Player i : Bukkit.getOnlinePlayers())
		{
			join(i);
		}
	}

	@Stop
	public void stop()
	{
		for(Player i : Bukkit.getOnlinePlayers())
		{
			quit(i);
		}
	}

	@Tick
	public void tick()
	{
		try
		{
			TICK.tick++;

			for(Projectile i : proj.copy())
			{
				if(i.isDead())
				{
					proj.remove(i);
				}

				else
				{

				}

				int fbh = 0;

				for(Entity j : i.getLocation().getWorld().getNearbyEntities(i.getLocation(), 1.9, 1.9, 1.9))
				{
					if(j instanceof FallingBlock)
					{
						if(fbh > 10)
						{
							break;
						}

						FallingBlock fb = (FallingBlock) j;

						if(fb.getCustomName() != null && fb.getCustomName().contains(":"))
						{
							fbh++;
							String c = fb.getName().split(":")[0];
							String id = fb.getName().split(":")[1];
							Player owner = null;
							for(Player k : Bukkit.getOnlinePlayers())
							{
								if((k.getEntityId() + "").equals(id))
								{
									owner = k;
									break;
								}
							}

							if(owner.equals(i.getShooter()))
							{
								continue;
							}

							new Audio(new SoundShieldColapse()).v(3).p((float) ((Math.random() * 1f) + 1f)).play(i.getLocation());

							if(M.r(0.4))
							{
								new ShockEffect(1.5f).play(fb.getLocation(), Vector.getRandom().subtract(Vector.getRandom()));
							}

							i.remove();

							if(c.length() <= 1)
							{
								j.setVelocity(i.getVelocity().clone().multiply(0.75));
								j.setGravity(true);
							}

							else
							{
								fb.setCustomName(c.substring(1) + ":" + id);
								Glower g = GlowManager.create(fb, null);
								Glower g2 = GlowManager.create(fb, owner);

								switch(c.length() - 1)
								{
									case 5:
										g.setColor(ChatColor.RED);
										g2.setColor(ChatColor.DARK_AQUA);
										break;
									case 4:
										g.setColor(ChatColor.RED);
										g2.setColor(ChatColor.DARK_AQUA);
										break;
									case 3:
										g.setColor(ChatColor.RED);
										g2.setColor(ChatColor.BLUE);
										break;
									case 2:
										g.setColor(ChatColor.DARK_RED);
										g2.setColor(ChatColor.BLUE);
										break;
									case 1:
										g.setColor(ChatColor.DARK_RED);
										g2.setColor(ChatColor.DARK_BLUE);
										break;
								}

								g.setGlowing(true);
								g2.setGlowing(true);
							}
						}

					}
				}
			}

			for(Player i : players.k())
			{
				new A()
				{
					@Override
					public void run()
					{
						players.get(i).tick(TICK.tick);
					}
				};
			}

			new A()
			{
				@Override
				public void run()
				{
					for(LivingEntity i : entities.k())
					{
						if(!i.isDead())
						{
							entities.get(i).tick(TICK.tick);
						}

						else
						{
							entities.remove(i);
						}
					}
				}
			};
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void pulseFX(int amt, double dist, Location l, C c)
	{
		for(int i = 0; i < amt; i++)
		{
			new ParticleRedstone().setColor(new Color(c.dye().getColor().asRGB())).play(l.clone().add(new Vector(0, 1.5, 0)).add(Vector.getRandom().subtract(Vector.getRandom()).multiply(dist)), 42.5);
		}
	}

	private void join(Player i)
	{
		players.put(i, U.getService(CharacterSVC.class).anyCharacterType(i));
	}

	private void quit(Player i)
	{
		players.remove(i);
	}

	@EventHandler
	public void on(ProjectileHitEvent e)
	{
		if(e.getHitEntity() != null && e.getHitEntity() instanceof FallingBlock)
		{
			if(e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof Arrow)
			{
				Vector v = VectorMath.direction(((Player) e.getEntity().getShooter()).getLocation(), e.getEntity().getLocation());
				v.setY(0);
				Location ll = e.getHitEntity().getLocation().clone().add(v.clone().multiply(5.3));
				Player p = ((Player) e.getEntity().getShooter());

				if(!((Arrow) e.getEntity()).isCritical())
				{
					return;
				}

				for(int i = 0; i < 1; i++)
				{
					TracerFireball tf = new TracerFireball(((Player) e.getEntity().getShooter()), ll.clone().add(v.clone().multiply(25)));
					tf.setSpeed(1.2);
					tf.setGforce(0.7);
					tf.setFireTicks(0);
					tf.setMaxTimeAlive(100);
					tf.setInitial(ll.clone());
					tf.other();
					tf.fire();

					FinalInteger fn = new FinalInteger(0);
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
								if(i instanceof Player && !i.equals(p) && !i.isDead())
								{
									tf.setTarget(((LivingEntity) i).getEyeLocation());
									break;
								}
							}
						}
					}));
				}

				e.getEntity().remove();

			}
		}
	}

	@EventHandler
	public void on(PlayerRespawnEvent e)
	{
		players.get(e.getPlayer()).destroy();
		ICharacter c = U.getService(CharacterSVC.class).get(e.getPlayer(), players.get(e.getPlayer()).getCharacterType());

		if(c != null)
		{

			players.put(e.getPlayer(), c);
			new Audio().s(Sound.BLOCK_ANVIL_USE).v(1.45f).p(1.5f).play(e.getPlayer());
		}
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			ICharacter c = players.get(p);

			for(HealthLayer i : c.getHealthPool().getLayers())
			{
				if(i.isVampiric())
				{
					i.vampiricDamage(e.getDamage());
				}
			}
		}

		if(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
		{
			Player p = (Player) ((Projectile) e.getDamager()).getShooter();
			ICharacter c = players.get(p);

			for(HealthLayer i : c.getHealthPool().getLayers())
			{
				if(i.isVampiric())
				{
					i.vampiricDamage(e.getDamage() * 1.25);
				}
			}
		}
	}

	@EventHandler
	public void onKb(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Projectile)
		{
			if(e.getEntity() instanceof LivingEntity)
			{
				LivingEntity c = (LivingEntity) e.getEntity();
				double size = Gate.BOW_HEADSHOT_AABB_SIZE;
				double accuracy = Gate.BOW_HEADSHOT_CHECK_ITERATIONS;
				Vector vv = e.getDamager().getVelocity();

				if(e.getDamager() instanceof SmallFireball)
				{
					e.setDamage(e.getDamage() * 0.65);
				}

				if(e.getDamager() instanceof Arrow)
				{
					calcDamage(e.getEntity(), 10, DamageType.PIERCE, 200);
				}

				for(int i = 1; i < accuracy; i++)
				{
					e.getDamager().teleport(e.getDamager().getLocation().clone().add(vv.clone().multiply(1.0 / accuracy)));
					if(c.getWorld().getNearbyEntities(c.getEyeLocation().clone().add(0, 0.6, 0), (size * 1.3) / 2.0, (size * 0.68) / 2.0, (size * 1.3) / 2.0).contains(e.getDamager()))
					{
						c.getEyeLocation().getWorld().playSound(c.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.7f, 1.2f);
						c.getEyeLocation().getWorld().playSound(c.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.7f, 1.2f);
						c.getEyeLocation().getWorld().playSound(c.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.7f, 1.2f);

						new com.volmit.volume.bukkit.task.S()
						{
							@Override
							public void run()
							{
								c.setNoDamageTicks(0);
								c.damage(e.getDamage());
							}
						};

						ParticleEffect.CRIT.display(0.6f, 24, c.getEyeLocation(), 100);
						break;
					}
				}
			}
		}
	}

	private void calcDamage(Entity e, double damage, DamageType t, float b)
	{
		if(e instanceof Player)
		{
			Player p = (Player) e;

			if(p.getNoDamageTicks() > 0)
			{
				return;
			}

			p.setNoDamageTicks(5);
			ICharacter i = players.get(p);
			float r = i.getHealthPool().damage((float) damage * 51, t, b);

			if(r > 0)
			{
				p.setHealth(0);
			}
		}

		else if(e instanceof LivingEntity)
		{
			if(!entities.containsKey(e))
			{
				entities.put((LivingEntity) e, createHealthPool((LivingEntity) e));
			}

			HealthPool p = entities.get(e);
			p.tick(TICK.tick);
			e.setCustomNameVisible(false);
			float r = p.damage((float) damage * 51, t, b);

			if(r > 0)
			{
				((LivingEntity) e).setHealth(0);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private HealthPool createHealthPool(LivingEntity e)
	{
		HealthPool hp = new HealthPool();
		HealthLayer hl = new HealthLayerBase("Health");
		hl.setMax((float) (65 * e.getMaxHealth()));
		hl.setRegenAmount(34);
		hl.setCurrent((float) (70 * e.getMaxHealth()));
		hl.setRegenDelay(100);
		hl.setRegenInterval(5);
		hl.setColor(com.volmit.volume.bukkit.util.text.C.RED);
		hl.setDamageSound(new SoundHealthAbsorb());
		hp.addLayer(hl);

		if(e instanceof Wither)
		{
			HealthLayer al = new HealthLayerBase("Shield A");
			al.setMax(10000);
			al.setCurrent(10000);
			al.setRegenAmount(117);
			al.setRegenDelay(33);
			al.setRegenInterval(1);
			al.setResistance(1.54f);
			al.setColor(com.volmit.volume.bukkit.util.text.C.LIGHT_PURPLE);
			al.setDamageSound(new SoundShieldAbsorb());
			al.setColapseSound(new SoundShieldColapse());
			hp.addLayer(al);

			HealthLayer ax = new HealthLayerBase("Shield B");
			ax.setMax(10000);
			ax.setCurrent(10000);
			ax.setRegenAmount(177);
			ax.setRegenDelay(33);
			ax.setRegenInterval(2);
			ax.setResistance(1.54f);
			ax.setColor(com.volmit.volume.bukkit.util.text.C.BLUE);
			ax.setDamageSound(new SoundShieldAbsorb());
			ax.setColapseSound(new SoundShieldColapse());
			hp.addLayer(ax);

			HealthLayer av = new HealthLayerBase("Armor A");
			av.setMax(9400);
			av.setCurrent(9400);
			av.setRegenAmount(3);
			av.setRegenDelay(33);
			av.setRegenInterval(7);
			av.setResistance(0.45f);
			av.setColor(com.volmit.volume.bukkit.util.text.C.GREEN);
			av.setDamageSound(new SoundArmorAbsorb());
			av.setColapseSound(new SoundArmorColapse());
			hp.addLayer(av);

			HealthLayer xav = new HealthLayerBase("Armor B");
			xav.setMax(8700);
			xav.setCurrent(8700);
			xav.setRegenAmount(3);
			xav.setRegenDelay(33);
			xav.setRegenInterval(7);
			xav.setResistance(0.77f);
			xav.setColor(com.volmit.volume.bukkit.util.text.C.AQUA);
			xav.setDamageSound(new SoundArmorAbsorb());
			xav.setColapseSound(new SoundArmorColapse());
			hp.addLayer(xav);
		}

		if(e instanceof Zombie || e instanceof ZombieVillager || e instanceof org.bukkit.entity.Skeleton || e instanceof Stray)
		{
			HealthLayer al = new HealthLayerBase("Armor");
			al.setMax(100);
			al.setCurrent(100);
			al.setColor(com.volmit.volume.bukkit.util.text.C.GOLD);
			al.setResistance(0.5f);
			al.setDamageSound(new SoundArmorAbsorb());
			al.setColapseSound(new SoundArmorColapse());
			hp.addLayer(al);
		}

		if(e instanceof Witch || e instanceof Enderman)
		{
			HealthLayer al = new HealthLayerBase("Shield");
			al.setMax(900);
			al.setCurrent(900);
			al.setColor(com.volmit.volume.bukkit.util.text.C.BLUE);
			al.setDamageSound(new SoundShieldAbsorb());
			al.setColapseSound(new SoundShieldColapse());
			hp.addLayer(al);
		}

		hp.setP(e);

		return hp;
	}

	@EventHandler
	public void on(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL))
		{
			float amp = 1;
			double range = 5;
			ICharacter c = players.get(e.getEntity());
			if(c.getHealthPool().getLayer("shatter_armor") != null)
			{
				float p = c.getHealthPool().getLayer("shatter_armor").getCurrent();
				c.getHealthPool().getLayer("shatter_armor").mod(-p - 10);
				amp += p / 250f;
				range += p / 1350f;
			}

			Area a = new Area(e.getEntity().getLocation(), range);

			for(Entity i : a.getNearbyEntities())
			{
				double pct = 1.0 - (a.getLocation().distance(i.getLocation()) / range + 2.25);

				if(i.equals(e.getEntity()))
				{
					continue;
				}

				calcDamage(i, pct * (e.getDamage() * amp), DamageType.BLUNT, 50);

				if(i instanceof FallingBlock)
				{
					i.setGravity(false);
					i.setVelocity(VectorMath.direction(i.getLocation(), a.getLocation()).multiply(pct * 0.03));
				}
			}

			if(amp > 1)
			{
				e.setCancelled(true);
				return;
			}
		}

		calcDamage(e.getEntity(), e.getDamage(), DamageType.BLUNT, (float) (e.getDamage()));

		e.setCancelled(true);
	}

	@EventHandler
	public void on(AsyncPlayerChatEvent e)
	{
		if(e.getMessage().startsWith("!"))
		{
			try
			{
				U.getService(CharacterSVC.class).reload();
			}

			catch(IOException e1)
			{
				e1.printStackTrace();
			}
			String s = e.getMessage().substring(1);
			ICharacter c = U.getService(CharacterSVC.class).get(e.getPlayer(), s);

			if(c != null)
			{
				players.get(e.getPlayer()).destroy();
				players.put(e.getPlayer(), c);
				new Audio().s(Sound.BLOCK_ANVIL_USE).v(1.45f).p(1.5f).play(e.getPlayer());
				HealthPool p = c.getHealthPool();

				e.getPlayer().sendMessage(C.GRAY + "Class: " + C.WHITE + "Brute");
				e.getPlayer().sendMessage(C.GRAY + "Effective Health Pool: " + C.WHITE + F.f((int) p.getEffectiveHealth()) + C.GRAY + " / " + C.WHITE + F.f((int) p.getMaximumHealth()));

				for(HealthLayer i : p.getLayers())
				{
					e.getPlayer().sendMessage(i.getColor() + i.getName() + ": " + C.WHITE + F.f((int) i.getCurrent()) + C.GRAY + " Res: " + C.WHITE + F.pc(i.getResistance()) + C.GRAY + " Eff: " + C.WHITE + F.f((int) i.getEffective()) + C.GRAY + "" + (i.isRegenerative() ? (C.GRAY + " Reg: " + C.WHITE + F.f((int) i.getRegenPerSecond()) + "/s " + C.GRAY + " after " + F.time(i.getRegenDelay() * 50.0, 1)) : " No Regen"));
				}
			}
		}
	}

	@EventHandler
	public void on(ProjectileLaunchEvent e)
	{
		track(e.getEntity());
	}

	private void track(Projectile entity)
	{
		proj.add(entity);
	}

	@EventHandler
	public void on(EntityChangeBlockEvent e)
	{
		if(e.getEntity() instanceof FallingBlock)
		{
			e.setCancelled(true);
			e.getEntity().remove();
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		join(e.getPlayer());
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		quit(e.getPlayer());
	}

	public ICharacter getCharacter(Player p)
	{
		return players.get(p);
	}
}
