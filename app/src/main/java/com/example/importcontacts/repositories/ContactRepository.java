package com.example.importcontacts.repositories;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.importcontacts.DatabaseClient;
import com.example.importcontacts.AppDatabase;
import com.example.importcontacts.daos.ContactDao;
import com.example.importcontacts.models.Contact;
import java.util.List;

// repository class for performing CRUD operations on contacts (& other utilities for contacts)
public class ContactRepository {

    public ContactDao contactDao;

    public ContactRepository(Application application) {
        AppDatabase dataBase = DatabaseClient.getInstance(application).getAppDatabase();
        contactDao = dataBase.contactDao();
    }

    public void insert(Contact contact) {
        new InsertContactAsyncTask(contactDao).execute(contact);
    }

    public void update(Contact contact) {
        new ContactRepository.UpdateContactAsyncTask(contactDao).execute(contact);
    }

    public Integer delete(Contact contact) {
        new ContactRepository.DeleteContactAsyncTask(contactDao).execute(contact);
        return contact.getId();
    }

    public LiveData<List<Contact>> getContactsLiveData() {
        return contactDao.getAllContactsLiveData();
    }

    public static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        ContactDao contactDao;

        InsertContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        public Void doInBackground(Contact... contacts) {
            contactDao.insert(contacts[0]);
            return null;
        }
    }

    public static class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        ContactDao contactDao;

        UpdateContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        public Void doInBackground(Contact... contacts) {
            contactDao.update(contacts[0]);
            return null;
        }
    }

    public static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Integer> {

        ContactDao contactDao;

        DeleteContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        public Integer doInBackground(Contact... contacts) {
            contactDao.delete(contacts[0]);
            return null;
        }
    }
}