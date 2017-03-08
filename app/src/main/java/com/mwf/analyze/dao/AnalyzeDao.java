package com.mwf.analyze.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作类
 */
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
        try {
            daoOpe.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查已经存在字段
     * 已经存在数量加一
     * 没有存在新增一个
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
            } else {
                //有同样的数量加1并更新
                int amount = bean.getAmount() + 1;
                bean.setAmount(amount);
                daoOpe.update(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否已经存在
     * 有则数量想加
     * 没有则新增一个
     */
    public void merge(AnalyzeBean mBean) {
        AnalyzeBean sqlBean = null;
        try {
            sqlBean = daoOpe.queryBuilder().orderBy("id", false).where().eq("name", mBean.getName()).queryForFirst();
            if (sqlBean == null) {
                //新建一个
                daoOpe.create(mBean);
            } else {
                //数量相加
                sqlBean.setAmount((sqlBean.getAmount() + mBean.getAmount()));
                daoOpe.update(sqlBean);
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
            //根据数量降序查询
            list = daoOpe.queryBuilder().orderBy("amount", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据指定数量查询所有数据
     * @param limit   限制数量
     * @param orderBy false降序  true升序
     * @return
     */
    public List<AnalyzeBean> queryAll(int limit, boolean orderBy) {
        List<AnalyzeBean> list = null;
        try {
            list = daoOpe.queryBuilder().orderBy("amount", orderBy).limit(limit).query();
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
            List<AnalyzeBean> list = queryAll();
            Log.e(TAG, "删除总数: " + list.size());

            //遍历逐个删除
            for (int i = 0; i < list.size(); i++) {
                daoOpe.delete(list.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
