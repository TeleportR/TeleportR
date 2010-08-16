/**
 * Copyright (c) 2010: andlabs gbr, teleportr.org All rights reserved.
 *	
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version <http://www.gnu.org/licenses/>
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
**/

package org.teleportr.model;

import java.net.URLDecoder;

import org.teleportr.R;
import org.teleportr.Teleporter;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class Place implements BaseColumns {

	public int lat;
    public int lon;
    public int icon;
    public String name;
	public String city;
    public String address;

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + lat;
        result = prime * result + lon;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + icon;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Place other = (Place) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (lat != other.lat)
            return false;
        if (lon != other.lon)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (icon != other.icon)
            return false;
        return true;
    }





	public static final Uri CONTENT_URI = Uri.parse("content://org.teleportr/places");

	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String ICON = "ICON";
	public static final String NAME = "name";
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String[] PROJECTION =
					new String[] { LAT, LON, ICON, NAME, CITY, ADDRESS };
    

	public static Place find(Uri uri, Context ctx) {
    	
		Place p = new Place();
    	
    	if (uri.getScheme().equals("content")) {
    		
    		Cursor c = ctx.getContentResolver().query(uri, PROJECTION, null, null, null);
    		if (c != null) {
    			if (c.getCount() > 0) {
    				c.moveToFirst();
    				p.lat = c.getInt(0);
    				p.lon = c.getInt(1);
    				p.icon = c.getInt(2);
    				p.name = c.getString(3);
    				p.city = c.getString(4);
    				p.address = c.getString(5);
    			}
    			c.close();
    		} else Log.d(Teleporter.TAG, "place not found!");
    		
    	} else if (uri.getScheme().equals("geo")) {
    		String query = URLDecoder.decode(uri.toString().substring(10));
    		
            String[] q = query.split(",");
            if (Character.isDigit(query.charAt(0))) {
                p.address = q[0].substring(q[0].indexOf(" ")+1) +" "+ q[0].substring(0, q[0].indexOf(" "))+", "+q[1].split(" ")[1];
            } else {
                p.address = q[0]+", "+ q[1].split(" ")[2];
            }
            p.name = p.address;
    	}
    	
    	return p;
	}
    
	public static Place find(String query, Context ctx) {
		Place p = new Place();
		String[] split = query.split(",");
		if (split.length == 2) 
			p.city = split[1].trim();
		p.name = split[0];
		p.address = split[0];
		p.icon = R.drawable.a_street;
		return p;
	}

}
