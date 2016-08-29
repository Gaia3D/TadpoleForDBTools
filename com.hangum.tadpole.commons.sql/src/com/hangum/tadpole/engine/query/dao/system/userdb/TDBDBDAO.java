/*******************************************************************************
 * Copyright (c) 2015 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.query.dao.system.userdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine.OBJECT_TYPE;

/**
 * Define Tadpole DB Hub db.
 * 
 * @author hangum
 *
 */
public class TDBDBDAO extends ManagerListViewDAO {
	/** This variable is schema assist */
	protected String schemaListSeparator = "";
	/** This variable is content assist */
	protected String tableListSeparator = null;
	/** This variable is content assit */
	protected String viewListSeparator = null;
	protected String functionLisstSeparator = null;
	
	/** 
	 * db object cache 
	 *
	 * {@code PublicTadpoleDefine#OBJECT_TYPE}, <SchemaName, Object> 
	 */
	protected Map<OBJECT_TYPE, Map<String, List<?>>> dbObjectCache= new HashMap<OBJECT_TYPE, Map<String, List<?>>>(); 
	
	public TDBDBDAO() {
	}
	
	/**
	 * @return the schemaListSeparator
	 */
	public String getSchemaListSeparator() {
		return schemaListSeparator;
	}

	/**
	 * @param schemaListSeparator the schemaListSeparator to set
	 */
	public void setSchemaListSeparator(String schemaListSeparator) {
		this.schemaListSeparator = schemaListSeparator;
	}

	/**
	 * @return the viewListSeparator
	 */
	public String getViewListSeparator() {
		return viewListSeparator;
	}

	/**
	 * @param viewListSeparator the viewListSeparator to set
	 */
	public void setViewListSeparator(String viewListSeparator) {
		this.viewListSeparator = viewListSeparator;
	}

	/**
	 * @return the tableListSeparator
	 */
	public String getTableListSeparator() {
		return tableListSeparator;
	}

	/**
	 * @param tableListSeparator the tableListSeparator to set
	 */
	public void setTableListSeparator(String tableListSeparator) {
		this.tableListSeparator = tableListSeparator;
	}
	
	/**
	 * get db object
	 * 
	 * @param objecType
	 * @param schemaName
	 * @return
	 */
	public List<?> getDBObject(OBJECT_TYPE objecType, String schemaName) {
		Map<String, List<?>> mapDBObject = dbObjectCache.get(objecType);
		if(mapDBObject == null) {
			return new ArrayList();
		} else {
			List listObject = mapDBObject.get(schemaName);
			if(listObject == null) return new ArrayList();
			else return listObject;
		}
	}
	
	/**
	 * set db object
	 * 
	 * @param objecType
	 * @param schemaName
	 * @param objectList
	 */
	public void setDBObject(OBJECT_TYPE objecType, String schemaName, List objectList) {
		Map<String, List<?>> mapDBObject = dbObjectCache.get(objecType);
		if(mapDBObject == null) {
			Map<String, List<?>> map = new HashMap<String, List<?>>();
			map.put(schemaName, objectList);
		
			dbObjectCache.put(objecType, map);
		} else {
			mapDBObject.put(schemaName, objectList);
		}
	}

	/**
	 * @return the functionLisstSeparator
	 */
	public String getFunctionLisstSeparator() {
		return functionLisstSeparator;
	}

	/**
	 * @param functionLisstSeparator the functionLisstSeparator to set
	 */
	public void setFunctionLisstSeparator(String functionLisstSeparator) {
		this.functionLisstSeparator = functionLisstSeparator;
	}
	
}
