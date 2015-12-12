/*******************************************************************************
 * Copyright (c) 2014 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.dialog.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.commons.util.GlobalImageUtils;
import com.hangum.tadpole.commons.util.TadpoleWidgetUtils;
import com.hangum.tadpole.engine.define.DBDefine;
import com.hangum.tadpole.engine.query.dao.mysql.TableDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.rdb.core.dialog.table.DataTypeDef.DATA_TYPE;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Table 수정 다이얼로그.
 * 
 * @author hangum
 *
 */
public class AlterTableDialog extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -717632913094073217L;

	private List<AlterTableMetaDataDAO> listAlterTableColumns = new ArrayList<AlterTableMetaDataDAO>();
	private List<AlterTableMetaDataDAO> listRemove = new ArrayList<>();

	TableViewer tableViewer;
	
	private UserDBDAO userDB;
	private TableDAO tableDao;
	private Text textTableName;
	private Text textComment;

	private AlterTableExecutor executor;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public AlterTableDialog(Shell parentShell, final UserDBDAO userDB) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.MAX | SWT.RESIZE | SWT.TITLE);

		this.userDB = userDB;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AlterTableDialog(Shell parentShell, final UserDBDAO userDB, TableDAO tableDao) {
		super(parentShell);
		
		this.userDB = userDB;
		this.tableDao = tableDao;
//		this.schemaName = "".equals(this.tableDao.getSchema_name()) ? userDB.getUsers() : this.tableDao.getSchema_name();
//		this.tableName = "".equals(this.tableDao.getTable_name()) ? this.tableDao.getName() : this.tableDao.getTable_name();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Alter Table");
		newShell.setImage(GlobalImageUtils.getTadpoleIcon());
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.makeColumnsEqualWidth = true;
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.widthHint = 403;
		composite.setLayoutData(gd_composite);
		
		Label lblTableName = new Label(composite, SWT.NONE);
		lblTableName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTableName.setText("Table Name");
		
		textTableName = new Text(composite, SWT.BORDER);
		textTableName.setText(tableDao.getName());
		textTableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblComment = new Label(composite, SWT.NONE);
		lblComment.setText("Comment");
		
		textComment = new Text(composite, SWT.BORDER | SWT.MULTI);
		GridData gd_textComment = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textComment.heightHint = 30;
		textComment.setLayoutData(gd_textComment);
		textComment.setText(tableDao.getComment());
		
		CTabFolder tabFolder = new CTabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setSelectionBackground(TadpoleWidgetUtils.getTabFolderBackgroundColor(), TadpoleWidgetUtils.getTabFolderPercents());
		
		CTabItem tbtmColumns = new CTabItem(tabFolder, SWT.NONE);
		tbtmColumns.setText("Columns");
		
	    tableViewer = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tbtmColumns.setControl(table);
		
		executor = new AlterTableExecutor(this.getParentShell(), listAlterTableColumns, userDB);
		this.listAlterTableColumns = executor.Initializing(tableDao.getName());

		createTaleColumn();
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new AlterTableLabelProvider());

		tableViewer.setInput(listAlterTableColumns);
		tableViewer.refresh();		
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginWidth = 2;
		gl_composite_1.marginHeight = 2;
		gl_composite_1.horizontalSpacing = 2;
		gl_composite_1.verticalSpacing = 2;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnAddColumn = new Button(composite_1, SWT.NONE);
		btnAddColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AlterTableMetaDataDAO addColumn = new AlterTableMetaDataDAO();
				addColumn.setDbdef(DBDefine.MYSQL_DEFAULT);
				addColumn.setColumnName("new_column");
				addColumn.setDataType(DataTypeDef.INTEGER);
				addColumn.setDataStatus(DATA_TYPE.INSERT);
				
				listAlterTableColumns.add(addColumn);
				tableViewer.add(addColumn);
			}
		});
		btnAddColumn.setText("Add Column");
		
		Button btnDeleteColumn = new Button(composite_1, SWT.NONE);
		btnDeleteColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection)tableViewer.getSelection();
				if(ss.isEmpty()) return;
				
				AlterTableMetaDataDAO addColumn = (AlterTableMetaDataDAO)ss.getFirstElement();
				listAlterTableColumns.remove(addColumn);
				tableViewer.remove(addColumn);
			}
		});
		btnDeleteColumn.setText("Delete Column");
		tabFolder.setSelection(0);
		
		AnalyticCaller.track(this.getClass().getName());

		return container;
	}

	/**
	 * create column
	 */
	private void createTaleColumn() {

		for (int i = 0; i < AlterTableConsts.sizes.length; i++) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tblclmnColumnName = tableViewerColumn.getColumn();
			tblclmnColumnName.setWidth(AlterTableConsts.sizes[i]);
			tblclmnColumnName.setText(AlterTableConsts.names[i]);

			tableViewerColumn.setEditingSupport(new AlterTableEditingSupport(tableViewer, i));
		}
	}


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "CANCEL", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(700, 600);
	}
}

