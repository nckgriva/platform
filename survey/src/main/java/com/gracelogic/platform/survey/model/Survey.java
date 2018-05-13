package com.gracelogic.platform.survey.model;

import javax.persistence.Entity;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY")
public class Survey extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "expires_dt", nullable = true)
    private Date expires;

    @Column(name = "show_progress", nullable = false)
    private boolean showProgress;

    @Column(name = "show_question_number", nullable = false)
    private boolean showQuestionNumber;

    @Column(name = "allow_return", nullable = false)
    private boolean allowReturn;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "conclusion", nullable = true)
    private String conclusion;

    @Column(name = "maximum_respondents", nullable = true)
    private Integer maximumRespondents;

    @Column(name = "time_limit", nullable = true)
    private Integer timeLimit;

    @Column(name = "authorization_required", nullable = true)
    private boolean isAuthorizationRequired;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isShowQuestionNumber() {
        return showQuestionNumber;
    }

    public void setShowQuestionNumber(boolean showQuestionNumber) {
        this.showQuestionNumber = showQuestionNumber;
    }

    public boolean isAllowReturn() {
        return allowReturn;
    }

    public void setAllowReturn(boolean allowReturn) {
        this.allowReturn = allowReturn;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Integer getMaximumRespondents() {
        return maximumRespondents;
    }

    public void setMaximumRespondents(Integer maximumRespondents) {
        this.maximumRespondents = maximumRespondents;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isAuthorizationRequired() {
        return isAuthorizationRequired;
    }

    public void setAuthorizationRequired(boolean authorizationRequired) {
        isAuthorizationRequired = authorizationRequired;
    }
}
