package com.rahul.journal_app.jmx;

import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//@ManagedResource(objectName = "com.rahul.journal_app.jmx:type=Monitoruser")
//@Service
@Component
public class Monitoruser implements MonitoruserMBean {


    @Autowired
    UserRepository userRepository;

    public Monitoruser(){}

    @Override
    public int getUserCount() {
        return userRepository.findAll().size();
    }

    @Override
    public List<String> getUserWithAdminAccess() {
        return userRepository.findAll().stream()
                .filter(user->user.getRoles().stream().anyMatch(role->role.equals("ADMIN")))
                .map(User::getFirstName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUsersCity() {
        return userRepository.findAll().stream()
                .map(User::getCity)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public int getVerifiedUsers() {
//        return (int)userRepository.findAll().stream()
//                .filter(user-> user.isVerified())
//                .count();

        int count= (int) userRepository.countUserByVerified();
        return count;
    }
}
