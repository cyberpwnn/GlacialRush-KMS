package com.volmit.grush.character;

import com.volmit.grush.Ticked;
import com.volmit.volume.bukkit.pawn.IPawn;

public interface ICharacter extends IPawn, Ticked
{
	public Property<Long> lastDamage();

	public Property<Long> lastEnergyUse();

	public Property<Integer> healthDelay();

	public Property<Integer> shieldDelay();

	public Property<Integer> energyDelay();

	public Property<Float> healthRegen();

	public Property<Float> shieldRegen();

	public Property<Float> energyRegen();

	public FloatProperty health();

	public FloatProperty armor();

	public FloatProperty energy();

	public FloatProperty shield();

	public FloatProperty resistance();

	public FloatProperty damage();

	public FloatProperty visibility();

	public FloatProperty speed();
}
