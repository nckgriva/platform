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

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EXPIRATION_DT", nullable = true)
    private Date expirationDate;

    @Column(name = "IS_SHOW_PROGRESS", nullable = false)
    private Boolean showProgress;

    @Column(name = "IS_SHOW_QUESTION_NUMBER", nullable = false)
    private Boolean showQuestionNumber;

    @Column(name = "IS_ALLOW_RETURN", nullable = false)
    private Boolean allowReturn;

    @Column(name = "INTRODUCTION", nullable = true)
    private String introduction;

    @Column(name = "CONCLUSION", nullable = true)
    private String conclusion;

    @Column(name = "LINK", nullable = true)
    private String link;

    @Column(name = "MAX_RESPONDENTS", nullable = true)
    private Integer maxRespondents;

    @Column(name = "TIME_LIMIT", nullable = true)
    private Long timeLimit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARTICIPATION_TYPE_ID", nullable = false)
    private SurveyParticipationType surveyParticipationType;

    @Column(name = "MAX_ATTEMPTS", nullable = true)
    private Integer maxAttempts;

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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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

    public Integer getMaxRespondents() {
        return maxRespondents;
    }

    public void setMaxRespondents(Integer maxRespondents) {
        this.maxRespondents = maxRespondents;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public SurveyParticipationType getSurveyParticipationType() {
        return surveyParticipationType;
    }

    public void setSurveyParticipationType(SurveyParticipationType surveyParticipationType) {
        this.surveyParticipationType = surveyParticipationType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}