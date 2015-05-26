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
package com.hangum.tadpole.engine.query.dao.system;

import java.sql.Timestamp;

/** 
 * user db resource data
 * 
 * @author hangum
 *
 */
public class UserDBResourceDataDAO {
	int seq;
	long group_seq;  
	int user_db_resource_seq;
	String datas;
	String delyn;
	Timestamp create_time;
	
	public UserDBResourceDataDAO() {
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getUser_db_resource_seq() {
		return user_db_resource_seq;
	}

	public void setUser_db_resource_seq(int user_db_resource_seq) {
		this.user_db_resource_seq = user_db_resource_seq;
	}

	public String getDatas() {
		return datas;
	}

	public void setDatas(String datas) {
		this.datas = datas;
	}
	/**
	 * @return the group_seq
	 */
	public long getGroup_seq() {
		return group_seq;
	}

	/**
	 * @param group_seq the group_seq to set
	 */
	public void setGroup_seq(long group_seq) {
		this.group_seq = group_seq;
	}

	/**
	 * @return the delyn
	 */
	public String getDelyn() {
		return delyn;
	}

	/**
	 * @param delyn the delyn to set
	 */
	public void setDelyn(String delyn) {
		this.delyn = delyn;
	}

	/**
	 * @return the create_time
	 */
	public Timestamp getCreate_time() {
		return create_time;
	}

	/**
	 * @param create_time the create_time to set
	 */
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	
}
