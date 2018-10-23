package com.volmit.grush.character.ability;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.volmit.grush.character.ICharacter;
import com.volmit.volume.lang.collections.GMap;

public class AbilityHandler implements Listener
{
	private final ICharacter p;
	private final GMap<AbilitySlot, IAbility> abilities;

	public AbilityHandler(ICharacter p)
	{
		this.p = p;
		abilities = new GMap<AbilitySlot, IAbility>();
		p.getPlayer().setAllowFlight(true);
	}

	public Player getP()
	{
		return p.getPlayer();
	}

	public GMap<AbilitySlot, IAbility> getAbilities()
	{
		return abilities;
	}

	public void fireAbility(AbilitySlot s)
	{
		if(abilities.containsKey(s))
		{
			abilities.get(s).fire(p);
		}
	}

	@EventHandler
	public void onFlightAttempt(PlayerSwapHandItemsEvent e)
	{
		if(e.getPlayer().equals(p.getPlayer()))
		{
			e.setCancelled(true);
			fireAbility(AbilitySlot.F);
		}
	}

	@EventHandler
	public void onFlightAttempt(PlayerToggleFlightEvent e)
	{
		if(e.isFlying() && e.getPlayer().getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
			fireAbility(AbilitySlot.DOUBLE_JUMP);
		}
	}
}
