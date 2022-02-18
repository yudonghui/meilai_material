package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.Snscomment;

public class SnsCommentDao {
    private DatabaseHelper dataHelper;

    public SnsCommentDao(Context context){
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


    public void insert(List<Snscomment> snscomments){
        try {
            Dao<Snscomment, Long> dao = dataHelper.getDao(Snscomment.class);
            DatabaseConnection conn = dao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (Snscomment snscomment : snscomments){
                dao.createOrUpdate(snscomment);
            }
            conn.commit(savePoint);
            dao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Snscomment queryLastestSnscomment() {
        try {
            Dao<Snscomment, Long> dao = dataHelper.getDao(Snscomment.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",false);
            return  (Snscomment)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Snscomment queryOldestSnscomment() {
        try {
            Dao<Snscomment, Long> dao = dataHelper.getDao(Snscomment.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",true);
            return  (Snscomment)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Snscomment> getLocalVoiceInfoList() {
        try {
            Dao<Snscomment, Long> voiceInfoDao = dataHelper.getDao(Snscomment.class);
            return  voiceInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
