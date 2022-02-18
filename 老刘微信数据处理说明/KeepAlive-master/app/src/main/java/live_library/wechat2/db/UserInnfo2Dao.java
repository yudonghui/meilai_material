package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.UserInfo2;

public class UserInnfo2Dao {
    private DatabaseHelper dataHelper;

    public UserInnfo2Dao(Context context){
        if (dataHelper == null) {
            dataHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }

    }

    public void releaseHelper(){
        if (dataHelper != null) {
            OpenHelperManager.releaseHelper();
            dataHelper = null;
        }
    }


    public void insert(List<UserInfo2> userInfo2s){
        try {
            Dao<UserInfo2, Long> userInfo2Dao = dataHelper.getDao(UserInfo2.class);
            DatabaseConnection conn = userInfo2Dao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (UserInfo2 voiceInfo : userInfo2s){
                userInfo2Dao.createOrUpdate(voiceInfo);
            }
            conn.commit(savePoint);
            userInfo2Dao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserInfo2> getLocalUserInfo2List() {
        try {
            Dao<UserInfo2, Long> userInfo2Dao = dataHelper.getDao(UserInfo2.class);
            return  userInfo2Dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
