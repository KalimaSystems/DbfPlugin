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

import java.sql.Types;
import java.util.Collections;
import java.util.Properties;
import java.util.TreeSet;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class Utilities {

	public static final java.util.Map hmJDBCTypeToName = new java.util.HashMap();

	public static final java.util.Map mJDBCNameToType = new java.util.TreeMap();

	public static final java.util.Vector vJDBCTypeNames = new java.util.Vector();

	public static final java.util.HashMap hmJDBCTypeToJavaType = new java.util.HashMap();

	public static final java.util.Vector vJavaTypes = new java.util.Vector();

	static {
		hmJDBCTypeToName.put(new Integer(Types.ARRAY), "ARRAY");
		hmJDBCTypeToName.put(new Integer(Types.BIGINT), "BIGINT");
		hmJDBCTypeToName.put(new Integer(Types.BINARY), "BINARY");
		hmJDBCTypeToName.put(new Integer(Types.BIT), "BIT");
		hmJDBCTypeToName.put(new Integer(Types.BLOB), "BLOB");
		hmJDBCTypeToName.put(new Integer(Types.CHAR), "CHAR");
		hmJDBCTypeToName.put(new Integer(Types.CLOB), "CLOB");
		hmJDBCTypeToName.put(new Integer(Types.DATE), "DATE");
		hmJDBCTypeToName.put(new Integer(Types.DECIMAL), "DECIMAL");
		hmJDBCTypeToName.put(new Integer(Types.DISTINCT), "DISTINCT");
		hmJDBCTypeToName.put(new Integer(Types.DOUBLE), "DOUBLE");
		hmJDBCTypeToName.put(new Integer(Types.FLOAT), "FLOAT");
		hmJDBCTypeToName.put(new Integer(Types.INTEGER), "INTEGER");
		hmJDBCTypeToName.put(new Integer(Types.JAVA_OBJECT), "OBJECT");
		hmJDBCTypeToName.put(new Integer(Types.LONGVARBINARY), "LONGVARBINARY");
		hmJDBCTypeToName.put(new Integer(Types.LONGVARCHAR), "LONGVARCHAR");
		hmJDBCTypeToName.put(new Integer(Types.NULL), "NULL");
		hmJDBCTypeToName.put(new Integer(Types.NUMERIC), "NUMERIC");
		hmJDBCTypeToName.put(new Integer(Types.OTHER), "OTHER");
		hmJDBCTypeToName.put(new Integer(Types.REAL), "REAL");
		hmJDBCTypeToName.put(new Integer(Types.REF), "REF");
		hmJDBCTypeToName.put(new Integer(Types.SMALLINT), "SMALLINT");
		hmJDBCTypeToName.put(new Integer(Types.STRUCT), "STRUCT");
		hmJDBCTypeToName.put(new Integer(Types.TIME), "TIME");
		hmJDBCTypeToName.put(new Integer(Types.TIMESTAMP), "TIMESTAMP");
		hmJDBCTypeToName.put(new Integer(Types.TINYINT), "TINYINT");
		hmJDBCTypeToName.put(new Integer(Types.VARBINARY), "VARBINARY");
		hmJDBCTypeToName.put(new Integer(Types.VARCHAR), "VARCHAR");
		Properties properties = null;
		/*
		 * while (it.hasNext()) { Map.Entry aEntry = (Map.Entry) it.next();
		 * mJDBCNameToType.put(aEntry.getValue(), aEntry.getKey()); } try {
		 * properties = new Properties(); properties.load(new FileInputStream(
		 * DbInterfaceProperties.m_PropertieName)); } catch (IOException
		 * ioexception) { }
		 */
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile();
		properties = DbInterfaceProperties.getPropertie(dbInterfaceproperties);
		if (properties == null || properties.size() < 2) {
			hmJDBCTypeToJavaType.put(new Integer(Types.ARRAY), "Object[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.BIGINT), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.BINARY), "byte[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.BIT), "Byte");
			hmJDBCTypeToJavaType.put(new Integer(Types.BLOB), "byte[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.CHAR), "String");
			hmJDBCTypeToJavaType.put(new Integer(Types.CLOB), "String");
			hmJDBCTypeToJavaType.put(new Integer(Types.DATE), "java.sql.Date");
			hmJDBCTypeToJavaType.put(new Integer(Types.DECIMAL), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.DISTINCT), "????");
			hmJDBCTypeToJavaType.put(new Integer(Types.DOUBLE), "Double");
			hmJDBCTypeToJavaType.put(new Integer(Types.FLOAT), "Double");
			hmJDBCTypeToJavaType.put(new Integer(Types.INTEGER), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.JAVA_OBJECT), "Object");
			hmJDBCTypeToJavaType
					.put(new Integer(Types.LONGVARBINARY), "byte[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.LONGVARCHAR), "byte[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.NULL), "Object");
			hmJDBCTypeToJavaType.put(new Integer(Types.NUMERIC), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.OTHER), "Object");
			hmJDBCTypeToJavaType.put(new Integer(Types.REAL), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.REF), "Object");
			hmJDBCTypeToJavaType.put(new Integer(Types.SMALLINT), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.STRUCT), "Object");
			hmJDBCTypeToJavaType.put(new Integer(Types.TIME), "java.sql.Time");
			hmJDBCTypeToJavaType.put(new Integer(Types.TIMESTAMP),
					"java.sql.Timestamp");
			hmJDBCTypeToJavaType.put(new Integer(Types.TINYINT), "Long");
			hmJDBCTypeToJavaType.put(new Integer(Types.VARBINARY), "byte[]");
			hmJDBCTypeToJavaType.put(new Integer(Types.VARCHAR), "String");
		} else {
			hmJDBCTypeToJavaType.put(new Integer(Types.ARRAY), properties
					.getProperty("JavaTypes.ARRAY", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.BIGINT), properties
					.getProperty("JavaTypes.BIGINT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.BINARY), properties
					.getProperty("JavaTypes.BINARY", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.BIT), properties
					.getProperty("JavaTypes.BIT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.BLOB), properties
					.getProperty("JavaTypes.BLOB", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.CHAR), properties
					.getProperty("JavaTypes.CHAR", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.CLOB), properties
					.getProperty("JavaTypes.CLOB", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.DATE), properties
					.getProperty("JavaTypes.DATE", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.DECIMAL), properties
					.getProperty("JavaTypes.DECIMAL", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.DISTINCT), properties
					.getProperty("JavaTypes.DISTINCT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.DOUBLE), properties
					.getProperty("JavaTypes.DOUBLE", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.FLOAT), properties
					.getProperty("JavaTypes.FLOAT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.INTEGER), properties
					.getProperty("JavaTypes.INTEGER", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.JAVA_OBJECT), properties
					.getProperty("JavaTypes.JAVA_OBJECT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.LONGVARBINARY),
					properties.getProperty("JavaTypes.LONGVARBINARY", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.LONGVARCHAR), properties
					.getProperty("JavaTypes.LONGVARCHAR", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.NULL), properties
					.getProperty("JavaTypes.NULL", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.NUMERIC), properties
					.getProperty("JavaTypes.NUMERIC", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.OTHER), properties
					.getProperty("JavaTypes.OTHER", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.REAL), properties
					.getProperty("JavaTypes.REAL", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.REF), properties
					.getProperty("JavaTypes.REF", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.SMALLINT), properties
					.getProperty("JavaTypes.SMALLINT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.STRUCT), properties
					.getProperty("JavaTypes.STRUCT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.TIME), properties
					.getProperty("JavaTypes.TIME", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.TIMESTAMP), properties
					.getProperty("JavaTypes.TIMESTAMP", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.TINYINT), properties
					.getProperty("JavaTypes.TINYINT", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.VARBINARY), properties
					.getProperty("JavaTypes.VARBINARY", ""));
			hmJDBCTypeToJavaType.put(new Integer(Types.VARCHAR), properties
					.getProperty("JavaTypes.VARCHAR", ""));
		}
		vJavaTypes.addAll(new TreeSet(hmJDBCTypeToJavaType.values()));
		Collections.sort(vJavaTypes);
		vJDBCTypeNames.addAll(new TreeSet(hmJDBCTypeToName.values()));
		Collections.sort(vJDBCTypeNames);
	}

	private Utilities() {
	}

	public static String getTypeNameFromJDBCType(int jdbcType) {
		return ((String) hmJDBCTypeToName.get(new Integer(jdbcType)));
	}

}