package pl.lukas.soi.thumbnailimageexample;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.net.URI;

/**
 * Created by Lukasz on 2015-06-02.
 */
public class PhoneContactsManager {
    public static final String TAG = PhoneContactsManager.class.getSimpleName();
    private static PhoneContactsManager instance = null;
    private Activity activity;

    private PhoneContactsManager() {}
    private PhoneContactsManager(Activity a) {
        this.activity = a;
    }

    public static PhoneContactsManager getInstance(Activity activ) {
        if (instance == null)
            instance = new PhoneContactsManager(activ);
        return instance;
    }

    public static int index = 0;

    public String getThumbnailPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        // This method was deprecated in API level 11
        // Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(activity, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        cursor.moveToFirst();
        long imageId = cursor.getLong(column_index);
        //cursor.close();
        String result = "";
        cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(activity.getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }
        return result;
    }

    public Bitmap getThumbnailBitmap(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        // This method was deprecated in API level 11
        // Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(activity, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        cursor.moveToFirst();
        long imageId = cursor.getLong(column_index);
        //cursor.close();

        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                activity.getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null);

        return bitmap;
    }

    public Bitmap getBitmap(Activity activity) {
        index++;
        ContentResolver cr = activity.getContentResolver();
        Cursor curr = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if (curr.getCount() > 0) {

//            while (curr.moveToNext()) {
            curr.moveToNext();
            String id = curr.getString(curr.getColumnIndex(ContactsContract.Contacts._ID));
            Log.d(TAG, "id: " + id);
            String name = curr.getString(curr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d(TAG, "name: " + name);
            String photo = curr.getString(curr.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            Uri photoUri = Uri.parse(photo);

            return getThumbnailBitmap(photoUri);

//            }
        }
        return null;
    }

    public Uri photoUri = null;

    public void readContacts(Activity activity, String query) {
        Log.d(TAG, "reading Contacts...");

        int j = 0;
        ContentResolver cr = activity.getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {


                Log.d(TAG, "basic info:");
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String content_item_type = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.CONTENT_ITEM_TYPE));
                String display_name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String name_raw_contact_id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                String display_name_primary = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                String display_name_alternative = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE));
                String photo_file_id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_FILE_ID));
                String photo_id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                String photo_uri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String photo_thumbnail_uri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Log.d(TAG, "--------------------------" );
                Log.d(TAG, "                     _ID: " + id);
                Log.d(TAG, "       content_item_type: " + content_item_type);
                Log.d(TAG, "            display_name: " + display_name);
                Log.d(TAG, "     name_raw_contact_id: " + name_raw_contact_id);
                Log.d(TAG, "    display_name_primary: " + display_name_primary);
                Log.d(TAG, "display_name_alternative: " + display_name_alternative);
                Log.d(TAG, "           photo_file_id: " + photo_file_id);
                Log.d(TAG, "                photo_id: " + photo_id);
                Log.d(TAG, "           photo_file_id: " + photo_file_id);
                Log.d(TAG, "               photo_uri: " + photo_uri);
                Log.d(TAG, "     photo_thumbnail_uri: " + photo_thumbnail_uri);
                Log.d(TAG, "--------------------------" );


//                if (Integer.getInteger(id) == 479) {
//                    Uri uri = Uri.parse(photo);
//                    Log.d(TAG, "photo updated!!! " + photo);
//                    photoUri = uri;
//                    Log.d(TAG, "id: " + id);
//                }

                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Log.d(TAG, "name: " + name);
                Log.d(TAG, "jakisindex1: " + j);

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    //System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        System.out.println("phone" + phone);
                        Log.d(TAG, "phone: " + phone);
                    }
                    pCur.close();


                    // get email and type

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        Log.d(TAG, "email: " + email);
                        Log.d(TAG, "Email Type : " + emailType);

                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        Log.d(TAG, "Note: " + note);
                    }
                    noteCur.close();

                    //Get Postal Address....

                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    Log.d(TAG, "addrWhere: " + addrWhere);
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    int kk = 0;
                    while (addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                        Log.d(TAG, "jakisindex2: " + kk);
                        Log.d(TAG, "poBox: " + poBox);
                        Log.d(TAG, "street: " + street);
                        Log.d(TAG, "city: " + city);
                        Log.d(TAG, "state: " + state);
                        Log.d(TAG, "postalCode: " + postalCode);
                        Log.d(TAG, "country: " + country);
                        Log.d(TAG, "type: " + type);
                        Log.d(TAG, "-----");
                        Log.d(TAG, "-----");


                        // Do something with these....
                        ++kk;
                    }
                    addrCur.close();

                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst()) {
                        String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType;
                        imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                        Log.d(TAG, "imName: " + imName);
                        Log.d(TAG, "imType: " + imType);
                        Log.d(TAG, "-----");
                        Log.d(TAG, "-----");


                    }
                    imCur.close();

                    // Get Organizations.........

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

                        Log.d(TAG, "orgName: " + orgName);
                        Log.d(TAG, "title: " + title);
                        Log.d(TAG, "-----");
                        Log.d(TAG, "-----");


                    }
                    orgCur.close();
                }

                ++j;

            }

        }
    }


}
