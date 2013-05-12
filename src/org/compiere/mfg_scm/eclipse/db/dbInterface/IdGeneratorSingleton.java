/*
 * ====================================================================
 * Copyright 2002-2004 The Apache Software Foundation. Parts (c) Copyright
 * 2004-2005 Compiere MFG + SCM.
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

package org.compiere.mfg_scm.eclipse.db.dbInterface;

import java.util.HashMap;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class IdGeneratorSingleton {

	private static IdGeneratorSingleton singleton = new IdGeneratorSingleton();

	private HashMap hmIds;

	private IdGeneratorSingleton() {
		hmIds = new HashMap();
		return;
	}

	private int _getId(String type, String instance) {
		HashMap hmTypeId = (HashMap) hmIds.get(type);
		Integer storedId;
		int id;
		if (hmTypeId == null) {
			hmTypeId = new HashMap();
			hmIds.put(type, hmTypeId);
		}
		storedId = (Integer) hmTypeId.get(instance);
		if (storedId == null)
			id = 0;

		else
			id = storedId.intValue() + 1;

		hmTypeId.put(instance, new Integer(id));
		return (id);
	}

	public static int getId(String type, String instance) {
		return (IdGeneratorSingleton.singleton._getId(type, instance));
	}

}