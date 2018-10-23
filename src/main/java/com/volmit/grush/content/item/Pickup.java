package com.volmit.grush.content.item;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.S;
import com.volmit.fulcrum.custom.CustomItem;
import com.volmit.grush.character.ICharacter;
import com.volmit.grush.health.HealthLayer;
import com.volmit.grush.service.GameSVC;
import com.volmit.volume.bukkit.U;

public abstract class Pickup extends CustomItem
{
	public Pickup(String id)
	{
		super(id);
	}

	public abstract float getMax();

	@Override
	public boolean onPickedUp(Player p, Item item)
	{
		HealthLayer h = getHealthLayer();
		ICharacter c = U.getService(GameSVC.class).getCharacter(p);

		boolean delete = false;
		boolean found = false;
		for(HealthLayer i : c.getHealthPool().getLayers())
		{
			if(i.getId().equals(h.getId()))
			{
				found = true;

				if(i.getCurrent() + h.getCurrent() > getMax())
				{
					if(i.getCurrent() < getMax())
					{
						i.setCurrent(getMax());
						delete = true;
					}
				}

				else
				{
					i.setCurrent(i.getCurrent() + h.getCurrent());
					delete = true;
				}
			}
		}

		if(!found)
		{
			c.getHealthPool().addLayer(h);
			delete = true;
		}

		if(delete)
		{
			new S()
			{
				@Override
				public void run()
				{
					item.remove();
				}
			};
		}

		return true;
	}

	public abstract HealthLayer getHealthLayer();
}
