package com.rahul.journal_app.jmx;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;

@Component
public class MBeanMetrics {

    public MBeanMetrics(MeterRegistry registry) throws Exception {

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("java.lang:type=Memory");
        CompositeData heapMemoryUsage = (CompositeData) mBeanServer.getAttribute(objectName, "HeapMemoryUsage");
        Long usedHeapMemory = (Long) heapMemoryUsage.get("used");  // Extracting "used" memory

        registry.gauge("jvm_xxxx_heap_memory_usage", usedHeapMemory);

    }
}
