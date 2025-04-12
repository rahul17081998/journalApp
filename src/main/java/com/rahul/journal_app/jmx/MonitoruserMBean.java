package com.rahul.journal_app.jmx;

import java.util.List;

//@MXBean
public interface MonitoruserMBean {
    public int getUserCount();
//    public List<String> getUserWithAdminAccess();
//    public List<String> getUsersCity();
    public int getVerifiedUsers();
}
