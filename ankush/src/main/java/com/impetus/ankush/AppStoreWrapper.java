/*******************************************************************************
 * ===========================================================
 * Ankush : Big Data Cluster Management Solution
 * ===========================================================
 * 
 * (C) Copyright 2014, by Impetus Technologies
 * 
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL v3) as
 * published by the Free Software Foundation;
 * 
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this software; if not, write to the Free Software Foundation, 
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 ******************************************************************************/
package com.impetus.ankush;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;

import org.apache.log4j.FileAppender;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.impetus.ankush.common.config.ConfigurationReader;
import com.impetus.ankush.common.domain.Log;
import com.impetus.ankush.common.framework.AbstractMonitor;
import com.impetus.ankush.common.framework.Clusterable;
import com.impetus.ankush.common.framework.Deployable;
import com.impetus.ankush.common.framework.ServiceMonitorable;
import com.impetus.ankush.common.mail.MailConf;
import com.impetus.ankush.common.mail.MailManager;
import com.impetus.ankush.common.service.AsyncExecutorService;
import com.impetus.ankush.common.service.GenericManager;
import com.impetus.ankush.common.utils.AnkushLogger;
import com.impetus.ankush.common.utils.FileUtils;
import com.impetus.ankush.common.utils.PasswordUtil;
import com.impetus.ankush.common.utils.ReflectionUtil;
import com.impetus.ankush.common.utils.XmlUtil;

/**
 * The Class AppStoreWrapper.
 */
public class AppStoreWrapper {

	/** The Constant KEY_APPLICATION_CONTEXT. */
	private static final String KEY_APPLICATION_CONTEXT = "applicationContext";

	/** The Constant KEY_ANKUSH_CONF_READER. */
	private static final String KEY_ANKUSH_CONF_READER = "ankushConfReader";

	/** The Constant KEY_ANKUSH_PROPERTIES. */
	private static final String KEY_ANKUSH_PROPERTIES = "ankushProperties";

	/** The Constant KEY_SERVLET_CONTEXT. */
	private static final String KEY_SERVLET_CONTEXT = "servletContext";

	/** The Constant KEY_APP_PATH. */
	private static final String KEY_APP_PATH = "appPath";

	/** The Constant KEY_RESOURCE_PATH. */
	private static final String KEY_RESOURCE_PATH = "resourcePath";

	/** The Constant KEY_MAIL_MANAGER. */
	private static final String KEY_MAIL_MANAGER = "mailManager";

	/** The Constant KEY_EXECUTOR. */
	private static final String KEY_EXECUTOR = "executor";

	/** The Constant KEY_LOG_MANAGER. */
	private static final String KEY_LOG_MANAGER = "logManager";

	/** The Constant KEY_SERVER. */
	private static final String KEY_SERVER = "server";

	/** The Constant KEY_REPO. */
	private static final String KEY_REPO = "repo";
	
	/** The Constant KEY_REPO. */
	private static final String KEY_LICENSE = "license";

	/** The log. */
	private static AnkushLogger log = new AnkushLogger(AppStoreWrapper.class);

	/**
	 * Get Database manager.
	 * 
	 * @param <S>
	 *            the generic type
	 * @param managerName
	 *            the manager name
	 * @param targetClass
	 *            the target class
	 * @return the manager
	 */
	public static <S> GenericManager<S, Long> getManager(String managerName,
			Class<S> targetClass) {
		if (getApplicationContext() == null) {
			return null;
		}
		return (GenericManager<S, Long>) getApplicationContext().getBean(
				managerName);
	}

	/**
	 * Get service.
	 * 
	 * @param <S>
	 *            the generic type
	 * @param serviceName
	 *            the service name
	 * @param targetClass
	 *            the target class
	 * @return the service
	 */
	public static <S> S getService(String serviceName, Class<S> targetClass) {
		if (getApplicationContext() == null) {
			return null;
		}
		return (S) getApplicationContext().getBean(serviceName);
	}

	/**
	 * Sets the application context.
	 * 
	 * @param applicationContext
	 *            the applicationContext to set
	 */
	public static void setApplicationContext(
			ApplicationContext applicationContext) {
		AppStore.setObject(KEY_APPLICATION_CONTEXT, applicationContext);
	}

	/**
	 * Gets the application context.
	 * 
	 * @return the application context
	 */
	public static ApplicationContext getApplicationContext() {
		return (ApplicationContext) AppStore.getObject(KEY_APPLICATION_CONTEXT);
	}

	/**
	 * Gets the ankush conf reader.
	 * 
	 * @return the ankushConfReader
	 * @author Hokam Chauhan
	 */
	public static ConfigurationReader getAnkushConfReader() {
		return (ConfigurationReader) AppStore.getObject(KEY_ANKUSH_CONF_READER);
	}

