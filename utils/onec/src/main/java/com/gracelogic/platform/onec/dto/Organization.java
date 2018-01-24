package com.gracelogic.platform.onec.dto;


/**
 * Реквизиты компании контрагента
 */
public class Organization {
    private String inn;

    private String kpp;

    private String name;

    private String ogrn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", name='" + name + '\'' +
                ", ogrn='" + ogrn + '\'' +
                '}';
    }
}
