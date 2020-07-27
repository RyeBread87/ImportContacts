package com.example.importcontacts;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.importcontacts.daos.ContactDao;
import com.example.importcontacts.models.Contact;

// Room database class
@Database(entities = {Contact.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String TABLE_NAME_CONTACTS = "Contact";
    public abstract ContactDao contactDao();
}
