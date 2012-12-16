package com.svenkapudija.imagewall.orm;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.models.Location;
import com.svenkapudija.imagewall.models.Tag;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
	    Image.class,
	    Location.class,
	    Tag.class
	  };
	
	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
	
}
