package com.gracelogic.platform.notification.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.model.StringJsonUserType;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "NOTIFICATION")
public class Notification extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NOTIFICATION_STATE_ID", nullable = false)
    private NotificationState notificationState;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NOTIFICATION_METHOD_ID", nullable = false)
    private NotificationMethod notificationMethod;

    @Column(name = "SOURCE", nullable = true)
    private String source;

    @Column(name = "DESTINATION", nullable = false)
    private String destination;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "BODY", nullable = false)
    private String body;

    @Type(JsonStringType.class)
    @Column(columnDefinition = "json", nullable = true, name = "FIELDS")
    private String fields;

    @Column(name = "PRIORITY", nullable = false)
    private Integer priority;

    @Column(name = "ERROR_DESCRIPTION", nullable = true)
    private String errorDescription;

    @Column(name = "REFERENCE_OBJECT_ID", nullable = true)
    private UUID referenceObjectId; //userId or other custom group field

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

    public NotificationState getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(NotificationState notificationState) {
        this.notificationState = notificationState;
    }

    public NotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(NotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }
}
