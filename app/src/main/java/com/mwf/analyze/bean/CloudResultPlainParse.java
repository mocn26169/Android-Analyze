package com.mwf.analyze.bean;


import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 请求的结果的数据解析
 */
public class CloudResultPlainParse {
    private ArrayList<String> statuses = new ArrayList<>();

    public ArrayList<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<String> statuses) {
        this.statuses = statuses;
    }

    /**
     *     取出每一个单独的词
     */
    public ArrayList<String> parse(String str) {
      if (!TextUtils.isEmpty(str)){
          //换行分割
          String [] arr_1 = str.split("\n");
          for (int i = 0; i <arr_1.length ; i++) {
              //空格分割
              String [] arr_2 = arr_1[i].split("\\s+");
              for (int j = 0; j <arr_2.length; j++) {
                  String result = arr_2[j];
                  statuses.add(result);
              }
          }
      }
        return statuses;
    }

}
