package com.mwf.analyze.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 数据库实体类
 */
@DatabaseTable(tableName = "tb_analyze")
public class AnalyzeBean {
    public AnalyzeBean() {
    }

    @DatabaseField(generatedId = true)
    private int id;

    /**
     * 名称
     */
    @DatabaseField(columnName = "name")
    private String name;

    /**
     * 总数
     */
    @DatabaseField(columnName = "amount")
    private int amount;

    /**
     * 长度
     */
    @DatabaseField(columnName = "length")
    private int length;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


}
