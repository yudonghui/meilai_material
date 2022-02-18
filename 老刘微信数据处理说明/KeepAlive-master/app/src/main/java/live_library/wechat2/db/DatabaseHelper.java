package live_library.wechat2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import live_library.wechat2.bean.RMessage;
import live_library.wechat2.bean.Rcontact;
import live_library.wechat2.bean.Snscomment;
import live_library.wechat2.bean.Snsinfo;
import live_library.wechat2.bean.UserInfo;
import live_library.wechat2.bean.UserInfo2;
import live_library.wechat2.bean.VoiceInfo;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public DatabaseHelper(Context context) {
        this(context,  Environment.getExternalStorageDirectory()+"/yes/wechat_reocrd.db",null,4);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {//建表
        try {
            TableUtils.createTable(connectionSource, RMessage.class);//此处创表
            TableUtils.createTable(connectionSource, Rcontact.class);//此处创表
            TableUtils.createTable(connectionSource, VoiceInfo.class);//此处创表
            TableUtils.createTable(connectionSource, Snscomment.class);//此处创表
            TableUtils.createTable(connectionSource, Snsinfo.class);//此处创表
//            TableUtils.createTable(connectionSource, UserInfo.class);//此处创表
            TableUtils.createTable(connectionSource, UserInfo2.class);//此处创表

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper","创建数据库失败:" + e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {//版本升级时的处理
        try {
            TableUtils.dropTable(connectionSource, RMessage.class, true);
            TableUtils.dropTable(connectionSource, Rcontact.class, true);
            TableUtils.dropTable(connectionSource, Snscomment.class, true);
            TableUtils.dropTable(connectionSource, Snsinfo.class, true);
            TableUtils.dropTable(connectionSource, UserInfo.class, true);
            TableUtils.dropTable(connectionSource, UserInfo2.class, true);
            TableUtils.dropTable(connectionSource, VoiceInfo.class, true);
            onCreate(sqLiteDatabase, connectionSource);
            Log.e("DatabaseHelper","更新数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper","更新数据库失败:"+e);
        }
    }
}
