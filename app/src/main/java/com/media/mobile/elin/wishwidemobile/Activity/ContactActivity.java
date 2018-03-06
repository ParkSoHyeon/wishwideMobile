package com.media.mobile.elin.wishwidemobile.Activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.media.mobile.elin.wishwidemobile.R;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "ContactActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "권한 허용");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Log.d(TAG, "권한 거부");
            }
        };

//        Cursor cursor = null;
//        Context context = this;
//        try {
//            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//            int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
//            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
//            cursor.moveToFirst();
//            do {
//                String idContact = cursor.getString(contactIdIdx);
//                String name = cursor.getString(nameIdx);
//                String phoneNumber = cursor.getString(phoneNumberIdx);
//
//                Log.d(TAG, idContact);
//                Log.d(TAG, name);
//                Log.d(TAG, phoneNumber);
//                //...
//            } while (cursor.moveToNext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }

        requestPermission(permissionListener, Manifest.permission.READ_CONTACTS);

    }

    private void requestPermission(PermissionListener permissionListener, String... permissions) {
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
//                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("거부하신 권한은 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(permissions)
                .check();
    }

}
