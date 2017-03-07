package com.mwf.analyze.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;


public class AnalyzeDao {
    public final String TAG = this.getClass().getName();

    private Context context;
    private Dao<AnalyzeBean, Integer> daoOpe;
    private DatabaseHelper helper;

    public AnalyzeDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            daoOpe = helper.getDao(AnalyzeBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加
     */
    private void add(AnalyzeBean user) {
        /*//事务操作
        TransactionManager.callInTransaction(helper.getConnectionSource(),
				new Callable<Void>()
				{

					@Override
					public Void call() throws Exception
					{
						return null;
					}
				});*/
        try {
            daoOpe.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过笔记本类型查询所有数据
     */
    public void checkAndCreate(String word) {
        AnalyzeBean bean;
        try {
            bean = daoOpe.queryBuilder().orderBy("id", false).where().eq("name", word).queryForFirst();
            if (bean == null) {
                bean = new AnalyzeBean();
                bean.setAmount(1);
                bean.setName(word);
                //没有同样的直接新建一个
                daoOpe.create(bean);
//                Log.i(TAG, "没有同样的直接新建一个" + word);
            } else {
                //有同样的数量加1
                int amount = bean.getAmount() + 1;
                bean.setAmount(amount);
                daoOpe.update(bean);
//                Log.i(TAG, "有同样的数量加1"+word);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * 查询所有数据
     */
    public List<AnalyzeBean> queryAll() {
        List<AnalyzeBean> list = null;
        try {
            list = daoOpe.queryBuilder().orderBy("amount", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 查询所有数据
     */
    public List<AnalyzeBean> queryAll(int limit) {
        List<AnalyzeBean> list = null;
        try {
            list = daoOpe.queryBuilder().orderBy("amount", true).limit(limit).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 删除所有数据
     */
    public void deletedAll() {
        try {
            daoOpe.delete(queryAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AnalyzeBean get() {
        try {
            if (daoOpe.queryForAll() != null && daoOpe.queryForAll().size() > 0) {
                return daoOpe.queryForAll().get(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
