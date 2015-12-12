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
package com.hangum.tadpole.mongodb.erd.core;

import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.rwt.RWT;

public class Messages {
	private static final String BUNDLE_NAME = "com.hangum.tadpole.mongodb.erd.core.messages"; //$NON-NLS-1$
	public String AutoLayoutAction_0;
	public String AutoLayoutAction_1;
	public String AutoLayoutAction_2;
	public String AutoLayoutAction_3;
	public String TableTransferDropTargetListener_1;
	public String TadpoleEditor_0;
	public String TadpoleEditor_1;
	public String TadpoleEditor_10;
	public String TadpoleEditor_11;
	public String TadpoleEditor_12;
	public String TadpoleEditor_13;
	public String TadpoleEditor_14;
	public String TadpoleEditor_15;
	public String TadpoleEditor_2;
	public String TadpoleEditor_3;
	public String TadpoleEditor_4;
	public String TadpoleEditor_9;
	public String TadpoleModelUtils_2;

	// static {
	// NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	// }
	public static Messages get() {
		return RWT.NLS.getISO8859_1Encoded(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
