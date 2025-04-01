package com.rahul.journal_app.config;

import com.rahul.journal_app.jmx.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class JmxConfigClass {


    @Bean
    public MBeanServer mBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    @Bean
    public TodoListMBean todoListMBean(MBeanServer mBeanServer) throws Exception {
        TodoListMBean mBean = new TodoList();
        ObjectName objectName = new ObjectName("rahul.example:type=TodoList, name=TodoListMBean");
        mBeanServer.registerMBean(mBean, objectName);
        return mBean;
    }


    @Bean
    public QueueSampler queueSamplerMBean(MBeanServer mBeanServer) throws Exception {
        Queue<String> queue = new ArrayBlockingQueue<>(10);
        queue.add("Request-1");
        queue.add("Request-2");
        queue.add("Request-3");

        QueueSampler mxBean = new QueueSampler(queue);
        ObjectName objectName = new ObjectName("rahul.example:type=QueueSampler, name=QueueSampler");
        mBeanServer.registerMBean(mxBean, objectName);

        return mxBean;
    }


//    @Bean
//    public MonitoruserMBean userMBean(MBeanServer mBeanServer, MonitoruserMBean userMBean) throws Exception {
//        ObjectName objectName = new ObjectName("com.rahul.journal_app.jmx:type=Monitoruser");
//        mBeanServer.registerMBean(userMBean, objectName);
//        return userMBean;
//    }

    @Bean
    public MonitoruserMBean userMBean(MBeanServer mBeanServer) throws Exception {
        MonitoruserMBean userMBean = new Monitoruser(); // Create instance directly
        ObjectName objectName = new ObjectName("rahul.example:type=MonitoruserMBean, name=MonitoruserMBean");
        mBeanServer.registerMBean(userMBean, objectName);
        return userMBean;
    }
}
