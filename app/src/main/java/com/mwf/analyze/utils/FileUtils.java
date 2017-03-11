package com.mwf.analyze.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.mwf.analyze.bean.AnalyzeBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 文件管理工具
 */
public class FileUtils {

    /**
     * 读取txt文件的内容
     */
    public static String readTxtFile(String filePath) {
        try {
            String encoding = "GBK";
//            String encoding="UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String result = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (!TextUtils.isEmpty(lineTxt)) {
                        System.out.println(lineTxt);
                        result += lineTxt;
                    }
                }
                read.close();
                return result;
            } else {
                System.out.println("找不到指定的文件");
                return null;
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 导出csv文件
     */
    public static boolean saveTxt(String content, Context context, String path, String fileFullName) {
        //sd卡检测
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(context, "SD 卡不可用", Toast.LENGTH_SHORT).show();
            return false;
        }
        //检测文件夹是否存在
        File file = new File(path);
        file.exists();
        file.mkdirs();
        String p = path + File.separator + fileFullName;
        FileOutputStream outputStream = null;
        try {
            //创建文件，并写入内容
            outputStream = new FileOutputStream(new File(p));
            String msg = new String(content);
            outputStream.write(msg.getBytes("GBK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    /**
     * 导入csv文件
     */
    public static List<AnalyzeBean> readCSV(String csvPath) {
        File csvFile = new File(csvPath);
        List<AnalyzeBean> mList = new ArrayList<AnalyzeBean>();
        AnalyzeBean newBean;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "GBK"));
            String line = "";
            while ((line = br.readLine()) != null) {
                // 把一行数据分割成多个字段
                StringTokenizer st = new StringTokenizer(line, "|");
                while (st.hasMoreTokens()) {
                    String str = st.nextToken();
                    String[] result = str.split(",");
                    newBean = new AnalyzeBean();
                    newBean.setAmount(Integer.valueOf(result[0]));
                    newBean.setName(result[1]);
                    newBean.setLength(result[1].length());
                    mList.add(newBean);
//                    System.out.println("tokens_____" + str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mList;
    }
}
