/*******************************************************************************
 * Copyright (c) 2016 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     nilrir - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.viewers.object.sub.agens.edge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;

import com.hangum.tadpole.commons.exception.dialog.ExceptionDetailsErrorDialog;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine.OBJECT_TYPE;
import com.hangum.tadpole.commons.libs.core.message.CommonMessages;
import com.hangum.tadpole.engine.manager.TadpoleSQLManager;
import com.hangum.tadpole.engine.query.dao.agens.AgensVertexDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.engine.sql.util.tables.TableUtil;
import com.hangum.tadpole.rdb.core.Activator;
import com.hangum.tadpole.rdb.core.Messages;
import com.hangum.tadpole.rdb.core.actions.object.AbstractObjectAction;
import com.hangum.tadpole.rdb.core.actions.object.rdb.object.ObjectRefreshAction;
import com.hangum.tadpole.rdb.core.editors.dbinfos.composites.ColumnHeaderCreator;
import com.hangum.tadpole.rdb.core.editors.dbinfos.composites.DefaultLabelProvider;
import com.hangum.tadpole.rdb.core.editors.dbinfos.composites.DefaultTableColumnFilter;
import com.hangum.tadpole.rdb.core.editors.dbinfos.composites.TableViewColumnDefine;
import com.hangum.tadpole.rdb.core.viewers.object.sub.AbstractObjectComposite;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * Agens Graph edge composite
 * 
 * @author hangum
 * 
 */
