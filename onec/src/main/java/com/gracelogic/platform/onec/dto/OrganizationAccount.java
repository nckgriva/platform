package com.gracelogic.platform.onec.dto;

/**
 * Рассчетный счет компании-контрагента
 */
public class OrganizationAccount {
    private String bankName;

    private String bik;

    private String corrAccount;

    private String raschAccount;

    private Organization organization = new Organization();

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getCorrAccount() {
        return corrAccount;
    }

    public void setCorrAccount(String corrAccount) {
        this.corrAccount = corrAccount;
    }

    public String getRaschAccount() {
        return raschAccount;
    }

    public void setRaschAccount(String raschAccount) {
        this.raschAccount = raschAccount;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "OrganizationAccount{" +
                "bankName='" + bankName + '\'' +
                ", bik='" + bik + '\'' +
                ", corrAccount='" + corrAccount + '\'' +
                ", raschAccount='" + raschAccount + '\'' +
                ", organization=" + organization +
                '}';
    }
}
