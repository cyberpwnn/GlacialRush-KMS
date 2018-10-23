package com.volmit.grush.character;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.volmit.fulcrum.bukkit.A;
import com.volmit.fulcrum.bukkit.S;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.grush.character.ability.AbilityCharge;
import com.volmit.grush.character.ability.AbilityFireBall;
import com.volmit.grush.character.ability.AbilityGroundPound;
import com.volmit.grush.character.ability.AbilityHandler;
import com.volmit.grush.character.ability.AbilityProtectiveBarrier;
import com.volmit.grush.character.ability.AbilitySlot;
import com.volmit.grush.health.HealthPool;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.lang.json.JSONObject;

public class BaseCharacter implements ICharacter
{
	private Player p;
	private String type;
	private HealthPool pool;
	private AbilityHandler abilityHandler;
	private float energy;
	private float aEnergy;
	private float maxEnergy;
	private long last;
	private int u;

	public BaseCharacter(Player p, JSONObject j)
	{
		this(p, "<loading>");
		fromJSON(j);
		u = 0;
	}

	public BaseCharacter(Player p, String type)
	{
		pool = new HealthPool();
		this.type = type;
		this.p = p;
		maxEnergy = 900f;
		energy = 900f;
		last = TICK.tick;
	}

	@Override
	public void destroy()
	{
		HandlerList.unregisterAll(abilityHandler);
	}

	@Override
	public void tick(long delta)
	{
		tickEnergy();
		pool.setP(getPlayer());
		pool.tick(delta);

		if(abilityHandler == null)
		{
			new S()
			{
				@Override
				public void run()
				{
					abilityHandler = new AbilityHandler(BaseCharacter.this);
					Bukkit.getPluginManager().registerEvents(abilityHandler, VolumePlugin.vpi);

					new A()
					{
						@Override
						public void run()
						{
							initAbilities();
						}
					};
				}
			};
		}
	}

	private void initAbilities()
	{
		abilityHandler.getAbilities().clear();

		if(getCharacterType().contains("mage"))
		{
			abilityHandler.getAbilities().put(AbilitySlot.F, new AbilityFireBall());
		}

		else if(getCharacterType().contains("tank"))
		{
			abilityHandler.getAbilities().put(AbilitySlot.F, new AbilityProtectiveBarrier());
		}

		else if(getCharacterType().contains("gladiator"))
		{
			abilityHandler.getAbilities().put(AbilitySlot.F, new AbilityCharge());
		}

		else if(getCharacterType().contains("brute"))
		{
			abilityHandler.getAbilities().put(AbilitySlot.DOUBLE_JUMP, new AbilityGroundPound());
		}
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
		j.put("health-pool", getHealthPool().toJSON());
		j.put("type", getCharacterType());
	}

	@Override
	public void fromJSON(JSONObject j)
	{
		pool = new HealthPool(j.getJSONObject("health-pool"));
		type = j.getString("type");
	}

	@Override
	public Player getPlayer()
	{
		return p;
	}

	@Override
	public HealthPool getHealthPool()
	{
		return pool;
	}

	@Override
	public String getCharacterType()
	{
		return type;
	}

	@Override
	public float getEnergy()
	{
		return energy;
	}

	@Override
	public void setEnergy(float e)
	{
		energy = e;

		if(energy > maxEnergy)
		{
			energy = maxEnergy;
		}
	}

	@Override
	public boolean useEnergy(float energy)
	{
		if(getEnergy() > energy)
		{
			setEnergy(getEnergy() - energy);
			u = 10;
			last = TICK.tick;
			return true;
		}

		return false;
	}

	@Override
	public void tickEnergy()
	{
		if(u > 0)
		{
			u--;
		}

		if(TICK.tick - last > 50 && TICK.tick % 15 == 0)
		{
			setEnergy(getEnergy() + 50);
		}

		if(aEnergy > energy)
		{
			aEnergy -= (aEnergy - energy) / 6f;
		}

		if(aEnergy < energy)
		{
			aEnergy += (energy - aEnergy) / 6f;
		}

		if(u > 0)
		{
			u--;

			if(TICK.tick % 2 == 0)
			{
				getPlayer().setExp(0f);
			}

			else
			{
				getPlayer().setExp(aEnergy / maxEnergy);
			}
		}

		else
		{
			getPlayer().setExp(aEnergy / maxEnergy);
		}
	}
}
