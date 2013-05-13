/*
 * ====================================================================
 *  * Copyright 2001-2013 Andre Charles Legendre <andre.legendre@kalimasystems.org>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.JarUtil;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class GenerateXmlFiles {

	public GenerateXmlFiles() {
		return;
	}

	public static void actionPerformed(DBMeta dbmeta, Properties properties) {
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile();
		String repositoryDir = dbInterfaceproperties.getUserRepositoryPath();
		String repositoryFileName = repositoryDir
				+ System.getProperty("file.separator") + "repository.xml";
		File repositoryFile = new File(repositoryFileName);
		PrintWriter writerUserRepository;
		JarUtil jarUtil = new JarUtil();
		try {
			PrintWriter writerRepository;
			if (!repositoryFile.exists())
				repositoryFile.createNewFile();

			if (repositoryFile.canWrite()) {
				writerRepository = new PrintWriter(new FileOutputStream(
						repositoryFile));
				writerRepository.println(getRepositoryXML());
				writerRepository.close();
				jarUtil.addFile(repositoryFile);
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate repository.xml " + repositoryFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate repository.xml " + repositoryFileName);
			DbfLauncherPlugin.log(ioexception);
		}
		String databaseRepositoryFileName = repositoryDir
				+ System.getProperty("file.separator")
				+ "repository_database.xml";
		File databaseRepositoryFile = new File(databaseRepositoryFileName);
		try {
			if (!databaseRepositoryFile.exists())
				databaseRepositoryFile.createNewFile();
			if (databaseRepositoryFile.canWrite()) {
				writerUserRepository = new PrintWriter(new FileOutputStream(
						databaseRepositoryFile));
				writerUserRepository.println(makeFileHeader()
						+ getDatabaseRepositoryXML());
				writerUserRepository.close();
				jarUtil.addFile(databaseRepositoryFile);
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate databaseRepository"
							+ databaseRepositoryFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate databaseRepository"
							+ databaseRepositoryFileName);
			DbfLauncherPlugin.log(ioexception);
		}
		String userRepositoryFileName = repositoryDir
				+ System.getProperty("file.separator") + "repository_user.xml";
		File userRepositoryFile = new File(userRepositoryFileName);
		try {
			if (!userRepositoryFile.exists())
				userRepositoryFile.createNewFile();
			if (userRepositoryFile.canWrite()) {
				writerUserRepository = new PrintWriter(new FileOutputStream(
						userRepositoryFile));
				dbmeta.writeXML(writerUserRepository);
				writerUserRepository.close();
				jarUtil.addFile(userRepositoryFile);
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate userRepository" + userRepositoryFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate userRepository" + userRepositoryFileName);
			DbfLauncherPlugin.log(ioexception);
		}
		String ejbRepositoryFileName = repositoryDir
				+ System.getProperty("file.separator") + "repository_ejb.xml";
		File ejbRepositoryFile = new File(ejbRepositoryFileName);
		try {
			if (!ejbRepositoryFile.exists())
				ejbRepositoryFile.createNewFile();
			if (ejbRepositoryFile.canWrite()) {
				writerUserRepository = new PrintWriter(new FileOutputStream(
						ejbRepositoryFile));
				writerUserRepository.println(makeFileHeader()
						+ getEjbRepositoryXML());
				writerUserRepository.close();
				jarUtil.addFile(ejbRepositoryFile);
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate ejbRepository" + ejbRepositoryFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"Fail to generate ejbRepository" + ejbRepositoryFileName);
			DbfLauncherPlugin.log(ioexception);
		}
		String tmpName = properties.getProperty("dbalias");
		int index = tmpName.lastIndexOf('/');
		String databaseName = tmpName.substring(index + 1, tmpName.length());
		String jarDirectory = repositoryDir
				+ System.getProperty("file.separator") + "lib";
		File jarFile = new File(new File(jarDirectory), databaseName + ".jar");
		try {
			jarUtil.createJar(jarFile);
		} catch (IOException e) {
			DbfLauncherPlugin.log(IStatus.ERROR, "Fail to generate jar "
					+ jarDirectory + System.getProperty("file.separator")
					+ databaseName + ".jar");
			DbfLauncherPlugin.log(e);
		}
		return;
	}

	public static String makeFileHeader() {
		String fileHeader = "<!-- This repository file is generated by the compiere MFGSCM DbInterface	-->";
		fileHeader += System.getProperty("line.separator");
		fileHeader += "<!--		www.compiere-mfg.org info@compiere-mfg.org			-->";
		fileHeader += System.getProperty("line.separator");
		Date date = new Date(System.currentTimeMillis());
		fileHeader += "<!--		created at " + date.toGMTString() + "				-->";
		fileHeader += System.getProperty("line.separator");
		fileHeader += System.getProperty("line.separator");
		fileHeader += System.getProperty("line.separator");
		return (fileHeader);
	}

	public static String getRepositoryXML() {
		String repositoryXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- Repository file generated by Compiere MFG + SCM -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- www.compiere-mfg.org info@compiere-mfg.org -->";
		repositoryXML += System.getProperty("line.separator");
		Date date = new Date(System.currentTimeMillis());
		repositoryXML += "<!-- created at " + date.toGMTString() + " -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- defining entities for include-files -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!DOCTYPE descriptor-repository PUBLIC \"-//Apache Software Foundation//DTD OJB Repository//EN\" \"repository.dtd\"";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "[";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!ENTITY database SYSTEM \"repository_database.xml\">";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!ENTITY internal SYSTEM \"repository_internal.xml\">";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!ENTITY user SYSTEM \"repository_user.xml\">";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!ENTITY ejb SYSTEM \"repository_ejb.xml\">";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "]>";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<descriptor-repository version=\"1.0\" isolation-level=\"read-uncommitted\" proxy-prefetching-limit=\"50\">";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- include all used database connections -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += " &database; ";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- include ojb internal mappings here -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += " &internal;";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "<!-- include user defined mappings here -->";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += " &user; ";
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += System.getProperty("line.separator");
		repositoryXML += "</descriptor-repository>";
		repositoryXML += System.getProperty("line.separator");
		return (repositoryXML);
	}

	public static String getDatabaseRepositoryXML() {
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile();
		String databaseRepositoryXML = System.getProperty("line.separator");
		databaseRepositoryXML += "<jdbc-connection-descriptor";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tjcd-alias=\""
				+ dbInterfaceproperties.getJcdAlias() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tdefault-connection=\""
				+ dbInterfaceproperties.isDefaultConnection() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tplatform=\""
				+ dbInterfaceproperties.getPlatform() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tjdbc-level=\""
				+ dbInterfaceproperties.getJdbcLevel() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tdriver=\""
				+ dbInterfaceproperties.getDriver() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tprotocol=\""
				+ dbInterfaceproperties.getProtocol() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tsubprotocol=\""
				+ dbInterfaceproperties.getSubprotocol() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tdbalias=\""
				+ dbInterfaceproperties.getDbalias() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tusername=\""
				+ dbInterfaceproperties.getUsername() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tpassword=\""
				+ dbInterfaceproperties.getPassword() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tbatch-mode=\""
				+ dbInterfaceproperties.isBatchMode() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\teager-release=\""
				+ dbInterfaceproperties.isEagerRelease() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tuseAutoCommit=\""
				+ dbInterfaceproperties.getAutoCommit() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "\tignoreAutoCommitExceptions=\""
				+ dbInterfaceproperties.isAcExceptions() + "\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += ">";
		databaseRepositoryXML += System.getProperty("line.separator");

		// databaseRepositoryXML += " <connection-pool maxActive=\"21\"
		// validationQuery=\"\" />";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<object-cache class=\"org.apache.ojb.broker.cache.ObjectCacheDefaultImpl\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "strategy=\"org.apache.ojb.broker.cache.CacheStrategyDefaultImpl\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += ">";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!-- attribute used by CacheStrategyDefaultImpl -->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"csUseTwoLevel\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!-- attributes used by ObjectCacheDefaultImpl -->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"timeout\" attribute-value=\"900\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"useSoftReference\" attribute-value=\"true\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"maxEntry\" attribute-value=\"-1\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "</object-cache>";
		databaseRepositoryXML += System.getProperty("line.separator");

		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<batch class=\"org.apache.ojb.broker.accesslayer.batch.BatchManagerImpl\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	strategy=\"org.apache.ojb.broker.accesslayer.batch.BatchStrategyDefaultImpl\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "limit=\"50\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += ">";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!-- property use by BatchManagerImpl -->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"includeOptimisticLocking\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "</batch>";
		databaseRepositoryXML += System.getProperty("line.separator");

		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<connection-factory";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	class=\"org.apache.ojb.broker.accesslayer.ConnectionFactoryPooledImpl\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	validationQuery=\"\"";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += ">";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!-- attributes supported by used ConnectionFactory class -->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!--";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "attributes supported by ConnectionFactoryPooledImpl and";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "ConnectionFactoryDBCPImpl";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "-->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"maxActive\" attribute-value=\"30\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"maxIdle\" attribute-value=\"-1\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"maxWait\" attribute-value=\"10000\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"whenExhaustedAction\" attribute-value=\"0\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"testOnBorrow\" attribute-value=\"true\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"testOnReturn\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"testWhileIdle\" attribute-value=\"true\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"timeBetweenEvictionRunsMillis\" attribute-value=\"-1\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"numTestsPerEvictionRun\" attribute-value=\"10\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"minEvictableIdleTimeMillis\" attribute-value=\"600000\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");

		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<!--";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "additional attributes supported by ConnectionFactoryDBCPImpl";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "-->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"logAbandoned\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"removeAbandoned\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "<attribute attribute-name=\"removeAbandonedTimeout\" attribute-value=\"300\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += System.getProperty("line.separator");

		databaseRepositoryXML += "</connection-factory>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += System.getProperty("line.separator");

		databaseRepositoryXML += "<sequence-manager className=\"org.apache.ojb.broker.util.sequence.SequenceManagerHighLowImpl\">";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	<!-- attributes supported by SequenceManagerHighLowImpl -->";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	<attribute attribute-name=\"grabSize\" attribute-value=\"20\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	<attribute attribute-name=\"autoNaming\" attribute-value=\"true\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	<attribute attribute-name=\"globalSequenceId\" attribute-value=\"false\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "	<attribute attribute-name=\"globalSequenceStart\" attribute-value=\"10000\"/>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "</sequence-manager>";
		databaseRepositoryXML += System.getProperty("line.separator");
		databaseRepositoryXML += "</jdbc-connection-descriptor>";
		databaseRepositoryXML += System.getProperty("line.separator");
		return (databaseRepositoryXML);
	}

	public static String getEjbRepositoryXML() {
		String ejbRepositoryXML = System.getProperty("line.separator");
		ejbRepositoryXML += "<!-- The Mappings for the EJB-examples are placed here to make it";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "     easier to find them for OJB newbies.";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "-->";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "<!-- Definitions for org.apache.ojb.ejb.PersonVO";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private Integer personId;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String firstName;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String lastName;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String grade;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "-->";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "   <class-descriptor class=\"org.apache.ojb.ejb.PersonVO\" table=\"EJB_PERSON\" >";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "      <field-descriptor name=\"personId\" column=\"PERSON_ID\" jdbc-type=\"INTEGER\" primarykey=\"true\" autoincrement=\"true\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "      <field-descriptor name=\"firstName\" column=\"FIRST_NAME\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "      <field-descriptor name=\"lastName\" column=\"LAST_NAME\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "      <field-descriptor name=\"grade\" column=\"GRADE\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "   </class-descriptor>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "<!-- Definitions for org.apache.ojb.ejb.ArticleVO";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private Integer articleId;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String name;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private BigDecimal price;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String description;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private Integer categoryId;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private CategoryVO category;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "-->";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    <class-descriptor class=\"org.apache.ojb.ejb.ArticleVO\" table=\"EJB_ARTICLE\" >";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"articleId\" column=\"ARTICLE_ID\" jdbc-type=\"INTEGER\" primarykey=\"true\" autoincrement=\"true\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"name\" column=\"NAME\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"price\" column=\"PRICE\" jdbc-type=\"DECIMAL\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"description\" column=\"DESCRIPTION\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"categoryId\" column=\"CATEGORY_ID\" jdbc-type=\"INTEGER\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <reference-descriptor name=\"category\" class-ref=\"org.apache.ojb.ejb.CategoryVO\" auto-retrieve=\"false\"";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        auto-update=\"false\" auto-delete=\"false\" >";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "            <foreignkey field-ref=\"categoryId\"/>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        </reference-descriptor>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    </class-descriptor>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "<!-- Definitions for org.apache.ojb.ejb.CategoryVO";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private Integer categoryId;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String categoryName;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private String description;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    private Collection assignedArticles;";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "-->";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    <class-descriptor class=\"org.apache.ojb.ejb.CategoryVO\" table=\"EJB_CATEGORY\" >";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"objId\" column=\"CATEGORY_ID\" jdbc-type=\"INTEGER\" primarykey=\"true\" autoincrement=\"true\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"categoryName\" column=\"CATEGORY_NAME\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <field-descriptor name=\"description\" column=\"DESCRIPTION\" jdbc-type=\"VARCHAR\" />";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        <collection-descriptor name=\"assignedArticles\" element-class-ref=\"org.apache.ojb.ejb.ArticleVO\"";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "            auto-retrieve=\"true\" auto-update=\"false\" auto-delete=\"false\" >";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "            <inverse-foreignkey field-ref=\"categoryId\"/>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "        </collection-descriptor>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "    </class-descriptor>";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "";
		ejbRepositoryXML += System.getProperty("line.separator");
		ejbRepositoryXML += "<!-- Mapping of User defined classes ends here -->";
		ejbRepositoryXML += System.getProperty("line.separator");
		return (ejbRepositoryXML);
	}

	private String getXMLReference(DBFKRelation dbfkrelation) {
		String xmlReference = "  <ReferenceDescriptor id=\""
				+ dbfkrelation.getId() + "\">"
				+ System.getProperty("line.separator");
		xmlReference += "    <rdfield.name>" + dbfkrelation.getFieldName()
				+ "</rdfield.name>" + System.getProperty("line.separator");
		xmlReference += "    <referenced.class>"
				+ dbfkrelation.getPKTable().getFQClassName()
				+ "</referenced.class>" + System.getProperty("line.separator");
		xmlReference += "    <fk_descriptor_ids>";
		Iterator it = dbfkrelation.getColumnPairIterator();
		while (it.hasNext()) {
			Object[] objs = (Object[]) it.next();
			DBColumn tmpCol = (DBColumn) objs[1];
			xmlReference += tmpCol.getId() + " ";
		}

		xmlReference += "    </fk_descriptor_ids>"
				+ System.getProperty("line.separator");
		xmlReference += "    <auto.retrieve>" + dbfkrelation.getAutoRetrieve()
				+ "</auto.retrieve>" + System.getProperty("line.separator");
		xmlReference += "    <auto.update>" + dbfkrelation.getAutoUpdate()
				+ "</auto.update>" + System.getProperty("line.separator");
		xmlReference += "    <auto.delete>" + dbfkrelation.getAutoDelete()
				+ "</auto.delete>" + System.getProperty("line.separator");
		xmlReference += "  </ReferenceDescriptor>"
				+ System.getProperty("line.separator");
		return (xmlReference);
	}

	private String getXMLCollection(DBFKRelation dbfkrelation) {
		String xmlCollection = "  <CollectionDescriptor id=\""
				+ dbfkrelation.getId() + "\">"
				+ System.getProperty("line.separator");
		xmlCollection += "    <cdfield.name>" + dbfkrelation.getFieldName()
				+ "</cdfield.name>" + System.getProperty("line.separator");
		xmlCollection += "    <items.class>"
				+ dbfkrelation.getFKTable().getFQClassName() + "</items.class>"
				+ System.getProperty("line.separator");
		xmlCollection += "    <inverse_fk_descriptor_ids>";
		Iterator it = dbfkrelation.getColumnPairIterator();
		while (it.hasNext()) {
			Object[] objs = (Object[]) it.next();
			DBColumn tmpCol = (DBColumn) objs[1];
			xmlCollection += tmpCol.getId() + " ";
		}

		xmlCollection += "    </inverse_fk_descriptor_ids>"
				+ System.getProperty("line.separator");
		xmlCollection += "    <auto.retrieve>" + dbfkrelation.getAutoRetrieve()
				+ "</auto.retrieve>" + System.getProperty("line.separator");
		xmlCollection += "    <auto.update>" + dbfkrelation.getAutoUpdate()
				+ "</auto.update>" + System.getProperty("line.separator");
		xmlCollection += "    <auto.delete>" + dbfkrelation.getAutoDelete()
				+ "</auto.delete>" + System.getProperty("line.separator");
		xmlCollection += "  </CollectionDescriptor>"
				+ System.getProperty("line.separator");
		return (xmlCollection);
	}

}
