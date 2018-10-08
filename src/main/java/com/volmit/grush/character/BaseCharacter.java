package com.volmit.grush.character;

import com.volmit.fulcrum.bukkit.TICK;

public class BaseCharacter implements ICharacter
{
	private final FloatProperty health;
	private final FloatProperty armor;
	private final FloatProperty energy;
	private final FloatProperty shield;
	private final FloatProperty resistance;
	private final FloatProperty damage;
	private final FloatProperty visibility;
	private final FloatProperty speed;
	private final Property<Long> lastDamage;
	private final Property<Long> lastEnergyUse;
	private final Property<Integer> healthDelay;
	private final Property<Integer> shieldDelay;
	private final Property<Integer> energyDelay;
	private final Property<Float> healthRegen;
	private final Property<Float> shieldRegen;
	private final Property<Float> energyRegen;

	public BaseCharacter()
	{
		//@builder
		healthDelay = new Property<Integer>(250);
		shieldDelay = new Property<Integer>(100);
		energyDelay = new Property<Integer>(65);
		healthRegen = new Property<Float>(0.25f);
		shieldRegen = new Property<Float>(2.5f);
		energyRegen = new Property<Float>(3.25f);

		health = new FloatProperty(1000f)
				.setMax(1000f)
				.setMin(0f)
				.onMin(() -> healthColapsed())
				.onMax(() -> healthFull())
				.onTake(() -> lastDamage().setCurrent(TICK.tick));
		armor = new FloatProperty(0f)
				.setMin(0f)
				.onMin(() -> armorColapsed())
				.onTake(() -> lastDamage().setCurrent(TICK.tick));
		shield = new FloatProperty(0f)
				.setMin(0f)
				.setMax(0f)
				.onMin(() -> shieldColapsed())
				.onMax(() -> shieldFull())
				.onTake(() -> lastDamage().setCurrent(TICK.tick));
		energy = new FloatProperty(1000f)
				.setMin(0f)
				.setMax(0f)
				.onMin(() -> energyDepleted())
				.onMax(() -> energyFull())
				.onTake(() -> lastEnergyUse().setCurrent(TICK.tick));
		resistance = new FloatProperty(1f)
				.setMin(0f)
				.setMax(2f);
		damage = new FloatProperty(1f)
				.setMin(0f)
				.setMax(2f);
		speed = new FloatProperty(1f)
				.setMin(0f)
				.setMax(3f);
		visibility = new FloatProperty(1f)
				.setMin(0f)
				.setMax(5f);
		lastDamage = new Property<Long>(TICK.tick);
		lastEnergyUse = new Property<Long>(TICK.tick);
		//@done
	}

	@Override
	public void tick()
	{
		if(TICK.tick - lastDamage().getCurrent() > shieldDelay().getCurrent())
		{
			shield().add(shieldRegen().getCurrent() / 20f, 20);
		}

		if(TICK.tick - lastDamage().getCurrent() > healthDelay().getCurrent())
		{
			health().add(healthRegen().getCurrent() / 20f, 20);
		}

		if(TICK.tick - lastEnergyUse().getCurrent() > energyDelay().getCurrent())
		{
			energy().add(energyRegen().getCurrent() / 20f, 20);
		}
	}

	@Override
	public int getTickRate()
	{
		return 0;
	}

	@Override
	public FloatProperty armor()
	{
		return armor;
	}

	@Override
	public Property<Long> lastDamage()
	{
		return lastDamage;
	}

	@Override
	public Property<Long> lastEnergyUse()
	{
		return lastEnergyUse;
	}

	@Override
	public FloatProperty health()
	{
		return health;
	}

	@Override
	public FloatProperty energy()
	{
		return energy;
	}

	@Override
	public FloatProperty shield()
	{
		return shield;
	}

	@Override
	public FloatProperty resistance()
	{
		return resistance;
	}

	@Override
	public FloatProperty damage()
	{
		return damage;
	}

	@Override
	public FloatProperty visibility()
	{
		return visibility;
	}

	@Override
	public FloatProperty speed()
	{
		return speed;
	}

	@Override
	public Property<Integer> healthDelay()
	{
		return healthDelay;
	}

	@Override
	public Property<Integer> shieldDelay()
	{
		return shieldDelay;
	}

	@Override
	public Property<Float> healthRegen()
	{
		return healthRegen;
	}

	@Override
	public Property<Float> shieldRegen()
	{
		return shieldRegen;
	}

	@Override
	public Property<Integer> energyDelay()
	{
		return energyDelay;
	}

	@Override
	public Property<Float> energyRegen()
	{
		return energyRegen;
	}

	protected void healthFull()
	{

	}

	protected void shieldFull()
	{

	}

	protected void energyFull()
	{

	}

	protected void energyDepleted()
	{

	}

	protected void shieldColapsed()
	{

	}

	protected void armorColapsed()
	{

	}

	protected void healthColapsed()
	{

	}
}
