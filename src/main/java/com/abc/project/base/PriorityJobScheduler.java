/****************************************************************************
 * File Name 		: PriorityJobScheduler.java
 * Package			: com.dxc.zurich.base
 * Author			: pmusunuru2
 * Creation Date	: Feb 16, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.base;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.runners.BrowserRunner;
import com.abc.project.utils.PropertyHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 2:04:49 pm
 */
public class PriorityJobScheduler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(PriorityJobScheduler.class);

	private ExecutorService priorityJobPoolExecutor;

	private Map<BrowserRunner, Callable<Boolean>> aBrowserTask;

	private LinkedList<Future<Boolean>> broWserTasks;

	public PriorityJobScheduler() {
		aBrowserTask = new LinkedHashMap<>();
		broWserTasks = new LinkedList<>();
	}

	/***
	 * adds Jobs to Queue
	 * 
	 * @param aBrowserRunner
	 * @throws Exception
	 */
	public void addJobstoQue(BrowserRunner aBrowserRunner)  {
		if (aBrowserRunner != null) {
			aBrowserTask.put(aBrowserRunner, aBrowserRunner);
		}
	}

	/***
	 * Schedules the Jobs in Queue
	 */
	public void scheduleJob() {
		if (aBrowserTask.isEmpty()) {
			return;
		}
		int iPoolSize = aBrowserTask.size();
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
				.setNameFormat(AppConstants.BROWSER_RUNNER_THERAD_POOL_FORMAT).build();
		priorityJobPoolExecutor = Executors.newFixedThreadPool(iPoolSize, namedThreadFactory);

		List<Map.Entry<BrowserRunner, Callable<Boolean>>> lstSortTask = new LinkedList<>(aBrowserTask.entrySet());

		Collections.sort(lstSortTask, (config1, config2) -> {
			if (config1.getKey().getPriority() == config2.getKey().getPriority()) {
				return 0;
			} else if (config1.getKey().getPriority() < config2.getKey().getPriority()) {
				return 1;
			} else {
				return -1;
			}
		});

		for (Entry<BrowserRunner, Callable<Boolean>> entry : lstSortTask) {
			broWserTasks.add(priorityJobPoolExecutor.submit(entry.getValue()));
		}
	}

	/***
	 * Waits for all the jobs in the queue to be completed
	 */
	public void waitQueueToComplete() throws Exception {
		String strMethodName = "waitPrevQueToComplete";
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			String strDBExportExecutionTimeOut = PropertyHandler.getExternalString(AppConstants.BROWSER_TASK_TOUT_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			long timeout = !StringUtils.isEmpty(strDBExportExecutionTimeOut)
					&& StringUtils.isNumeric(strDBExportExecutionTimeOut) ? Long.valueOf(strDBExportExecutionTimeOut)
							: 0;

			if (CollectionUtils.isNotEmpty(broWserTasks)) {
				for (Future<Boolean> aFuture : broWserTasks) {
					if (null != aFuture && (timeout == 0 ? aFuture.get() : aFuture.get(timeout, TimeUnit.MINUTES))) {
						continue;
					}
				}
				shutdownExecutorService();
				priorityJobPoolExecutor = null;
				aBrowserTask = null;
			}
		} catch (InterruptedException be) {
			throw be;
		} catch (Exception ex) {
			throw new Exception(ErrorMsgConstants.TH_INTERRUPT, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	/**
	 * Shutdowns the Job Service
	 * 
	 * @throws Exception
	 */
	public void shutdownExecutorService() throws Exception {

		if (null != priorityJobPoolExecutor) {
			priorityJobPoolExecutor.shutdown();
			try {
				if (!priorityJobPoolExecutor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
					priorityJobPoolExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				throw new InterruptedException(ErrorMsgConstants.SHUT_DOWN);
			}
		}
	}
}
