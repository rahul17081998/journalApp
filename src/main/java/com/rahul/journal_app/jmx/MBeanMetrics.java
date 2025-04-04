package com.rahul.journal_app.jmx;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;

@Component
@Slf4j
public class MBeanMetrics {

    public MBeanMetrics(MeterRegistry registry) throws Exception {

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();


        ObjectName objectName = new ObjectName("rahul.example:type=TodoList");
        registry.gauge("rahul_example_todoSize", this, m -> {
            try {
                int x= (int) mBeanServer.getAttribute(objectName, "TodoSize");
                return x;
            } catch (Exception e) {
                log.info("Error to get jmx metric data: Exception: {}", e.getMessage());
                return -1; // Default if error occurs
            }
        });


        ObjectName memoryObject = new ObjectName("java.lang:type=Memory");
        CompositeData heapMemory = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
        long usedHeapMemory = (long) heapMemory.get("used");
        long maxHeapMemory = (long)heapMemory.get("max");
        long committedHeapMemory = (long)heapMemory.get("committed");
        long initHeapMemory = (long)heapMemory.get("init");

        Gauge.builder("jvm_x_heap_memory_usage", ()-> 0)
                .description("Custom metric to monitor JVM heap memory usage from JMX")
                .tags(Tags.of("type", "heap")
                        .and("source", "jmx MBean")
                        .and("env", "local")
                        .and("used", String.valueOf(usedHeapMemory))
                        .and("max", String.valueOf(maxHeapMemory))
                        .and("committed", String.valueOf(committedHeapMemory))
                        .and("init", String.valueOf(initHeapMemory))
                )
                .register(registry);
    }
}
