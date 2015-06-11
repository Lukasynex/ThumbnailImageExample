package pl.lukas.soi.thumbnailimageexample;

/**
 * Created by Lukasz Marczak on 2015-06-11.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import static android.provider.ContactsContract.Contacts;

public final class GetContactImage {

    private static final String[] PHOTO_ID_PROJECTION = new String[]{Contacts.PHOTO_ID};

    private static final String[] INFO_PROJECTION = new String[]{Contacts.PHOTO_ID, Contacts.DISPLAY_NAME,
            StructuredPostal.CITY, StructuredPostal.POSTCODE};

//    private static final String[] POSTAL_PROJECTION = new String[]{
//            StructuredPostal.DISPLAY_NAME,
//            StructuredPostal.CITY, StructuredPostal.POSTCODE,
//            StructuredPostal.FORMATTED_ADDRESS, StructuredPostal.STREET,
//    };


    private static final String[] PHOTO_BITMAP_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO};
    private static final String TAG = GetContactImage.class.getSimpleName();


    private final Context context;
    private final ContentResolver contentResolver;

    public GetContactImage(final Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();

    }

    public Bitmap getThumbnail(String name_or_surname_whatever) {
        String phoneNumber = getPhoneNumber(name_or_surname_whatever);
        Log.d(TAG, "phoneNumber");
        final Integer thumbnailId = fetchThumbnailId(phoneNumber);
        if (thumbnailId != null) {
            final Bitmap thumbnail = fetchThumbnail(thumbnailId);
            return thumbnail;
        }
        return null;
    }

    public void getAddressInfo(String query) {
        final Uri uri = Uri.withAppendedPath(
                ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                Uri.encode(query));

        final Cursor cursor = contentResolver.query(uri,
//                PHOTO_ID_PROJECTION,
                INFO_PROJECTION,
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

    }

    private Integer fetchThumbnailId(String phoneNumber) {
        Log.d(TAG, "fetchThumbnailId(" + phoneNumber + ")");
        final Uri uri = Uri.withAppendedPath(
                ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));//TODO: encode phone number to get image

        final Cursor cursor = contentResolver.query(uri,
                PHOTO_ID_PROJECTION,
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            Integer thumbnailId = null;
            if (cursor.moveToFirst()) {
                thumbnailId = cursor.getInt(cursor
                        .getColumnIndex(Contacts.PHOTO_ID));

                Log.d(TAG, "Value of thumbnailId: " + thumbnailId);
            }
            return thumbnailId;
        } finally {
            cursor.close();

        }

    }

    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,


    };
    private static final String[] POSTAL_PROJECTION = {
            StructuredPostal._ID,
            StructuredPostal.POSTCODE,
            StructuredPostal.STREET,
            StructuredPostal.COUNTRY,
            StructuredPostal.CITY,
            StructuredPostal.DISPLAY_NAME,

    };

    public String getPhoneNumber(String str) {

        String phoneNumber = "536141945";
        String id;
        String whichName = ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%" + str + "%' ";
        Log.d(TAG, "addrWhere: " + whichName);
        Cursor curr = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, whichName, null, null);
        if (curr.getCount() > 0) {
            while (curr.moveToNext()) {
                id = curr.getString(curr.getColumnIndex(ContactsContract.Contacts._ID));
                Log.d(TAG, "id: " + id);
                String name = curr.getString(curr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d(TAG, "name: " + name);
                if (Integer.parseInt(curr.getString(curr.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNumber = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d(TAG, "phoneNumber: " + phoneNumber);
                        if (phoneNumber.length() > 0)
                            return phoneNumber;
                    }
                    pCur.close();
                }
            }

        }
        return phoneNumber;
    }

    public String getAdressInfo(String name_or_surname_whatever) {
        String info = "Brak wyników";

        Log.d(TAG, "inside getAdressInfo(" + name_or_surname_whatever + ")");
        String addrWhere = StructuredPostal.DISPLAY_NAME + " LIKE '%"
                + name_or_surname_whatever + "%' AND " + StructuredPostal.MIMETYPE + " = ?";
        Log.d(TAG, "selection = " + addrWhere);

        String[] addrWhereParams = new String[]{StructuredPostal.CONTENT_ITEM_TYPE};
        Log.d(TAG, "selectionArgs = " + addrWhere);

        Cursor addrCur = contentResolver.query(StructuredPostal.CONTENT_URI,
                null, addrWhere, addrWhereParams, null/*, ContactsContract.Data.DISPLAY_NAME + " ASC"*/);
        int ind = 0;
        while (addrCur.moveToNext()) {
            String __id__ = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal._ID));
            String namex = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.DISPLAY_NAME));
            String street = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.STREET));
            String city = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.CITY));
            String state = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.REGION));
            String postalCode = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.POSTCODE));
            String country = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.COUNTRY));
            String type = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.TYPE));
            info = namex + ", " + street + ", " + city + ", " + state + ", " + postalCode + ", " + country;

            if (city.length() > 0)
                return info;

            Log.d(TAG, "_____" + ind + "_____");
            Log.d(TAG, "          ID: " + __id__);
            Log.d(TAG, "display name: " + namex);
            Log.d(TAG, "      street: " + street);
            Log.d(TAG, "        city: " + city);
            Log.d(TAG, "       state: " + state);
            Log.d(TAG, "  postalCode: " + postalCode);
            Log.d(TAG, "     country: " + country);
            Log.d(TAG, "        type: " + type);
            Log.d(TAG, "____________");
            ++ind;
        }
        addrCur.close();
        return info;
    }

    public void queryOnAdresses(String name_or_surname) {
        String postalSelection = StructuredPostal.DISPLAY_NAME + " LIKE '%?%' ";
        Log.d(TAG, "queryOnAdress(" + name_or_surname + ")");
        String[] postalSelectionArgs = new String[]{name_or_surname};

        Cursor addrCur = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                POSTAL_PROJECTION,
                postalSelection,
                postalSelectionArgs,
                StructuredPostal.DISPLAY_NAME + " ASC");
        while (addrCur.moveToNext()) {
            String street = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.STREET));
            String city = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.CITY));
            String postalCode = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.POSTCODE));
            String country = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.COUNTRY));
            String disp = addrCur.getString(
                    addrCur.getColumnIndex(StructuredPostal.DISPLAY_NAME));
            Log.d(TAG, "____________");
            Log.d(TAG, "      street: " + street);
            Log.d(TAG, "display name: " + disp);
            Log.d(TAG, "        city: " + city);
            Log.d(TAG, "  postalCode: " + postalCode);
            Log.d(TAG, "     country: " + country);
            Log.d(TAG, "____________");

        }
        addrCur.
                close();

    }

    final Bitmap fetchThumbnail(final int thumbnailId) {

        final Uri uri = ContentUris.withAppendedId(
                ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = contentResolver.query(uri,
                PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes,
                            0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        } finally {
            cursor.close();
        }

    }

}