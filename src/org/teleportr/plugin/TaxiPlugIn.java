package org.teleportr.plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.teleportr.R;
import org.teleportr.model.Place;
import org.teleportr.model.Ride;

public class TaxiPlugIn implements IPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {

        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        Ride r = new Ride();
      
     // taxi
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+5*60000);
        r.arr = new Date(System.currentTimeMillis()+(5+22)*60000);
        r.mode = Ride.MODE_TAXI;
        r.price = 2300;
        r.fun = 1;
        r.eco = 1;
        r.fast = 4;
        r.social = 1;
        r.green = 1;
        rides.add(r);
        rides.add(r);
        
        return rides;
    }

	@Override
	public void share(Ride offer) {
		// TODO Auto-generated method stub
	}

}
