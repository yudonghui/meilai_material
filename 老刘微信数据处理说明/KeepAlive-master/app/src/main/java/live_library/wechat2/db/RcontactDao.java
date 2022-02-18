package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.Rcontact;

public class RcontactDao {
    private DatabaseHelper dataHelper;

    public RcontactDao(Context context){
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


    public void insert(List<Rcontact> messages){
        try {
            Dao<Rcontact, Long> rcontactDao = dataHelper.getDao(Rcontact.class);
            DatabaseConnection conn = rcontactDao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (Rcontact message : messages){
                rcontactDao.createOrUpdate(message);
            }
            conn.commit(savePoint);
            rcontactDao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Rcontact> getLocalRcontactList() {
        try {
            Dao<Rcontact, Long> rcontactDao = dataHelper.getDao(Rcontact.class);
            return  rcontactDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
