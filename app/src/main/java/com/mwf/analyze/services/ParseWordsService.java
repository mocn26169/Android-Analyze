package com.mwf.analyze.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.utils.FileUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理词的服务类
 */
public class ParseWordsService extends IntentService {

    public final String TAG = this.getClass().getName();

    /**
     * 结束加载回调
     */
    private static ILoadWorsdFinish iLoadFinish;

    /**
     * 结束加载回调
     */
    private static ILoadWorsdUpdateUI iLoadUpdateUI;

    /**
     * 数据库访问
     */
    private AnalyzeDao dao;

    public ParseWordsService() {
        super("com.mwf.analyze.services.ParseWordsService");
    }

    public static void setLoadFinish(ILoadWorsdFinish iLoadWordsFinishInterface) {
        iLoadFinish = iLoadWordsFinishInterface;

    }

    public static void setLoadWorsdUpdateUI(ILoadWorsdUpdateUI updateUI) {
        iLoadUpdateUI = updateUI;
    }

    /**
     * 结束加载回调接口
     */
    public interface ILoadWorsdFinish {
        void LoadWorsdFinish();
    }

    /**
     * 更新界面回调接口
     */
    public interface ILoadWorsdUpdateUI {
        void setLoadWorsdUpdateUI(String text);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");
        dao = new AnalyzeDao(ParseWordsService.this);
        //获取文件地址
        String filePath = intent.getStringExtra("filePath");
        if (!TextUtils.isEmpty(filePath)) {
            String string = FileUtils.readTxtFile(filePath);
            //转单个字符
            char[] chars = new char[string.length()];
            chars = string.toCharArray();
            double total = chars.length;
            for (int i = 0; i < string.length(); i++) {
                String text = String.valueOf(chars[i]);
//                Log.i(TAG, text);
                dbSave(text);
                double progress = i / total * 100;
                DecimalFormat df = new DecimalFormat("#.##");

                iLoadUpdateUI.setLoadWorsdUpdateUI("进度：" + df.format(progress) + "");
            }
        }
    }

    public void onDestroy() {
        Log.i(TAG, " ParseTermService  destroy");
        iLoadFinish.LoadWorsdFinish();
        super.onDestroy();
    }

    /**
     * 数字判断
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 标点符号判断
     *
     * @param str
     * @return
     */
    public boolean isPunc(String str) {
        Pattern patPunc = Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]$·");
        Matcher matcher = patPunc.matcher(str);

        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存到数据库
     */
    private void dbSave(String word) {
        //空判断
        if (TextUtils.isEmpty(word)) {
            return;
        }
        //数字判断
        if (isNumeric(word)) {
            return;
        }
        //标点判断
        if (isPunc(word)) {
            return;
        }
        //保存到数据库
        dao.checkAndCreate(word);
    }

}
