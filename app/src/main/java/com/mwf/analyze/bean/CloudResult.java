package com.mwf.analyze.bean;

/**
 * 请求数据模型
 */
public class CloudResult {
    private String id;
    private String cont;
    private String pos;
    private String ne;
    private String parent;
    private String relate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getNe() {
        return ne;
    }

    public void setNe(String ne) {
        this.ne = ne;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRelate() {
        return relate;
    }

    public void setRelate(String relate) {
        this.relate = relate;
    }

    @Override
    public String toString() {
        return "CloudResult{" +
                "id='" + id + '\'' +
                ", cont='" + cont + '\'' +
                ", pos='" + pos + '\'' +
                ", ne='" + ne + '\'' +
                ", parent='" + parent + '\'' +
                ", relate='" + relate + '\'' +
                '}';
    }
}
