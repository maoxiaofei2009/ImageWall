package com.svenkapudija.imagewall.orm;

import java.sql.SQLException;

import android.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.models.Location;
import com.svenkapudija.imagewall.models.Tag;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "trambusDontTouchDatabase.db";
	// any time you make changes to your database objects, you may have to increase the database version
	public static final int DATABASE_VERSION = 15;

	private RuntimeExceptionDao<Image, Integer> imageDao = null;
	private RuntimeExceptionDao<Location, Integer> locationDao = null;
	private RuntimeExceptionDao<Tag, Integer> tagDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, 0);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Image.class);
			TableUtils.createTable(connectionSource, Location.class);
			TableUtils.createTable(connectionSource, Tag.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Image.class, true);
			TableUtils.dropTable(connectionSource, Location.class, true);
			TableUtils.dropTable(connectionSource, Tag.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Image, Integer> getImagesDao() {
		if (imageDao == null) {
			imageDao = getRuntimeExceptionDao(Image.class);
		}
		return imageDao;
	}
	
	public RuntimeExceptionDao<Location, Integer> getLocationsDao() {
		if (locationDao == null) {
			locationDao = getRuntimeExceptionDao(Location.class);
		}
		return locationDao;
	}
	
	public RuntimeExceptionDao<Tag, Integer> getTagsDao() {
		if (tagDao == null) {
			tagDao = getRuntimeExceptionDao(Tag.class);
		}
		return tagDao;
	}

}
