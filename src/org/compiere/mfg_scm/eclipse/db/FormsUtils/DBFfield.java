/*
 * ====================================================================
 * Copyright 2001-2005 Compiere MFG + SCM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ====================================================================
 */

package org.compiere.mfg_scm.eclipse.db.FormsUtils;

import java.rmi.RemoteException;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public interface DBFfield {

	// ==============================================================================

	public String getName() throws RemoteException;

	public abstract void setName(String name);

	// ==============================================================================

	public int getId() throws RemoteException;

	public abstract void setId(int id);

	// ==============================================================================

	public int getType() throws RemoteException;

	public abstract void setType(int type);

	// ==============================================================================

	public boolean getPrKey() throws RemoteException;

	public abstract void setPrKey(boolean prKey);

	// ==============================================================================

	public boolean getUnq() throws RemoteException;

	public abstract void setUnq(boolean unq);

	// ==============================================================================

	public boolean getNotN() throws RemoteException;

	public abstract void setNotN(boolean notN);

	// ==============================================================================

	public boolean getMUnq() throws RemoteException;

	public abstract void setMUnq(boolean mUnq);

	// ==============================================================================

	public String getDef() throws RemoteException;

	public abstract void setDef(String def);

	// ==============================================================================

	public boolean getIdx1() throws RemoteException;

	public abstract void setIdx1(boolean notN);

	// ==============================================================================

	public boolean getIndex_U() throws RemoteException;

	public abstract void setIndex_U(boolean INDEX_U);

	// ==============================================================================

	public String getComment() throws RemoteException;

	public abstract void setComment(String comment);

	// ==============================================================================

	public int getRefTable() throws RemoteException;

	public abstract void setRefTable(int refTable);

	// ==============================================================================

	public int getRefField() throws RemoteException;

	public abstract void setRefField(int refField);

	// ==============================================================================

	public boolean OnDelete() throws RemoteException;

	public abstract void setOnDelete(boolean onDelete);

	// ==============================================================================

	public boolean OnUpdate() throws RemoteException;

	public abstract void setOnUpdate(boolean onUpdate);

	// ==============================================================================

	// public abstract void addField(Object fieldType, ){

	// }

}
