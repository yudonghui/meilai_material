package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.VoiceInfo;

public class VoiceinfoDao {
    private DatabaseHelper dataHelper;

    public VoiceinfoDao(Context context){
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


    public void insert(List<VoiceInfo> voiceInfos){
        try {
            Dao<VoiceInfo, Long> voiceInfoDao = dataHelper.getDao(VoiceInfo.class);
            DatabaseConnection conn = voiceInfoDao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (VoiceInfo voiceInfo : voiceInfos){
                voiceInfoDao.createOrUpdate(voiceInfo);
            }
            conn.commit(savePoint);
            voiceInfoDao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VoiceInfo queryLastestVoiceInfo() {
        try {
            Dao<VoiceInfo, Long> dao = dataHelper.getDao(VoiceInfo.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",false);
            return  (VoiceInfo)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public VoiceInfo queryOldestVoiceInfo() {
        try {
            Dao<VoiceInfo, Long> dao = dataHelper.getDao(VoiceInfo.class);
            QueryBuilder queryBuilder = dao.queryBuilder().orderBy("createTime",true);
            return  (VoiceInfo)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VoiceInfo> getLocalVoiceInfoList() {
        try {
            Dao<VoiceInfo, Long> voiceInfoDao = dataHelper.getDao(VoiceInfo.class);
            return  voiceInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
