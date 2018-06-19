package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_VARIANT_LOGIC")
public class SurveyLogicTrigger extends IdObject<UUID> {
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

    /**
     * Страница, к которой относится данная логика
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_PAGE_ID", nullable = false)
    private SurveyPage surveyPage;

    /**
     * Отслеживаемый вопрос / вопрос, относящийся к данному answerVariant
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_QUESTION_ID", nullable = false)
    private SurveyQuestion surveyQuestion;

    /**
     * Отслеживаемый вариант ответа
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ANSWER_VARIANT_ID", nullable = false)
    private SurveyAnswerVariant answerVariant;

    /**
     * Если TRUE, логика срабатывает при отвеченном вопросе/выбранном варианте
     */
    @Column(name = "IS_INTERACTION_REQUIRED", nullable = false)
    private boolean isInteractionRequired;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TARGET_QUESTION_ID", nullable = false)
    private SurveyQuestion targetQuestion;

    @Column(name = "new_conclusion", nullable = true)
    private String newConclusion;

    @Column(name = "page_index", nullable = true)
    private Integer pageIndex;

    @Column(name = "new_link", nullable = true)
    private String newLink;

    @Column(name = "logic_type", nullable = false)
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID logicActionType;

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

    public SurveyPage getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(SurveyPage surveyPage) {
        this.surveyPage = surveyPage;
    }

    public SurveyQuestion getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public SurveyAnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(SurveyAnswerVariant answerVariant) {
        this.answerVariant = answerVariant;
    }

    public boolean isInteractionRequired() {
        return isInteractionRequired;
    }

    public void setInteractionRequired(boolean interactionRequired) {
        isInteractionRequired = interactionRequired;
    }

    public SurveyQuestion getTargetQuestion() {
        return targetQuestion;
    }

    public void setTargetQuestion(SurveyQuestion targetQuestion) {
        this.targetQuestion = targetQuestion;
    }

    public String getNewConclusion() {
        return newConclusion;
    }

    public void setNewConclusion(String newConclusion) {
        this.newConclusion = newConclusion;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getNewLink() {
        return newLink;
    }

    public void setNewLink(String newLink) {
        this.newLink = newLink;
    }

    public UUID getLogicActionType() {
        return logicActionType;
    }

    public void setLogicActionType(UUID logicActionType) {
        this.logicActionType = logicActionType;
    }
}
