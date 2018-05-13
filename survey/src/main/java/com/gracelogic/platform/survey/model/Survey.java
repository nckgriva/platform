package com.gracelogic.platform.survey.model;

import javax.persistence.Entity;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
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

    @Column(name = "is_show_progress", nullable = false)
    private Boolean showProgress;

    @Column(name = "is_show_question_number", nullable = false)
    private Boolean showQuestionNumber;

    @Column(name = "is_allow_return", nullable = false)
    private Boolean allowReturn;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "conclusion", nullable = true)
    private String conclusion;

    @Column(name = "maximum_respondents", nullable = true)
    private Integer maximumRespondents;

    @Column(name = "time_limit", nullable = true)
    private Long timeLimit;

    @Column(name = "is_authorization_required", nullable = false)
    private Boolean authorizationRequired;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OWNER_USER_ID", nullable = false)
    private User owner;

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

    public Boolean getShowProgress() {
        return showProgress;
    }

    public void setShowProgress(Boolean showProgress) {
        this.showProgress = showProgress;
    }

    public Boolean getShowQuestionNumber() {
        return showQuestionNumber;
    }

    public void setShowQuestionNumber(Boolean showQuestionNumber) {
        this.showQuestionNumber = showQuestionNumber;
    }

    public Boolean getAllowReturn() {
        return allowReturn;
    }

    public void setAllowReturn(Boolean allowReturn) {
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

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isAuthorizationRequired() {
        return authorizationRequired;
    }

    public Boolean getAuthorizationRequired() {
        return authorizationRequired;
    }

    public void setAuthorizationRequired(Boolean authorizationRequired) {
        this.authorizationRequired = authorizationRequired;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setAuthorizationRequired(boolean authorizationRequired) {
        this.authorizationRequired = authorizationRequired;


    }
}
