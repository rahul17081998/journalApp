package com.rahul.journal_app;

import com.rahul.journal_app.controller.JournalEntryControllerV2;
import com.rahul.journal_app.jmx.QueueSampler;
import com.rahul.journal_app.jmx.QueueSamplerMXBean;
import com.rahul.journal_app.jmx.TodoList;
import com.rahul.journal_app.jmx.TodoListMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@SpringBootApplication
@EnableScheduling
public class JournalApplication {

	private static final Logger logger = LoggerFactory.getLogger(JournalApplication.class);

	public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		ObjectName objectNameTodoList = new ObjectName("rahul.example:type=TodoList");
		TodoListMBean mBean = new TodoList();
		mbs.registerMBean(mBean,objectNameTodoList);

		ObjectName objectNameQueueSampler = new ObjectName("rahul.example:type=QueueSampler");
		Queue<String> queue = new ArrayBlockingQueue<String>(10);
		queue.add("Request-1");
		queue.add("Request-2");
		queue.add("Request-3");
		QueueSampler mxBean = new QueueSampler(queue);
		mbs.registerMBean(mxBean,objectNameQueueSampler);


		ConfigurableApplicationContext context =SpringApplication.run(JournalApplication.class, args);
		logger.info("> Active Environment: {}", context.getEnvironment());
	}

}
