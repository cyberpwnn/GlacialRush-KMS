package com.volmit.grush.character;

import com.volmit.volume.bukkit.task.SR;
import com.volmit.volume.lang.collections.FinalInteger;

public class FloatProperty extends Property<Float>
{
	private float max;
	private float min;
	private boolean useMax;
	private boolean useMin;
	private Runnable minEvent;
	private Runnable maxEvent;
	private Runnable takeEvent;
	private Runnable gainEvent;
	private boolean gateMinEvent;
	private boolean gateMaxEvent;

	public FloatProperty(Float base)
	{
		super(base);
		this.max = 0f;
		this.min = 0f;
		this.useMax = false;
		this.useMin = false;
		this.gateMinEvent = false;
		this.gateMaxEvent = false;
	}

	public FloatProperty onMin(Runnable r)
	{
		this.minEvent = r;
		return this;
	}

	public FloatProperty onMax(Runnable r)
	{
		this.maxEvent = r;
		return this;
	}

	public FloatProperty onGain(Runnable r)
	{
		this.gainEvent = r;
		return this;
	}

	public FloatProperty onTake(Runnable r)
	{
		this.takeEvent = r;
		return this;
	}

	public float getMax()
	{
		return max;
	}

	public float getMin()
	{
		return min;
	}

	public boolean hasMax()
	{
		return useMax;
	}

	public boolean hasMin()
	{
		return useMin;
	}

	public FloatProperty setMax(float max)
	{
		this.max = max;
		useMax = true;
		return this;
	}

	public FloatProperty setMin(float min)
	{
		this.min = min;
		useMin = true;
		return this;
	}

	public FloatProperty add(float amt, int ticks)
	{
		FinalInteger i = new FinalInteger(0);

		new SR(0)
		{
			@Override
			public void run()
			{
				i.add(1);

				if(i.get() > ticks)
				{
					cancel();
				}

				add(amt);
			}
		};

		return this;
	}

	public FloatProperty sub(float amt, int ticks)
	{
		add(-amt, ticks);
		return this;
	}

	public FloatProperty add(float amt)
	{
		float current = getCurrent();
		setCurrent(getCurrent() + amt);
		validate();
		float nc = getCurrent();

		if(amt > 0 && gainEvent != null && nc > current)
		{
			gainEvent.run();
		}

		if(amt < 0 && takeEvent != null && nc < current)
		{
			takeEvent.run();
		}

		return this;
	}

	public FloatProperty sub(float amt)
	{
		add(-amt);
		return this;
	}

	public FloatProperty validate()
	{
		if(getCurrent() > max && useMax)
		{
			setCurrent(max);

			if(maxEvent != null && !gateMaxEvent)
			{
				maxEvent.run();
				gateMaxEvent = true;
			}
		}

		else
		{
			gateMaxEvent = false;
		}

		if(getCurrent() < min && useMin)
		{
			setCurrent(min);

			if(minEvent != null && !gateMinEvent)
			{
				minEvent.run();
				gateMinEvent = true;
			}
		}

		else
		{
			gateMinEvent = false;
		}

		return this;
	}
}