	/**
	 * Sets the ankush conf reader.
	 * 
	 * @param ankushConfReader
	 *            the ankushConfReader to set
	 * @author Hokam Chauhan
	 */
	public static void setAnkushConfReader(ConfigurationReader ankushConfReader) {
		/* Getting user home path */
		String userHome = System.getProperty("user.home");
		if (ankushConfReader != null) {
			/* setting serverMetadataPath */
			AppStore.setObject(KEY_SERVER,
					userHome + ankushConfReader.getStringValue(KEY_SERVER));
			/* setting serverRepoPath */
			AppStore.setObject(KEY_REPO,
					userHome + ankushConfReader.getStringValue(KEY_REPO));
			/* setting serverLicensePath */
			AppStore.setObject(KEY_LICENSE,
					userHome + ankushConfReader.getStringValue(KEY_LICENSE));
		}
		AppStore.setObject(KEY_ANKUSH_CONF_READER, ankushConfReader);
	}

	/**
	 * Method to get app path.
	 * 
	 * @return returns server metadata path
	 */
	public static String getServerMetadataPath() {
		return (String) AppStore.getObject(KEY_SERVER);
	}

	/**
	 * Method to get repository path.
	 * 
	 * @return returns path of component repository on server
	 */
	public static String getServerRepoPath() {
		return (String) AppStore.getObject(KEY_REPO);
	}

	/**
	 * Method to get Servlet Context.
	 * 
	 * @return the servlet context
	 */
	public static ServletContext getServletContext() {
		return (ServletContext) AppStore.getObject(KEY_SERVLET_CONTEXT);
	}

	/**
	 * Method to set servlet context.
	 * 
	 * @param servletContext
	 *            the new servlet context
	 */
	public static void setServletContext(ServletContext servletContext) {
		String appPath = servletContext.getRealPath("/");
		String resourcesPath = "";

		appPath = FileUtils.getSeparatorTerminatedPathEntry(appPath);
		resourcesPath = appPath + "WEB-INF/classes/";

		if (appPath.contains("src" + File.separator)) {
			resourcesPath = appPath + "../resources/";
		}

		AppStore.setObject(KEY_APP_PATH, appPath);
		AppStore.setObject(KEY_RESOURCE_PATH, resourcesPath);
		AppStore.setObject(KEY_SERVLET_CONTEXT, servletContext);
	}

	/**
	 * Method to get app path.
	 * 
	 * @return path of application
	 */
	public static String getAppPath() {
		return (String) AppStore.getObject(KEY_APP_PATH);
	}

	/**
	 * Method to get resource path.
	 * 
	 * @return returns path to resources
	 */
	public static String getResourcePath() {
		return (String) AppStore.getObject(KEY_RESOURCE_PATH);
	}

	/**
	 * Method to get configured MailManager.
	 * 
	 * @return returns the currently configured mail manager object in
	 *         AppStoreWrapper
	 */
	public static MailManager getMailManager() {
		return (MailManager) AppStore.getObject(KEY_MAIL_MANAGER);
	}

	/**
	 * Method to setup mail manager in AppStoreWrapper.
	 * 
	 * @param mailManager
	 *            mailmanager object configured with proper mail configuration
	 *            that will be used to send mail
	 */

	public static void setMailManager(MailManager mailManager) {
		AppStore.setObject(KEY_MAIL_MANAGER, mailManager);
	}

	/**
	 * Method to get executor.
	 * 
	 * @return the executor
	 */
	public static AsyncExecutorService getExecutor() {
		return (AsyncExecutorService) AppStore.getObject(KEY_EXECUTOR);
	}

	/**
	 * Method to set executor.
	 * 
	 * @param executor
	 *            the new executor
	 */
	public static void setExecutor(AsyncExecutorService executor) {
		AppStore.setObject(KEY_EXECUTOR, executor);
	}

	/**
	 * Get Log manager.
	 * 
	 * @return Log Manager.
	 */
	public static GenericManager<Log, Long> getLogManager() {

		return (GenericManager<Log, Long>) getApplicationContext().getBean(
				KEY_LOG_MANAGER);
	}

