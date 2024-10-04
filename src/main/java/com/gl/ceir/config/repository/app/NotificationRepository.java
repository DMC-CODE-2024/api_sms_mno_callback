package com.gl.ceir.config.repository.app;

import com.gl.ceir.config.model.app.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Notification save(Notification paramNotification);

    Notification findByCorelationIdAndOperatorName(String paramString1, String paramString2);
}
