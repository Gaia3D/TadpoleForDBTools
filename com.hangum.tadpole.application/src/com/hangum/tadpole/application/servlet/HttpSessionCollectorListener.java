package com.hangum.tadpole.application.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * HTTP session collector
 */
public class HttpSessionCollectorListener implements HttpSessionListener {
	private static final Logger logger = Logger.getLogger(HttpSessionCollectorListener.class);
	private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession().getId());
	}

	public static Map<String, HttpSession> getSessions() {
		return sessions;
	}

	public static HttpSession find(String sessionId) {
		return sessions.get(sessionId);
	}

}
