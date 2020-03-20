package com.recuencojones.bitbucket.log;

import com.atlassian.bitbucket.event.repository.RepositoryDeletedEvent;

import com.atlassian.bitbucket.repository.Repository;

import com.atlassian.event.api.EventListener;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Inject;

@Named("onRepositoryDeleted")
public class OnRepositoryDeleted {
	private static final Logger log = LoggerFactory.getLogger(OnRepositoryClone.class);

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public OnRepositoryDeleted(
		final PluginSettingsFactory pluginSettingsFactory
	) {
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@EventListener
	public void onRepositoryDeleted(final RepositoryDeletedEvent event) {
		deleteSettings(event.getRepository());
	}

	private void deleteSettings(final Repository repository) {

	}
}
