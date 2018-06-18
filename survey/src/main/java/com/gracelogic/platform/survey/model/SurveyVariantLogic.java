package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_VARIANT_LOGIC")
public class SurveyVariantLogic extends IdObject<UUID> {
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
     * Вопрос, относящийся к данному answerVariant
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
     * Необходимо ли выбрать вариант, чтобы действие сработало
     */
    @Column(name = "IS_SELECTION_REQUIRED", nullable = false)
    private boolean isSelectionRequired;

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
    private UUID logicType;

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

    public SurveyAnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(SurveyAnswerVariant answerVariant) {
        this.answerVariant = answerVariant;
    }

    public boolean isSelectionRequired() {
        return isSelectionRequired;
    }

    public void setSelectionRequired(boolean selectionRequired) {
        isSelectionRequired = selectionRequired;
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

    public UUID getLogicType() {
        return logicType;
    }

    public void setLogicType(UUID logicType) {
        this.logicType = logicType;
    }

    public SurveyQuestion getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }
}
