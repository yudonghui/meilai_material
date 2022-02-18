package live_library.wechat2.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import live_library.wechat2.bean.RMessage;

public class MessageDao {
    private DatabaseHelper dataHelper;

    public MessageDao(Context context){
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


    public void insert(List<RMessage> RMessages){
        try {
            Dao<RMessage, Long> messageDao = dataHelper.getDao(RMessage.class);
            DatabaseConnection conn = messageDao.startThreadConnection();
            Savepoint savePoint = conn.setSavePoint(null);
            for (RMessage rMessage : RMessages){
                messageDao.createOrUpdate(rMessage);
            }
            conn.commit(savePoint);
            messageDao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RMessage> getLocalMessageList() {
        try {
            Dao<RMessage, Long> messageDao = dataHelper.getDao(RMessage.class);
            return  messageDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RMessage queryLastestMessage() {
        try {
            Dao<RMessage, Long> messageDao = dataHelper.getDao(RMessage.class);
            QueryBuilder queryBuilder = messageDao.queryBuilder().orderBy("createTime",false);
            return  (RMessage)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RMessage queryOldestMessage() {
        try {
            Dao<RMessage, Long> messageDao = dataHelper.getDao(RMessage.class);
            QueryBuilder queryBuilder = messageDao.queryBuilder().orderBy("createTime",true);
            return  (RMessage)queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
