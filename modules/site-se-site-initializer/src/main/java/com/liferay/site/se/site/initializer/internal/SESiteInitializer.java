/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.site.se.site.initializer.internal;


import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.exception.InitializationException;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.se.site.initializer.internal.util.ImagesImporterUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author evanthibodeau
 */
@Component(
	immediate = true,
	property = "site.initializer.key=" + SESiteInitializer.KEY,
	service = SiteInitializer.class
)
public class SESiteInitializer implements SiteInitializer {
	
	public static final String KEY = "site-se-site-initializer";

	@Override
	public String getDescription(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return "SE Demo Site";
	}

	@Override
	public String getThumbnailSrc() {
		return _servletContext.getContextPath() + "/images/thumbnail.png";
	}

	@Override
	public void initialize(long groupId) throws InitializationException {
		try {
			_createServiceContext(groupId);

			_addDLFileEntries();

			_addDDMStructures();

			_addDDMTemplates();

			_addJournalArticles(_addJournalFolders());
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw new InitializationException(exception);
		}
	}

	@Override
	public boolean isActive(long companyId) {
		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	private void _addDDMStructures() throws Exception {
		System.out.println("#########      _addDDMStructures      #########");

		Enumeration<URL> enumeration = _bundle.findEntries(
			_PATH + "/ddm-structures", StringPool.STAR, false);

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			Class<?> clazz = getClass();

			_defaultDDMStructureHelper.addDDMStructures(
				_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				clazz.getClassLoader(), url.getPath(), _serviceContext);
		}
	}

	private void _addDDMTemplates() throws Exception {
		long resourceClassNameId = _portal.getClassNameId(JournalArticle.class);

		Enumeration<URL> enumeration = _bundle.findEntries(
			_PATH + "/ddm-templates", "ddm_template.json", true);

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			JSONObject ddmTemplateJSONObject = JSONFactoryUtil.createJSONObject(
				StringUtil.read(url.openStream()));

			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					_serviceContext.getScopeGroupId(), resourceClassNameId,
					ddmTemplateJSONObject.getString("ddmStructureKey"));

