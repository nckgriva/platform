package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
/*
 Модель, определяющая, что опрос был начат пользователем User, во время started
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_PASSING")
public class SurveyPassing extends IdObject<UUID> {
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

    @Column(name = "started_dt", nullable = false)
    private Date started;

    @Column(name = "ended_dt", nullable = true)
    private Date ended;

    @Column(name = "conclusion", nullable = true)
    private String conclusion;

    @Column(name = "link", nullable = true)
    private String link;

    @Column(name = "last_visited_page_index", nullable = true)
    private Integer lastVisitedPageIndex;

    @Column(name = "finish_page_index", nullable = false)
    private Integer finishPageIndex;

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

    public Integer getLastVisitedPageIndex() {
        return lastVisitedPageIndex;
    }

    public void setLastVisitedPageIndex(Integer lastVisitedPageIndex) {
        this.lastVisitedPageIndex = lastVisitedPageIndex;
    }

    public Integer getFinishPageIndex() {
        return finishPageIndex;
    }

    public void setFinishPageIndex(Integer finishPageIndex) {
        this.finishPageIndex = finishPageIndex;
    }
}
