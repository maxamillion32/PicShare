package picshare.mk.com.picshare.Utils;

/**
 * Created by Salim on 05/04/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    public SharedPreferences Pref;
    public Editor editor;
    Context Context;
    private static final String PREF_NAME = "Users";
    public static final String KEY_NAME = "name";
    public static final String KEY_PRENAME = "prename";
    public static final String KEY_ID = "id";
    public static final String KEY_PHOTOURL = "photo";
    public static final String KEY_EMAIL = "email";

    public SessionManager(Context cont) {
        Context = cont;
        Pref = Context.getSharedPreferences(PREF_NAME, 0);
        editor = Pref.edit();
    }

    public void createLoginSession(String id, String email, String name, String prename, String photoUrl) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PRENAME, prename);
        editor.putString(KEY_PHOTOURL, photoUrl);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID, id);
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

    }

    public int getId() {
        int id = Integer.parseInt(Pref.getString(KEY_ID, "0"));
        return id;
    }

    public String getName() {
        return Pref.getString(KEY_NAME, "");
    }

    public String getPrename() {
        return Pref.getString(KEY_PRENAME, "");
    }

    public String getPhotourl() {
        return Pref.getString(KEY_PHOTOURL, "");
    }

    public String getEmail() {
        return Pref.getString(KEY_EMAIL, "");
    }
}
