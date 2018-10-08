package com.volmit.grush.character;

public class Property<T extends Number>
{
	private T base;
	private T current;
	
	public Property(T base)
	{
		this.base = base;
		this.current = base;
	}

	public T getBase()
	{
		return base;
	}

	public void setBase(T base)
	{
		this.base = base;
	}

	public T getCurrent()
	{
		return current;
	}

	public void setCurrent(T current)
	{
		this.current = current;
	}
}
