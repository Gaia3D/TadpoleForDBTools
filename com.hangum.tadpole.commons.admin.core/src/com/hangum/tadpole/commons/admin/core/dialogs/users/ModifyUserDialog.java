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
package com.hangum.tadpole.commons.admin.core.dialogs.users;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hangum.tadpole.commons.admin.core.Activator;
import com.hangum.tadpole.commons.admin.core.Messages;
import com.hangum.tadpole.commons.exception.dialog.ExceptionDetailsErrorDialog;
import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.engine.query.dao.system.UserDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserQuery;
import com.hangum.tadpole.session.manager.SessionManager;
import com.hangum.tadpole.session.manager.SessionManagerListener;

/**
 * admin의 사용자 수정 다이얼로그
 * 
 * @author hangum
 *
 */
public class ModifyUserDialog extends Dialog {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ModifyUserDialog.class);

	/** 사용자 패스워드 초기화 */
	private int BTN_INITIALIZE_PASSWORD = IDialogConstants.CLIENT_ID + 1;
	
	private UserDAO userDAO;
	
	private Text textEmail;
	private Text textName;
	private Text textCreateDate;
	
	private Combo comboIsRegistDB;
	private Combo comboApproval;
	private Combo comboUserConfirm;
	private Combo comboDel;
	private Text textAllowIP;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ModifyUserDialog(Shell parentShell, UserDAO userDAO) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		
		this.userDAO = userDAO;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.get().ModifyUserDialog_0);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		Label lblEmail = new Label(container, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText(Messages.get().ModifyUserDialog_1);
		
		textEmail = new Text(container, SWT.BORDER);
		textEmail.setEditable(false);
		textEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText(Messages.get().ModifyUserDialog_2);
		
		textName = new Text(container, SWT.BORDER);
		textName.setEditable(false);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblAllowIp = new Label(container, SWT.NONE);
		lblAllowIp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAllowIp.setText(Messages.get().ModifyUserDialog_3);
		
		textAllowIP = new Text(container, SWT.BORDER);
		textAllowIP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblIsRegistDb = new Label(container, SWT.NONE);
		lblIsRegistDb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIsRegistDb.setText(Messages.get().ModifyUserDialog_lblIsRegistDb_text);
		
		comboIsRegistDB = new Combo(container, SWT.READ_ONLY);
		comboIsRegistDB.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboIsRegistDB.add("YES"); //$NON-NLS-1$
		comboIsRegistDB.add("NO"); //$NON-NLS-1$
		
		Label lblApproval = new Label(container, SWT.NONE);
		lblApproval.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblApproval.setText(Messages.get().ModifyUserDialog_4);
		
		comboApproval = new Combo(container, SWT.READ_ONLY);
		comboApproval.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboApproval.add("YES"); //$NON-NLS-1$
		comboApproval.add("NO"); //$NON-NLS-1$
		
		Label lblUserConfirm = new Label(container, SWT.NONE);
		lblUserConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUserConfirm.setText(Messages.get().ModifyUserDialog_5);
		
		comboUserConfirm = new Combo(container, SWT.READ_ONLY);
		comboUserConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboUserConfirm.add("YES"); //$NON-NLS-1$
		comboUserConfirm.add("NO"); //$NON-NLS-1$
		
		Label lblDelete = new Label(container, SWT.NONE);
		lblDelete.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDelete.setText(Messages.get().ModifyUserDialog_6);
		
		comboDel = new Combo(container, SWT.READ_ONLY);
		comboDel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboDel.add("YES"); //$NON-NLS-1$
		comboDel.add("NO"); //$NON-NLS-1$
		
		Label lblCreateDate = new Label(container, SWT.NONE);
		lblCreateDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCreateDate.setText(Messages.get().ModifyUserDialog_7);
		
		textCreateDate = new Text(container, SWT.BORDER);
		textCreateDate.setEditable(false);
		textCreateDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		initData();
		
		// google analytic
		AnalyticCaller.track(this.getClass().getName());
				
		return container;
	}
	
	/**
	 * 초기 데이터를 설정 합니다.
	 */
	private void initData() {

		textEmail.setText(userDAO.getEmail());
		textName.setText(userDAO.getName());
		textAllowIP.setText(userDAO.getAllow_ip());
		textCreateDate.setText(userDAO.getCreate_time());
		
		comboIsRegistDB.setText(userDAO.getIs_regist_db());
		comboApproval.setText(userDAO.getApproval_yn());
		comboUserConfirm.setText(userDAO.getIs_email_certification());
		comboDel.setText(userDAO.getDelYn());
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(BTN_INITIALIZE_PASSWORD == buttonId) {
			if(MessageDialog.openConfirm(null, Messages.get().ModifyUserDialog_8, String.format(Messages.get().ModifyUserDialog_9, "tadpole"))) { //$NON-NLS-3$
				userDAO.setPasswd("tadpole"); //$NON-NLS-1$
				try {
					TadpoleSystem_UserQuery.updateUserPassword(userDAO);
					SessionManager.updateSessionAttribute(SessionManager.NAME.LOGIN_PASSWORD.toString(), userDAO.getPasswd());
					
					MessageDialog.openInformation(null, Messages.get().ModifyUserDialog_8, Messages.get().ModifyUserDialog_17);
				} catch(Exception e) {
					logger.error("Changing password", e); //$NON-NLS-1$
					MessageDialog.openError(getShell(), "Error", e.getMessage());			 //$NON-NLS-1$
				}
			}
			
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	@Override
	protected void okPressed() {
		if(MessageDialog.openConfirm(getShell(), Messages.get().ModifyUserDialog_12, Messages.get().ModifyUserDialog_13)) {
			UserDAO user = new UserDAO();
			user.setSeq(userDAO.getSeq());
			user.setAllow_ip(textAllowIP.getText());
			user.setIs_regist_db(comboIsRegistDB.getText());
			user.setApproval_yn(comboApproval.getText());
			user.setIs_email_certification(comboUserConfirm.getText());
			user.setDelYn(comboDel.getText());
			
			// 사용자의 권한을 no로 만들면 session에서 삭제 하도록 합니다.
			if("YES".equals(user.getDelYn()) || "YES".equals(user.getApproval_yn())) { //$NON-NLS-1$ //$NON-NLS-2$
				String sessionId = SessionManagerListener.getSessionIds(user.getEmail());
			}
			
			try {
				TadpoleSystem_UserQuery.updateUserData(user);
			} catch (Exception e) {
				logger.error("data update", e); //$NON-NLS-1$
				
				Status errStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e); //$NON-NLS-1$
				ExceptionDetailsErrorDialog.openError(getShell(), "Error", "User Info update", errStatus); //$NON-NLS-1$ //$NON-NLS-2$
				
				return;
			}
			
			super.okPressed();	
		} else {
			return;
		}		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BTN_INITIALIZE_PASSWORD, Messages.get().ModifyUserDialog_19, false);
		createButton(parent, IDialogConstants.OK_ID, Messages.get().ModifyUserDialog_11, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.get().ModifyUserDialog_14, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(430, 320);
	}

}
