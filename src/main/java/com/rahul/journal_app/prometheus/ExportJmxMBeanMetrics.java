package com.rahul.journal_app.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;

@Slf4j
@Component
public class ExportJmxMBeanMetrics {

    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    public ExportJmxMBeanMetrics(MeterRegistry registry){
        registerHeapMemoryMetrics(registry);
        registerTodoMetric(registry);
    }

    private void registerTodoMetric(MeterRegistry meterRegistry){

        Gauge.builder("jmx_rahul_example_todoSize",
                        ()->fetchJmxValue("rahul.example:type=TodoList", "TodoSize", null, "jmx_rahul_example_todoSize")
                )
                .description("Number of todo count")
                .tags(Tags.of("type", "todoList", "env", "local"))
                .register(meterRegistry);
    }


    private void registerHeapMemoryMetrics(MeterRegistry meterRegistry){
        Tags baseTag=Tags.of("type", "heap", "source", "jmx MBean", "env", "local");

        Gauge.builder("heap_memory_used",
                ()->fetchJmxValue("java.lang:type=Memory", "HeapMemoryUsage",  "used", "heap_memory_used")
                )
                .description("JVM heap memory used")
                .tags(baseTag)
                .register(meterRegistry);

        Gauge.builder("heap_memory_max",
                        ()->fetchJmxValue("java.lang:type=Memory", "HeapMemoryUsage",  "max", "heap_memory_used")
                )
                .description("JVM heap memory max")
                .tags(baseTag)
                .register(meterRegistry);

        Gauge.builder("heap_memory_committed",
                        ()->fetchJmxValue("java.lang:type=Memory", "HeapMemoryUsage",  "committed", "heap_memory_committed")
                )
                .description("JVM heap memory committed")
                .tags(baseTag)
                .register(meterRegistry);

        Gauge.builder("heap_memory_init",
                        ()->fetchJmxValue("java.lang:type=Memory", "HeapMemoryUsage",  "init", "heap_memory_init")
                )
                .description("JVM heap memory inti")
                .tags(baseTag)
                .register(meterRegistry);
    }




    private Double fetchJmxValue(String objectNameStr, String attributeName, String subAttributeName, String metricName) {
        try {
            ObjectName objectName = new ObjectName(objectNameStr);
            Object attribute = mBeanServer.getAttribute(objectName, attributeName);

            if (subAttributeName != null && attribute instanceof CompositeData compositeData) {
                Object value = compositeData.get(subAttributeName);
                if (value instanceof Number number) {
                    return number.doubleValue();
                }
            } else if (attribute instanceof Number number) {
                return number.doubleValue();
            } else {
                log.warn("Unsupported attribute type for {}.{}", objectNameStr, attributeName);
            }
        } catch (Exception e) {
            log.debug("Skipping metric {} due to JMX fetch error: {}", metricName, e.getMessage());
        }

        return null; // Don't expose the metric if it fails
    }
}
