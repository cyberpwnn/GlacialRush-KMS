package com.volmit.grush.character.ability;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.sfx.Audio;
import com.volmit.gloss.api.glow.GlowManager;
import com.volmit.gloss.api.glow.Glower;
import com.volmit.gloss.api.util.VectorMath;
import com.volmit.grush.character.ICharacter;
import com.volmit.grush.content.sound.SoundShieldBuild;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

public class AbilityProtectiveBarrier extends BasicAbility
{
	public static GMap<Player, GList<FallingBlock>> use = new GMap<Player, GList<FallingBlock>>();

	public AbilityProtectiveBarrier()
	{
		super("Protective Barrier");
	}

	@Override
	public void fire(ICharacter c)
	{
		if(!c.useEnergy(700))
		{
			return;
		}

		if(use.containsKey(c.getPlayer()))
		{
			for(Entity i : use.get(c.getPlayer()))
			{
				i.remove();
			}

			use.remove(c.getPlayer());
		}

		use.put(c.getPlayer(), new GList<FallingBlock>());
		Location o = c.getPlayer().getLocation();
		Vector v = c.getPlayer().getLocation().getDirection();
		new S()
		{
			@Override
			public void run()
			{
				new Audio(new SoundShieldBuild()).setPitch(1f).play(o);

				int r = 19;
				double sr = Math.sqrt(r);
				for(double x = -r / 2; x < r / 2; x += 0.5)
				{
					for(double y = -r / 2; y < r / 2; y += 0.5)
					{
						for(double z = -r / 2; z < r / 2; z += 0.5)
						{
							double d = o.distanceSquared(o.clone().add(new Vector(x, y, z)));

							if(Math.abs(d - r) <= sr)
							{
								Location ll = o.clone().add(new Vector(x, y, z));
								Vector vv = VectorMath.direction(o, ll);

								if(vv.distance(v) < 0.55 && ll.getBlock().getType().equals(Material.AIR))
								{
									@SuppressWarnings("deprecation")
									FallingBlock fb = c.getPlayer().getWorld().spawnFallingBlock(o.clone().add(VectorMath.direction(o, ll).clone().multiply(3.54)), Material.THIN_GLASS, (byte) 9);
									fb.setGravity(false);
									fb.setInvulnerable(true);
									fb.setHurtEntities(false);
									fb.setDropItem(false);
									fb.setSilent(true);
									use.get(c.getPlayer()).add(fb);
									Glower g = GlowManager.create(fb, null);
									g.setColor(ChatColor.RED);
									g.setGlowing(true);
									Glower g2 = GlowManager.create(fb, c.getPlayer());
									g2.setColor(ChatColor.AQUA);
									g2.setGlowing(true);
									fb.setCustomName("--------:" + c.getPlayer().getEntityId());
									fb.setCustomNameVisible(false);

									new S(1900)
									{
										@Override
										public void run()
										{
											if(!fb.isDead())
											{
												fb.setGravity(true);
											}
										}
									};
								}
							}
						}
					}
				}

			}
		};
	}
}
