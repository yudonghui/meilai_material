package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.Snsinfo;

public class SnsinfoDao {
    private DatabaseHelper dataHelper;

    public SnsinfoDao(Context context){
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


    public void insert(List<Snsinfo> snsinfos){
        try {
            Dao<Snsinfo, Long> dao = dataHelper.getDao(Snsinfo.class);
            DatabaseConnection conn = dao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (Snsinfo snsinfo : snsinfos){
                dao.createOrUpdate(snsinfo);
            }
            conn.commit(savePoint);
            dao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Snsinfo queryLastestSnsinfo() {
        try {
            Dao<Snsinfo, Long> dao = dataHelper.getDao(Snsinfo.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",false);
            return  (Snsinfo)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Snsinfo queryOldestSnsinfo() {
        try {
            Dao<Snsinfo, Long> dao = dataHelper.getDao(Snsinfo.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",true);
            return  (Snsinfo)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Snsinfo> getLocalSnsinfoList() {
        try {
            Dao<Snsinfo, Long> voiceInfoDao = dataHelper.getDao(Snsinfo.class);
            return  voiceInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