			_ddmTemplateLocalService.addTemplate(
				_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
				_portal.getClassNameId(DDMStructure.class),
				ddmStructure.getStructureId(), resourceClassNameId,
				ddmTemplateJSONObject.getString("ddmTemplateKey"),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(),
					ddmTemplateJSONObject.getString("name")
				).build(),
				null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
				TemplateConstants.LANG_TYPE_FTL, _read("ddm_template.ftl", url),
				false, false, null, null, _serviceContext);
		}
	}

	private void _addDLFileEntries() throws Exception {
		URL url = _bundle.getEntry("/images.zip");
		
		if (url == null) {
			throw new InitializationException("Unable to get entry '/images.zip'.");
		}

		File file = FileUtil.createTempFile(url.openStream());

		_fileEntries = ImagesImporterUtil.importFile(
			_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
			file);
	}

	private void _addJournalArticles(List<JournalFolder> journalFolders)
		throws Exception {

		Map<String, String> fileEntriesMap = _getFileEntriesMap();

		Map<String, Long> journalFolderMap = new HashMap<>();

		for (JournalFolder journalFolder : journalFolders) {
			journalFolderMap.put(
				journalFolder.getName(), journalFolder.getFolderId());
		}

		Enumeration<URL> enumeration = _bundle.findEntries(
			_PATH + "/journal-articles", "journal_article.json", true);

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String content = StringUtil.read(url.openStream());

			JSONObject journalArticleJSONObject =
				JSONFactoryUtil.createJSONObject(content);

			Calendar calendar = CalendarFactoryUtil.getCalendar(
				_serviceContext.getTimeZone());

			int displayDateMonth = calendar.get(Calendar.MONTH);
			int displayDateDay = calendar.get(Calendar.DAY_OF_MONTH);
			int displayDateYear = calendar.get(Calendar.YEAR);
			int displayDateHour = calendar.get(Calendar.HOUR_OF_DAY);
			int displayDateMinute = calendar.get(Calendar.MINUTE);

			JournalArticle article = _journalArticleLocalService.addArticle(
				_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
				journalFolderMap.getOrDefault(
					journalArticleJSONObject.getString("folder"),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				journalArticleJSONObject.getString("articleId"), false, 1,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(),
					journalArticleJSONObject.getString("name")),
				null,
				StringUtil.replace(
					_read("journal_article.xml", url), StringPool.DOLLAR,
					StringPool.DOLLAR, fileEntriesMap),
				journalArticleJSONObject.getString("ddmStructureKey"),
				journalArticleJSONObject.getString("ddmTemplateKey"), null,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, 0, 0, 0, 0, 0, true, 0, 0,
				0, 0, 0, true, true, false, null, null, null, null,
				_serviceContext);

			long resourceClassNameId = _portal.getClassNameId(
				JournalArticle.class);

			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					_serviceContext.getScopeGroupId(), resourceClassNameId,
					journalArticleJSONObject.getString("ddmStructureKey"));

			long defaultLayoutPageTemplateEntryId =
				_getDefaultLayoutPageTemplateEntryId(
					resourceClassNameId, ddmStructure.getStructureId());

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
				resourceClassNameId, article.getResourcePrimKey(),
				defaultLayoutPageTemplateEntryId,
				AssetDisplayPageConstants.TYPE_DEFAULT, _serviceContext);
		}
	}

	private List<JournalFolder> _addJournalFolders() throws Exception {
		List<JournalFolder> journalFolders = new ArrayList<>();

		JSONArray journalFoldersJSONArray = JSONFactoryUtil.createJSONArray(
			_read("/journal-folders/journal_folders.json"));

		for (int i = 0; i < journalFoldersJSONArray.length(); i++) {
			JSONObject jsonObject = journalFoldersJSONArray.getJSONObject(i);

			JournalFolder journalFolder = _journalFolderLocalService.addFolder(
				_serviceContext.getUserId(), _serviceContext.getScopeGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				jsonObject.getString("name"), null, _serviceContext);

			journalFolders.add(journalFolder);
		}

		return journalFolders;
	}

	private void _createServiceContext(long groupId) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Locale locale = LocaleUtil.getSiteDefault();

		serviceContext.setLanguageId(LanguageUtil.getLanguageId(locale));

		serviceContext.setScopeGroupId(groupId);

		User user = _userLocalService.getUser(PrincipalThreadLocal.getUserId());

		serviceContext.setTimeZone(user.getTimeZone());
		serviceContext.setUserId(user.getUserId());

		_serviceContext = serviceContext;
	}

	private long _getDefaultLayoutPageTemplateEntryId(
		long classNameId, long classTypeId) {

		LayoutPageTemplateEntry defaultAssetDisplayPage =
			_layoutPageTemplateEntryService.fetchDefaultLayoutPageTemplateEntry(
				_serviceContext.getScopeGroupId(), classNameId, classTypeId);

		if (defaultAssetDisplayPage == null) {
			return 0;
		}

		return defaultAssetDisplayPage.getLayoutPageTemplateEntryId();
	}

	private Map<String, String> _getFileEntriesMap() throws Exception {
		Map<String, String> fileEntriesMap = new HashMap<>();

		for (FileEntry fileEntry : _fileEntries) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				JSONFactoryUtil.looseSerialize(fileEntry));

			jsonObject.put("alt", StringPool.BLANK);

			fileEntriesMap.put(
				"JSON_" + fileEntry.getFileName(), jsonObject.toString());

			fileEntriesMap.put(
				"URL_" + fileEntry.getFileName(),
				_dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false));
		}

		return fileEntriesMap;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(clazz.getClassLoader(), _PATH + fileName);
	}

	private String _read(String fileName, URL url) throws Exception {
		String path = url.getPath();

		URL entryURL = _bundle.getEntry(
			path.substring(0, path.lastIndexOf("/") + 1) + fileName);

		return StringUtil.read(entryURL.openStream());
	}

	private static final String _PATH =
		"com/liferay/site/se/site/initializer/internal/dependencies";
	
	private static final Log _log = LogFactoryUtil.getLog(
		SESiteInitializer.class);

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private Portal _portal;

	private ServiceContext _serviceContext;
	
	private Bundle _bundle;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	private List<FileEntry> _fileEntries;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService _layoutPageTemplateEntryService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.se.site.initializer)"
	)
	private ServletContext _servletContext;

	@Reference
	private DefaultDDMStructureHelper _defaultDDMStructureHelper;

	@Reference
	private UserLocalService _userLocalService;

}