	/**
	 * Method to setup mail manager in AppStoreWrapper by extracting mail
	 * configuration from a map.
	 * 
	 * @param mailMap
	 *            the new up mail
	 */
	public static void setupMail(Map mailMap) {
		if (mailMap == null) {
			return;
		}
		MailConf appMail = new MailConf();
		int port = 0;
		try {
			port = Integer.parseInt((String) mailMap.get("port"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		appMail.setPort(port);
		appMail.setServer((String) mailMap.get("server"));
		appMail.setUserName((String) mailMap.get("userName"));
		String decoded = new PasswordUtil().decrypt((String) mailMap
				.get("password"));
		appMail.setPassword(decoded);
		try {
			Boolean secured = (Boolean) mailMap.get("secured");
			if (secured != null) {
				appMail.setSecured(secured);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (appMail != null) {
			MailManager mm = new MailManager(appMail);
			AppStoreWrapper.setMailManager(mm);
		}
	}

	public static FileAppender getAppender(String fileName) {
		return (FileAppender) AppStore.getObject(fileName);
	}

	public static void setAppender(String fileName, FileAppender appender) {
		AppStore.setObject(fileName, appender);
	}

	public static void setAnkushConfigurableClassNames() {
		System.out
				.println("public static void setAnkushConfigurableClassNames()");
		List<String> classTypes = new ArrayList<String>();
		classTypes.add("cluster");
		classTypes.add("monitor");
		classTypes.add("service");

		for (String classType : classTypes) {
			Map map = new HashMap();
			String filePath = getConfigClassNameFile(classType);
			System.out.println("file path " + filePath);
			if (filePath != null) {
				try {
					Map items = XmlUtil.loadConfigXMLParameters(filePath,
							classType, Collections.singletonList("class"));
					System.out.println(classType + " : " + items);
					AppStore.setObject(classType, items);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}

		List<String> subItems = new ArrayList<String>();
		subItems.add("priority");
		subItems.add("conf");
		subItems.add("class");
		String filePath = getConfigClassNameFile("deployer");
		Map items = XmlUtil.loadConfigXMLParameters(filePath, "deployer",
				subItems);

		Map priorityMap = new HashMap();
		for (Object id : items.keySet()) {
			Map map = (Map) items.get(id);
			String className = map.get("class").toString();
			priorityMap.put(className, map.get("priority"));
		}

		// setting priority map of classname and priority.
		items.put("priority", priorityMap);

		System.out.println("deployer : " + items);
		AppStore.setObject("deployer", items);
	}

	/**
	 * Gets the app config.
	 * 
	 * @param type
	 *            the type
	 * @return the app config
	 */
	private static String getConfigClassNameFile(String type) {
		// getting configuration file
		Resource resource = new ClassPathResource("/ankush-" + type
				+ "-config.xml");

		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the clusterable instance by id.
	 * 
	 * @param id
	 *            the id
	 * @return the clusterable instance by id
	 */
	public static Clusterable getClusterableInstanceById(String id) {
		// get clusterable class from id
		Map map = (Map) AppStore.getObject("cluster");
		Map classConfMap = (Map) map.get(id);
		try{
			String className = (String) classConfMap.get("class");
			/*if (className == null) {
				return null;
			}*/
			// create clusterable object
			return ReflectionUtil.getClusterableObject(className);
		}catch(NullPointerException e){
			return null;
		}
	}

	/**
	 * Gets the monitorable instance by id.
	 * 
	 * @param id
	 *            the id
	 * @return the monitorable instance by id
	 */
	public static AbstractMonitor getMonitorableInstanceById(String id) {
		// get clusterable class from id
		Map map = (Map) AppStore.getObject("monitor");
		Map classConfMap = (Map) map.get(id);
		String className = (String) classConfMap.get("class");
		if (className == null) {
			return null;
		}
		// create clusterable object
		return ReflectionUtil.getMonitorableObject(className);
	}

	/**
	 * Gets the monitorable instance by id.
	 * 
	 * @param serviceName
	 *            the id
	 * @return the monitorable instance by id
	 */
	public static ServiceMonitorable getServiceMonitorableInstanceByServiceName(String serviceName) {

		Map map = (Map) AppStore.getObject("service");
		
		Map classConfMap = (Map) map.get(serviceName);
		String className = (String) classConfMap.get("class");

		if (className == null) {
			return null;
		}
		// create ServiceMonitorable object
		return ReflectionUtil.getServiceMonitorableObject(className);
	}

	/**
	 * Gets the deployable priority.
	 * 
	 * @param className
	 *            the class name
	 * @return the deployable priority
	 */
	public static int getDeployablePriority(String className) {
		Map map = (Map) AppStore.getObject("deployer");
		Map priorityMap = (Map) map.get("priority");
		String priority = (String) priorityMap.get(className);

		if (priority == null) {
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(priority);
	}

	/**
	 * Gets the instance by id.
	 * 
	 * @param id
	 *            the id
	 * @return the instance by id
	 */
	public static Deployable getDeployableInstanceById(String id) {
		Map map = (Map) AppStore.getObject("deployer");
		Map classConfMap = (Map) map.get(id);
		String className = (String) classConfMap.get("class");

		if (className == null) {
			return null;
		}
		// create deployable object
		return ReflectionUtil.getDeployableObject(className);
	}

	/**
	 * Gets the instance by id.
	 * 
	 * @param id
	 *            the id
	 * @return the instance by id
	 */
	public static String getDeployableConfClassName(String id) {
		Map map = (Map) AppStore.getObject("deployer");
		Map classConfMap = (Map) map.get(id);
		String className = (String) classConfMap.get("conf");

		if (className == null) {
			return null;
		}
		return className;
	}
}
