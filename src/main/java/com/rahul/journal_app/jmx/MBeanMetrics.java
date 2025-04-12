//package com.rahul.journal_app.jmx;
//
//import io.micrometer.core.instrument.Gauge;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tags;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.management.*;
//import javax.management.openmbean.CompositeData;
//import java.lang.management.ManagementFactory;
//
//@Component
//@Slf4j
//public class MBeanMetrics {
//
//    public MBeanMetrics(MeterRegistry registry) throws Exception {
//
//        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//
//
//        ObjectName objectName = new ObjectName("rahul.example:type=TodoList");
//
////        double val=-1;
////        int todoSize= (int) mBeanServer.getAttribute(objectName, "TodoSize");
////        val=todoSize;
////
////            Gauge.builder("rahul_example_TodoList_todoSize", ()-> 0)
////                    .description("Number of todo count")
////                    .tags(Tags.of("type", "todoList")
////                            .and("env", "local")
////                            .and("todoSize", String.valueOf(val))
////                    )
////                    .register(registry);
//
//
//
//
//        registry.gauge("rahul_example_TodoList_todoSize", this, m -> {
//            try {
//                int x= (int) mBeanServer.getAttribute(objectName, "TodoSize");
//                return x;
//            } catch (Exception e) {
//                log.info("Error to get jmx metric data: Exception: {}", e.getMessage());
//                return -1; // Default if error occurs
//            }
//        });
//
//
//        ObjectName memoryObject = new ObjectName("java.lang:type=Memory");
//
//        Gauge.builder("heap_memory_used", ()-> {
//                    CompositeData heapMemory = null;
//                    try {
//                        heapMemory = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    long usedHeapMemory = (long) heapMemory.get("used");
//                    return (double)usedHeapMemory;
//                })
//                .description("Custom metric to monitor JVM heap memory usage from JMX")
//                .tags(Tags.of("type", "heap")
//                        .and("source", "jmx MBean")
//                        .and("env", "local")
//                )
//                .register(registry);
//
//        Gauge.builder("heap_memory_max", ()-> {
//                    CompositeData heapMemory = null;
//                    try {
//                        heapMemory = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    long maxHeapMemory = (long)heapMemory.get("max");
//                    return maxHeapMemory;
//                })
//                .description("Custom metric to monitor JVM heap memory usage from JMX")
//                .tags(Tags.of("type", "heap")
//                        .and("source", "jmx MBean")
//                        .and("env", "local")
//                )
//                .register(registry);
//
//
//
//        Gauge.builder("heap_memory_committed", ()-> {
//                    CompositeData heapMemory = null;
//                    try {
//                        heapMemory = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    long committedHeapMemory = (long)heapMemory.get("committed");
//                    return committedHeapMemory;
//                })
//                .description("Custom metric to monitor JVM heap memory usage from JMX")
//                .tags(Tags.of("type", "heap")
//                        .and("source", "jmx MBean")
//                        .and("env", "local")
//                )
//                .register(registry);
//
//
//        Gauge.builder("heap_memory_init", ()-> {
//                    CompositeData heapMemory = null;
//                    try {
//                        heapMemory = (CompositeData) mBeanServer.getAttribute(memoryObject, "HeapMemoryUsage");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    long initHeapMemory = (long)heapMemory.get("init");
//                    return initHeapMemory;
//                })
//                .description("Custom metric to monitor JVM heap memory usage from JMX")
//                .tags(Tags.of("type", "heap")
//                        .and("source", "jmx MBean")
//                        .and("env", "local")
//                )
//                .register(registry);
//        }
//}
