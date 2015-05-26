/*******************************************************************************
 * Copyright (c) 2012 - 2015 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.preference.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.eclipse.rap.rwt.RWT;

import com.hangum.tadpold.commons.libs.core.mails.dto.SMTPDTO;
import com.hangum.tadpole.engine.query.dao.system.UserDAO;
import com.hangum.tadpole.engine.query.dao.system.UserInfoDataDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserInfoData;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserQuery;
import com.hangum.tadpole.preference.define.PreferenceDefine;

/**
 * get administrator preference
 *
 *
 * @author hangum
 * @version 1.6.1
 * @since 2015. 4. 17.
 *
 */
public class GetAdminPreference extends GetPreferenceGeneral {
	private static final Logger logger = Logger.getLogger(GetAdminPreference.class);

	/**
	 * 
	 * @return
	 */
	public static SMTPDTO getSessionSMTPINFO() throws Exception {
		SMTPDTO dto = new SMTPDTO();
		
		HttpSession sStore = RWT.getRequest().getSession();
		dto = (SMTPDTO)sStore.getAttribute("smtpinfo");
		
		if(dto == null) {
			dto = new SMTPDTO();
			
//			try {
				UserDAO userDao = TadpoleSystem_UserQuery.getSystemAdmin();
				List<UserInfoDataDAO> listUserInfo = TadpoleSystem_UserInfoData.getUserInfoData(userDao.getSeq());
				Map<String, UserInfoDataDAO> mapUserInfoData = new HashMap<String, UserInfoDataDAO>();
				for (UserInfoDataDAO userInfoDataDAO : listUserInfo) {						
					mapUserInfoData.put(userInfoDataDAO.getName(), userInfoDataDAO);
				}
				
				String strHost = getValue(mapUserInfoData, PreferenceDefine.SMTP_HOST_NAME, PreferenceDefine.SMTP_HOST_NAME_VALUE);
				String strPort = getValue(mapUserInfoData, PreferenceDefine.SMTP_PORT, PreferenceDefine.SMTP_PORT_VALUE);
				String strEmail = getValue(mapUserInfoData, PreferenceDefine.SMTP_EMAIL, PreferenceDefine.SMTP_EMAIL_VALUE);
				String strPwd = getValue(mapUserInfoData, PreferenceDefine.SMTP_PASSWD, PreferenceDefine.SMTP_PASSWD_VALUE);
			
				dto.setHost(strHost);
				dto.setPort(strPort);
				dto.setEmail(strEmail);
				dto.setPasswd(strPwd);
				
				if("".equals(strHost) | "".equals(strPort) | "".equals(strEmail) | "".equals(strPwd)) {
					throw new Exception("SMTP Server가 설정 되어 있지 않습니다. ");
				}
				
				sStore.setAttribute("smtpinfo", dto);
//			} catch (Exception e) {
//				logger.error("get stmt info", e);
//			}
		}
		
		return dto;
	}
	
	public static SMTPDTO getSMTPINFO() {
		SMTPDTO dto = new SMTPDTO();
		
		try {
			UserDAO userDao = TadpoleSystem_UserQuery.getSystemAdmin();
			List<UserInfoDataDAO> listUserInfo = TadpoleSystem_UserInfoData.getUserInfoData(userDao.getSeq());
			Map<String, UserInfoDataDAO> mapUserInfoData = new HashMap<String, UserInfoDataDAO>();
			for (UserInfoDataDAO userInfoDataDAO : listUserInfo) {						
				mapUserInfoData.put(userInfoDataDAO.getName(), userInfoDataDAO);
			}
		
			dto.setHost(getValue(mapUserInfoData, PreferenceDefine.SMTP_HOST_NAME, PreferenceDefine.SMTP_HOST_NAME_VALUE));
			dto.setPort(getValue(mapUserInfoData, PreferenceDefine.SMTP_PORT, PreferenceDefine.SMTP_PORT_VALUE));
			dto.setEmail(getValue(mapUserInfoData, PreferenceDefine.SMTP_EMAIL, PreferenceDefine.SMTP_EMAIL_VALUE));
			dto.setPasswd(getValue(mapUserInfoData, PreferenceDefine.SMTP_PASSWD, PreferenceDefine.SMTP_PASSWD_VALUE));
			
		} catch (Exception e) {
			logger.error("get stmt info", e);
		}
		
		return dto;
	}
}
