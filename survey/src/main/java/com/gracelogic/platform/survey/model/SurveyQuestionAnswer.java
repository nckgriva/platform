package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.filestorage.model.StoredFile;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_QUESTION_ANSWER")
public class SurveyQuestionAnswer extends IdObject<UUID> {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_SESSION_ID", nullable = false)
    private SurveySession surveySession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAGE_ID", nullable = false)
    private SurveyPage surveyPage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private SurveyQuestion surveyQuestion;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ANSWER_VARIANT_ID", nullable = true)
    private SurveyAnswerVariant answerVariant;

    @Column(name = "TEXT", nullable = true, length = 4000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "STORED_FILE_ID", nullable = true)
    private StoredFile storedFile; // Для хранения прикрепленного пользователем файла

    @Column(name = "SELECTED_MATRIX_ROW", nullable = true)
    private Integer selectedMatrixRow;

    @Column(name = "SELECTED_MATRIX_COLUMN", nullable = true)
    private Integer selectedMatrixColumn;

    public SurveyQuestionAnswer() {}

    public SurveyQuestionAnswer(SurveySession surveySession, SurveyQuestion question, SurveyAnswerVariant answerVariant,
                                String text, StoredFile storedFile) {
        this.surveySession = surveySession;
        this.surveyPage = question.getSurveyPage();
        this.surveyQuestion = question;
        this.answerVariant = answerVariant;
        this.text = text;
        this.storedFile = storedFile;
    }

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

    public SurveySession getSurveySession() {
        return surveySession;
    }

    public void setSurveySession(SurveySession surveySession) {
        this.surveySession = surveySession;
    }

    public SurveyAnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(SurveyAnswerVariant answerVariant) {
        this.answerVariant = answerVariant;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public StoredFile getStoredFile() {
        return storedFile;
    }

    public void setStoredFile(StoredFile storedFile) {
        this.storedFile = storedFile;
    }

    public SurveyQuestion getQuestion() {
        return surveyQuestion;
    }

    public void setQuestion(SurveyQuestion question) {
        this.surveyQuestion = question;
    }

    public Integer getSelectedMatrixRow() {
        return selectedMatrixRow;
    }

    public void setSelectedMatrixRow(Integer selectedMatrixRow) {
        this.selectedMatrixRow = selectedMatrixRow;
    }

    public Integer getSelectedMatrixColumn() {
        return selectedMatrixColumn;
    }

    public void setSelectedMatrixColumn(Integer selectedMatrixColumn) {
        this.selectedMatrixColumn = selectedMatrixColumn;
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

    @Override
    public String toString() {
        return "SurveyQuestionAnswer{" +
                "id=" + id +
                ", created=" + created +
                ", changed=" + changed +
                ", surveySession=" + surveySession +
                ", surveyPage=" + surveyPage +
                ", surveyQuestion=" + surveyQuestion +
                ", answerVariant=" + answerVariant +
                ", text='" + text + '\'' +
                ", storedFile=" + storedFile +
                ", selectedMatrixRow=" + selectedMatrixRow +
                ", selectedMatrixColumn=" + selectedMatrixColumn +
                '}';
    }
}
