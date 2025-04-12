package com.rahul.journal_app.prometheus;

import com.rahul.journal_app.service.UserService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

    @Autowired
    private UserService userService;


    public CustomMetrics(MeterRegistry registry){
        getMetrics(registry);
    }


    private void getMetrics(MeterRegistry registry){
        getActiveUsersMetrics(registry);
        updateUserCountWithNonEmptyJournals(registry);
        updateAdminUserCount(registry);
    }

    private void getActiveUsersMetrics(MeterRegistry registry) {
        Gauge.builder("journal_app_active_users_count", ()->userService.getActiveUserCount()
        )
                .description("Number of Active users in Journal App")
                .tags(Tags.of("env", "local")
                        .and("application", "journalApp")
                )
                .register(registry);
    }

    private void updateUserCountWithNonEmptyJournals(MeterRegistry registry){
        Gauge.builder("journal_app_non_empty_journal_users", ()->userService.getUserCountWithNonEmptyJournals()
                )
                .description("Number of users who have at least one journals")
                .tags(Tags.of("env", "local")
                        .and("application", "journalApp")
                )
                .register(registry);
    }

    private void updateAdminUserCount(MeterRegistry registry){
        Gauge.builder("journal_app_admin_access_users_count", ()->userService.getUserCountWithAdminAccess()
                )
                .description("Number of users who have ADMIN access")
                .tags(Tags.of("env", "local")
                        .and("application", "journalApp")
                )
                .register(registry);
    }
}
