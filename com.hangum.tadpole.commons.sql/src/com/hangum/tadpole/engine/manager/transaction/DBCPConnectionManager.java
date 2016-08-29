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
package com.hangum.tadpole.engine.manager.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.hangum.tadpole.cipher.core.manager.CipherManager;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.engine.define.DBDefine;
import com.hangum.tadpole.engine.manager.TadpoleSQLTransactionManager;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;

/**
 * DBCP connection manager
 * 
 * @author hangum
 *
 */
public class DBCPConnectionManager {
	private static final Logger logger = Logger.getLogger(DBCPConnectionManager.class);
	
	public static DBCPConnectionManager instance = new DBCPConnectionManager();
	private Map<String, DataSource> mapDataSource = new ConcurrentHashMap<String, DataSource>();
	private Map<String, GenericObjectPool> mapGenericObject = new ConcurrentHashMap<String, GenericObjectPool>();
	
	private DBCPConnectionManager() {}
	
	public static DBCPConnectionManager getInstance() {
		return instance;
	}
	
	private DataSource makePool(final String userId, UserDBDAO userDB) {
		String searchKey = TadpoleSQLTransactionManager.getKey(userId, userDB);
		
		//
		GenericObjectPool connectionPool = new GenericObjectPool();
		connectionPool.setMaxActive(5);
		connectionPool.setWhenExhaustedAction((byte)1);
		connectionPool.setMaxWait(1000 * 60); 						// 1분대기.
		connectionPool.setTimeBetweenEvictionRunsMillis(10 * 1000);	// 10분한한번 테스트
		connectionPool.setTestWhileIdle(true);
		
		String passwdDecrypt = "";
		try {
			passwdDecrypt = CipherManager.getInstance().decryption(userDB.getPasswd());
		} catch(Exception e) {
			passwdDecrypt = userDB.getPasswd();
		}
		
		ConnectionFactory cf = new DriverManagerConnectionFactory(userDB.getUrl(), userDB.getUsers(), passwdDecrypt);
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, connectionPool, null, userDB.getDBDefine().getValidateQuery(), false, false);
		
		if(!"".equals(PublicTadpoleDefine.CERT_USER_INFO)) {
			// initialize connection string
			List<String> listInitializeSql = new ArrayList<String>();
			String strFullHelloSQL = String.format(PublicTadpoleDefine.CERT_USER_INFO, userDB.getTdbLogingIP(), userDB.getTdbUserID()) + "\n " + userDB.getDBDefine().getValidateQuery();
			if(logger.isInfoEnabled()) logger.info(strFullHelloSQL);
			
			pcf.setValidationQuery(userDB.getDBDefine().getValidateQuery());
			listInitializeSql.add(strFullHelloSQL);
			
			if(userDB.getDBDefine() == DBDefine.ORACLE_DEFAULT) {
				listInitializeSql.add(String.format("CALL DBMS_APPLICATION_INFO.SET_MODULE('Tadpole Hub-Transaction(%s)', '')", userDB.getTdbUserID()));
			}
			if(!listInitializeSql.isEmpty()) pcf.setConnectionInitSql(listInitializeSql);
		}
		
		// setting poolable connection factory
		DataSource ds = new PoolingDataSource(connectionPool);
		mapDataSource.put(searchKey, ds);
		mapGenericObject.put(searchKey, connectionPool);
		
		return ds;
	}
	
	public DataSource makeDataSource(final String userId, final UserDBDAO userDB) {
		DataSource retDataSource = mapDataSource.get(TadpoleSQLTransactionManager.getKey(userId, userDB));
		if(retDataSource == null) { 
			return makePool(userId, userDB);
		}
		
		return retDataSource;
	}
	
	public DataSource getDataSource(final String searchKey) {
		return mapDataSource.get(searchKey);
	}
	
	public void releaseConnectionPool(final String searchKey) {
		GenericObjectPool connectionPool = mapGenericObject.get(searchKey);
		try {
			if(connectionPool != null) {
				connectionPool.clear();
				connectionPool.close();
				
				mapDataSource.remove(searchKey);
				mapGenericObject.remove(searchKey);
			}
			
		} catch(Exception e) {
			logger.error(String.format("**** release connection key is %s", searchKey), e);
		}
	}
	
	/**
	 * map의 카를 가져옵니다.
	 * @param userDB
	 * @return
	 */
	private static String getPoolKey(final String userId, final UserDBDAO userDB) {
		return userId + userDB.getSeq() + userDB.getDisplay_name();
	}

}
