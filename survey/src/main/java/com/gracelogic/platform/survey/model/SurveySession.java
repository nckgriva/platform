package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_SESSION")
@TypeDefs({
        @TypeDef(
                name = "int-array",
                typeClass = IntArrayType.class
        )
})
public class SurveySession extends IdObject<UUID> {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @Column(name = "LAST_VISIT_IP", nullable = true)
    private String lastVisitIP;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_ID", nullable = false)
    private Survey survey;

    @Column(name = "STARTED_DT", nullable = false)
    private Date started;

    @Column(name = "ENDED_DT", nullable = true)
    private Date ended;

    @Column(name = "EXPIRATION_DT", nullable = true)
    private Date expirationDate;

    @Column(name = "CONCLUSION", nullable = true, length = 4000)
    private String conclusion;

    @Column(name = "LINK", nullable = true)
    private String link;

    @Type(type = "int-array")
    @Column(name = "PAGE_VISIT_HISTORY", columnDefinition = "integer[]")
    private Integer[] pageVisitHistory;
    /**
     * Detects if survey creator passing this survey
     */
    @Column(name = "IS_PREVIEW_SESSION", nullable = false)
    private Boolean previewSession;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastVisitIP() {
        return lastVisitIP;
    }

    public void setLastVisitIP(String lastVisitIP) {
        this.lastVisitIP = lastVisitIP;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer[] getPageVisitHistory() {
        return pageVisitHistory;
    }

    public void setPageVisitHistory(Integer[] pageVisitHistory) {
        this.pageVisitHistory = pageVisitHistory;
    }

    public Boolean getPreviewSession() {
        return previewSession;
    }

    public void setPreviewSession(Boolean previewSession) {
        this.previewSession = previewSession;
    }
}
