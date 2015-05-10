/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.query.sql;

import java.sql.Statement;

import org.apache.log4j.Logger;

import com.hangum.tadpole.engine.manager.TadpoleSQLManager;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.session.manager.SessionManager;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * tadpole system에서 공통으로 사용하는 모듈
 * 
 * @author hangum
 *
 */
public class TadpoleSystemCommons {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TadpoleSystemCommons.class);

//	/**
//	 * smtm.execute문의 쿼리를 날립니다. 즉 select 이외의 문...
//	 * 
//	 * @param selText
//	 */
//	public static boolean executSQL(UserDBDAO userDB, String selText) throws Exception {
//		if(logger.isDebugEnabled()) logger.debug("[executeSQL]" + selText);
//		
//		Connection javaConn = null;
//		Statement stmt = null;
//		try {
//			SqlMapClient client = TadpoleSQLManager.getInstance(userDB);
//			javaConn = client.getDataSource().getConnection();
//			stmt = javaConn.createStatement();
//
//			return stmt.execute( selText );
//		} finally {
//			try { if(stmt != null) stmt.close(); } catch(Exception e){}
//			try { if(javaConn != null) javaConn.close(); } catch(Exception e){}
//		}
//	}

	/**
	 * 쿼리중에 quote sql을 반영해서 작업합니다.
	 * 
	 * @param userDB
	 * @param executeType
	 * @param strDML
	 * @param args
	 */
	public static boolean executSQL(UserDBDAO userDB, String executeType, String strDML, String ... args) throws Exception {
		String strQuery = String.format(strDML, args);
		
		java.sql.Connection javaConn = null;
		try {
			SqlMapClient client = TadpoleSQLManager.getInstance(userDB);
			javaConn = client.getDataSource().getConnection();
			
			if(logger.isDebugEnabled()) logger.debug(String.format(strDML, args));
			
			Statement stmt = javaConn.createStatement();

			return stmt.execute(strDML);
			
		} finally {
			// save schema history
			
			TadpoleSystem_SchemaHistory.save(SessionManager.getUserSeq(), userDB, 
				"EDITOR",
				executeType,
				"",
				strQuery);
			
			try { if(javaConn != null) javaConn.close(); } catch(Exception e) {}
			
		}
	}
}