public class TadpoleEdgeComposite extends AbstractObjectComposite {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TadpoleEdgeComposite.class);

	private CTabItem tbtmEdge;

	// table info
	private TableViewer edgeListViewer;
	private List<AgensVertexDAO> showEdge = new ArrayList<AgensVertexDAO>();
	private DefaultTableColumnFilter edgeFilter;

	private AbstractObjectAction refreshAction_Edge;

	/**
	 * Create the composite.
	 * 
	 * @param partSite
	 * @param parent
	 * @param userDB
	 */
	public TadpoleEdgeComposite(IWorkbenchPartSite partSite, final CTabFolder tabFolderObject, UserDBDAO userDB) {
		super(partSite, tabFolderObject, userDB);

		createWidget(tabFolderObject);
	}

	private void createWidget(final CTabFolder tabFolderObject) {
		tbtmEdge = new CTabItem(tabFolderObject, SWT.NONE);
		tbtmEdge.setText("Edge");
		tbtmEdge.setData(TAB_DATA_KEY, PublicTadpoleDefine.OBJECT_TYPE.EDGE.name());

		Composite compositeTables = new Composite(tabFolderObject, SWT.NONE);
		tbtmEdge.setControl(compositeTables);
		GridLayout gl_compositeTables = new GridLayout(1, false);
		gl_compositeTables.verticalSpacing = 2;
		gl_compositeTables.horizontalSpacing = 2;
		gl_compositeTables.marginHeight = 2;
		gl_compositeTables.marginWidth = 2;
		compositeTables.setLayout(gl_compositeTables);
		compositeTables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SashForm sashForm = new SashForm(compositeTables, SWT.NONE);
		sashForm.setOrientation(SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// SWT.VIRTUAL 일 경우 FILTER를 적용하면 데이터가 보이지 않는 오류수정.
		edgeListViewer = new TableViewer(sashForm, /* SWT.VIRTUAL | */ SWT.BORDER | SWT.FULL_SELECTION);
//		vertexListViewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				try {
//					IStructuredSelection is = (IStructuredSelection) event.getSelection();
//					if (null != is) {
//						OracleSequenceDAO sequenceDAO = (OracleSequenceDAO) is.getFirstElement();
//						FindEditorAndWriteQueryUtil.run(userDB, "SELECT " + sequenceDAO.getFullName() + ".NEXTVAL FROM DUAL;" , PublicTadpoleDefine.OBJECT_TYPE.SEQUENCE);
//					}
//				} catch (Exception e) {
//					logger.error("create sequence", e);
//				}
//			}
//		});

		Table tableTableList = edgeListViewer.getTable();
		tableTableList.setLinesVisible(true);
		tableTableList.setHeaderVisible(true);

		createSequenceMenu();
		createSequenceListColumns();

		edgeListViewer.setInput(showEdge);
		edgeListViewer.refresh();

		edgeFilter = new DefaultTableColumnFilter();
		edgeListViewer.addFilter(edgeFilter);
	}

	/** create column */
	private void createSequenceListColumns() {
		TableViewColumnDefine[] tableColumnDef = new TableViewColumnDefine[] { //
		new TableViewColumnDefine("LABNAME", CommonMessages.get().Name, 100, SWT.LEFT) // //$NON-NLS-1$
				, new TableViewColumnDefine("RELID", "RELID", 80, SWT.RIGHT) // //$NON-NLS-1$
				, new TableViewColumnDefine("LABOWNER", "LABOWNER", 80, SWT.RIGHT) // //$NON-NLS-1$
				, new TableViewColumnDefine("LABKIND", "LABKIND", 40, SWT.RIGHT) // //$NON-NLS-1$
				, new TableViewColumnDefine("INHRELID", "INHRELID", 40, SWT.CENTER) // //$NON-NLS-1$
				, new TableViewColumnDefine("INHPARENT", "INHPARENT", 40, SWT.RIGHT) // //$NON-NLS-1$
				, new TableViewColumnDefine("INHSEQNO", "INHSEQNO", 80, SWT.RIGHT) // //$NON-NLS-1$
		};

		ColumnHeaderCreator.createColumnHeader(edgeListViewer, tableColumnDef);

		edgeListViewer.setContentProvider(new ArrayContentProvider());
		edgeListViewer.setLabelProvider(new DefaultLabelProvider(edgeListViewer));
	}

	/**
	 * create Table menu
	 */
	private void createSequenceMenu() {
		if(getUserDB() == null) return;
		
		refreshAction_Edge = new ObjectRefreshAction(getSite().getWorkbenchWindow(), PublicTadpoleDefine.OBJECT_TYPE.EDGE, CommonMessages.get().Refresh); //$NON-NLS-1$

		final MenuManager menuMgr = new MenuManager("#PopupMenu", "Vertex"); //$NON-NLS-1$ //$NON-NLS-2$
		menuMgr.add(refreshAction_Edge);
		edgeListViewer.getTable().setMenu(menuMgr.createContextMenu(edgeListViewer.getTable()));
		getSite().registerContextMenu(menuMgr, edgeListViewer);
	}

	/**
	 * 정보를 최신으로 리프레쉬합니다.
	 * @param strObjectName 
	 */
	public void refreshSequence(final UserDBDAO selectUserDb, final boolean boolRefresh, final String strObjectName) {
		if (!boolRefresh) if (!showEdge.isEmpty()) return;
		this.userDB = selectUserDb;

		showEdge = (List<AgensVertexDAO>)selectUserDb.getDBObject(OBJECT_TYPE.EDGE, selectUserDb.getDefaultSchemanName());
		if(!(showEdge == null || showEdge.isEmpty())) {
			edgeListViewer.setInput(showEdge);
			edgeListViewer.refresh();
			TableUtil.packTable(edgeListViewer.getTable());

			// select tabitem
			getTabFolderObject().setSelection(tbtmEdge);
			
			selectDataOfTable(strObjectName);
		} else {
			Job job = new Job(Messages.get().MainEditor_45) {
				@Override
				public IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(MSG_DataIsBeginAcquired, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
	
					try {
						showEdge = getVertexList(userDB);

						// set push of cache
						userDB.setDBObject(OBJECT_TYPE.EDGE, userDB.getDefaultSchemanName(), showEdge);
					} catch (Exception e) {
						logger.error("EDGE Referesh", e); //$NON-NLS-1$
	
						return new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage());
					} finally {
						monitor.done();
					}
	
					return Status.OK_STATUS;
				}
			};
	
			job.addJobChangeListener(new JobChangeAdapter() {
	
				public void done(IJobChangeEvent event) {
					final IJobChangeEvent jobEvent = event;
	
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (jobEvent.getResult().isOK()) {
								edgeListViewer.setInput(showEdge);
								edgeListViewer.refresh();
								TableUtil.packTable(edgeListViewer.getTable());
	
								// select tabitem
								getTabFolderObject().setSelection(tbtmEdge);
								
								selectDataOfTable(strObjectName);
							} else {
								if (showEdge != null) showEdge.clear();
								edgeListViewer.setInput(showEdge);
								edgeListViewer.refresh();
								TableUtil.packTable(edgeListViewer.getTable());
	
								Status errStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, jobEvent.getResult().getMessage(), jobEvent.getResult().getException()); //$NON-NLS-1$
								ExceptionDetailsErrorDialog.openError(null,CommonMessages.get().Error, Messages.get().ExplorerViewer_86, errStatus); //$NON-NLS-1$
							}
						}
					}); // end display.asyncExec
				} // end done
	
			}); // end job
	
			job.setName(userDB.getDisplay_name());
			job.setUser(true);
			job.schedule();
		}
	}

	/**
	 * 보여 주어야할 목록을 정의합니다.
	 * 
	 * @param userDB
	 * @return
	 * @throws Exception
	 */
	public static List<AgensVertexDAO> getVertexList(final UserDBDAO userDB) throws Exception {
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(userDB);
		return sqlClient.queryForList("agensEdge", userDB.getSchema()); //$NON-NLS-1$
	}

	/**
	 * initialize action
	 */
	public void initAction() {
		if(getUserDB() == null) return; 
		refreshAction_Edge.setUserDB(getUserDB());
	}

	/**
	 * get sequenceViewer
	 * 
	 * @return
	 */
	public TableViewer getTableviewer() {
		return edgeListViewer;
	}

	/**
	 * initialize filter text
	 * 
	 * @param textSearch
	 */
	public void filter(String textSearch) {
		edgeFilter.setSearchString(textSearch);
		edgeListViewer.refresh();
	}

	@Override
	public void dispose() {
		super.dispose();	
		if(refreshAction_Edge != null) refreshAction_Edge.dispose();
	}

	@Override
	public void setSearchText(String searchText) {
		edgeFilter.setSearchString(searchText);
	}

	@Override
	public void selectDataOfTable(String strObjectName) {
		if("".equals(strObjectName) || strObjectName == null) return;
		
		getTableviewer().getTable().setFocus();
		
		// find select object and viewer select
		for(int i=0; i< this.showEdge.size(); i++) {
			AgensVertexDAO sequenceDao = (AgensVertexDAO)getTableviewer().getElementAt(i);
			if(StringUtils.equalsIgnoreCase(strObjectName, sequenceDao.getLabname())) {
				getTableviewer().setSelection(new StructuredSelection(getTableviewer().getElementAt(i)), true);
				break;
			}
		}
	}
}
