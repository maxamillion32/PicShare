package picshare.mk.com.picshare.Utils;

/**
 * Created by Salim on 05/04/16.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    public final static String TABLE_USER = "Users";

    public DataBaseOpenHelper(Context context, String name,
                              CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String req = "CREATE TABLE "
                + TABLE_USER
                + " (_id INTEGER PRIMARY KEY, email TEXT, name TEXT, prename TEXT, photo TEXT);)";
        db.execSQL(req);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_USER + ";");
        onCreate(db);
    }

    public Boolean verifyTable(SQLiteDatabase db) {
        String req = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_USER + "'";
        Cursor cursor = db.rawQuery(req, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}
