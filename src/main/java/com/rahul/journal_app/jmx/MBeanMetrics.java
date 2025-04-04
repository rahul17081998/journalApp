package com.rahul.journal_app.jmx;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;

@Component
@Slf4j
public class MBeanMetrics {

    public MBeanMetrics(MeterRegistry registry) throws Exception {
        /*
        TodoListMBean mBean = new TodoList();
        ObjectName objectName = new ObjectName("rahul.example:type=TodoList, name=TodoListMBean");
        mBeanServer.registerMBean(mBean, objectName);
        return mBean;
        getTodoSize
        -javaagent:/Users/rahulkumar/jmx_prometheus_javaagent-1.2.0.jar=1234:/Users/rahulkumar/Documents/mongoDb-springboot/journalApp/config/jmx_exporter_config.yml
        */
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//        ObjectName objectName = new ObjectName("rahul.example:type=TodoList");
//
//
//        registry.gauge("rahul_example_todoSize", this, m -> {
//            try {
//                int x= (int) mBeanServer.getAttribute(objectName, "TodoSize");
//                return x;
//            } catch (Exception e) {
//                log.info("Error to get jmx metric data: Exception: {}", e.getMessage());
//                return -1; // Default if error occurs
//            }
//        });


        ObjectName memoryObject = new ObjectName("java.lang:type=Memory");
        registry.gauge("jvm_x_heap_memory_usage", this, m -> {
            try {
                CompositeData heapMemoryUsage = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
                long usedHeapMemory = (long) heapMemoryUsage.get("used");
                return usedHeapMemory;
            } catch (Exception e) {
                log.info("Error to get jmx metric data: Exception: {}", e.getMessage());
                return -1; // Default if error occurs
            }
        });
//        CompositeData heapMemoryUsage = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
//        long usedHeapMemory = (long) heapMemoryUsage.get("used");  // Extracting "used" memory
//        log.info("UsedHeapMemory: {}", usedHeapMemory);
//        registry.gauge("jvm_x_heap_memory_usage", usedHeapMemory);

    }
}
