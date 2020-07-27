package com.example.importcontacts;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.importcontacts.models.Contact;
import com.example.importcontacts.models.ContactImportModel;
import com.example.importcontacts.repositories.ContactRepository;
import com.hbb20.CountryCodePicker;
import java.util.regex.Pattern;

public class ContactEdit extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextEmail;
    private CountryCodePicker country_picker;
    private ContactRepository contactRepository;
    private Contact editContact;
    private Context context;
    private int editContactID;
    boolean isNewContact = false;
    boolean isImported = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // initialize basic variables
        context = getApplicationContext();
        ContactImportModel contactImportModel = (ContactImportModel) getIntent().getSerializableExtra("ContactImport");
        contactRepository = new ContactRepository(getApplication());

        // get ui components
        editTextName = findViewById(R.id.name_entry_edit_text);
        country_picker = findViewById(R.id.country_picker);
        editTextPhone = findViewById(R.id.phone_entry_edit_text);
        editTextEmail = findViewById(R.id.email_entry_edit_text);
        Button deleteButton = findViewById(R.id.delete_contact_button);

        // if contactImportModel exists, we're importing a contact
        if (contactImportModel != null) {
            isImported = true;
            String contactImportModelName = contactImportModel.getName();
            String contactImportModelPhone = contactImportModel.getPhone();
            String contactImportModelEmail = contactImportModel.getEmail();
            if (contactImportModelName != null) {
                editTextName.setText(contactImportModelName);
            }
            if (contactImportModelPhone != null) {
                editTextPhone.setText(contactImportModelPhone);
            }
            if (contactImportModelEmail != null) {
                editTextEmail.setText(contactImportModelEmail);
            }
        }

        editContactID = getIntent().getIntExtra("id", -100);

        if (editContactID == -100)                    // is this a new contact?
            isNewContact = true;
            country_picker.detectLocaleCountry(true);
        if (!isNewContact) {                        // if it's not a new contact, we retrieve the contact info to set up the
            fetchContactById(editContactID);        // edit contact activity & we enable the delete button
            deleteButton.setVisibility(View.VISIBLE);
        }

        deleteButton.setOnClickListener(v -> deleteContact(editContact));
    }

    // save the contact
    public void saveContact(View v) {
        final int contactID = editContactID;        // we'll only have an ID already if we're editing an existing contact

        final String contactName = editTextName.getText().toString().trim();
        if (!validateName(contactName)) {
            return;
        }

        final String countryCode = country_picker.getSelectedCountryNameCode();
        String phoneTemp = editTextPhone.getText().toString().trim();
        final String phoneNumber = phoneTemp.replaceAll("[^\\d.]", "");
        if (!validatePhoneNumber(phoneTemp, phoneNumber)) {
            return;
        }

        final String email = editTextEmail.getText().toString().trim();
        if (!validateEmail(email)) {
            return;
        }

        boolean goToContactSelect = false;

        // creating a new contact
        Contact contact = new Contact();
        contact.setName(contactName);
        contact.setPhone(phoneNumber);
        contact.setEmail(email);
        contact.setCountryCode(countryCode);

        if (isNewContact) {
            contactRepository.insert(contact);
        }
        else {
            contact.setId(contactID);
            contactRepository.update(contact);
            goToContactSelect = true;           // if we're editing an existing contact, we came from ContactSelect so we should return there
        }

        if (goToContactSelect) {
            Intent contactSelectIntent = new Intent(context, ContactSelect.class);
            contactSelectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(contactSelectIntent);
        }
        else {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivityIntent);
        }

        finish();
        Toast.makeText(context, getResources().getString(R.string.contact_saved_toast), Toast.LENGTH_LONG).show();
    }

    // if we're editing an existing contact, we fetch it by its ID and fill out fields with the contact's data
    @SuppressLint("StaticFieldLeak")
    private void fetchContactById(final int contactID) {

        new AsyncTask<Integer, Void, Contact>() {
            @Override
            protected Contact doInBackground(Integer... params) {
                return contactRepository.contactDao.fetchContactById(params[0]);
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected void onPostExecute(Contact contact) {
                super.onPostExecute(contact);
                editTextName.setText(contact.getName());
                editTextPhone.setText(contact.getPhone());
                editTextEmail.setText(contact.getEmail());

                editContact = contact;
                editContactID = contact.getId();
                country_picker.setCountryForNameCode(contact.getCountryCode());
            }
        }.execute(contactID);
    }

    // deletes a contact
    @SuppressLint("StaticFieldLeak")
    private void deleteContact(Contact contact)
    {
        contactRepository.delete(contact);
        Toast.makeText(context, getResources().getString(R.string.contact_deleted_toast), Toast.LENGTH_LONG).show();

        Intent contactSelectIntent = new Intent(context, ContactSelect.class);
        contactSelectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(contactSelectIntent);
        finish();
    }

    // validation code starts here

    // name validation

    public boolean validateName(String name) {
        if (isNameEmpty(name)) {
            editTextName.setError(getResources().getString(R.string.name_missing_toast));
            editTextName.requestFocus();
            return false;
        }
        if (isNameTooLong(name)) {
            editTextName.setError(getResources().getString(R.string.name_too_long_toast));
            editTextName.requestFocus();
            return false;
        }
        return true;
    }

    // static name validation helpers

    public static boolean isNameEmpty(String name) {
        return name.isEmpty();
    }

    public static boolean isNameTooLong(String name) { return (name.length() > 100);}

    // phone number validation

    public boolean validatePhoneNumber(String phoneTemp, String phoneNumber) {
        if (phoneNumberStartsWithPlus(phoneTemp)) {
            editTextPhone.setError(getResources().getString(R.string.included_country_code_toast));
            editTextPhone.requestFocus();
            return false;
        }

        if (phoneNumberIsEmpty(phoneNumber)) {
            editTextPhone.setError(getResources().getString(R.string.phone_number_missing_toast));
            editTextPhone.requestFocus();
            return false;
        }

        if ((!phoneNumberIsEmpty(phoneNumber)) && !isMobileValidLength(phoneNumber)) {
            editTextPhone.setError(getResources().getString(R.string.invalid_phone_number_length_toast));
            editTextPhone.requestFocus();
            return false;
        }
        return true;
    }

    // static phone number validation helpers

    public static boolean phoneNumberStartsWithPlus(String phoneNumber) { return phoneNumber.startsWith("+"); }

    public static boolean phoneNumberIsEmpty(String phoneNumber) { return phoneNumber.isEmpty(); }

    public static boolean isMobileValidLength(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() >= 4 && phone.length() <= 12;
        }
        return false;
    }

    // email validation

    private boolean validateEmail(String email) {
        if (isEmailEmpty(email)) {
            editTextEmail.setError(getResources().getString(R.string.email_address_missing_toast));
            editTextEmail.requestFocus();
            return false;
        }
        if ((!isEmailEmpty(email)) && (!isValidMail(email))) {
            editTextEmail.setError(getResources().getString(R.string.invalid_email_address_toast));
            editTextEmail.requestFocus();
            return false;
        }
        return true;
    }

    // static email validation helpers

    public static boolean isEmailEmpty(String email) {
        return email.isEmpty();
    }

    public static boolean isValidMail(String email) {
        String EMAIL_STRING = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
    }

}
