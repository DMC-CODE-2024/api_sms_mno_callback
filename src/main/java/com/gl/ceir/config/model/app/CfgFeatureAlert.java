package com.gl.ceir.config.model.app;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cfg_feature_alert")
public class CfgFeatureAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "alert_id")
    private String alertId;

    @Column(name = "description")
    private String description;

    @Column(name = "feature")
    private String feature;

    public CfgFeatureAlert() {}

    public CfgFeatureAlert(Integer id, LocalDateTime createdOn, LocalDateTime modifiedOn, String alertId, String description, String feature) {
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
        this.alertId = alertId;
        this.description = description;
        this.feature = feature;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return this.modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getAlertId() {
        return this.alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeature() {
        return this.feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CfgFeatureAlert{");
        sb.append("id=").append(this.id);
        sb.append(", createdOn=").append(this.createdOn);
        sb.append(", modifiedOn=").append(this.modifiedOn);
        sb.append(", alertId='").append(this.alertId).append('\'');
        sb.append(", description='").append(this.description).append('\'');
        sb.append(", feature='").append(this.feature).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

