package com.recuencojones.bitbucket.log;

import com.recuencojones.bitbucket.log.dao.*;

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

@Named
public class OnRepositoryDeleted {
	private static final Logger log = LoggerFactory.getLogger(OnRepositoryClone.class);

	private final RepositoryCloneSettingsDAO repositoryCloneSettingsDAO;

	@Inject
	public OnRepositoryDeleted(
		final RepositoryCloneSettingsDAO repositoryCloneSettingsDAO
	) {
		this.repositoryCloneSettingsDAO = repositoryCloneSettingsDAO;
	}

	@EventListener
	public void onRepositoryDeleted(final RepositoryDeletedEvent event) {
		final Repository repository = event.getRepository();
		final int repositoryID = repository.getId();
		final String repositorySlug = repository.getSlug();
		final String projectKey = repository.getProject().getKey();
		final RepositoryCloneSettings settings = repositoryCloneSettingsDAO.get(repositoryID);

		if (settings != null) {
			log.debug("Repository {}/{} has log-on-clone settings, delete.", projectKey, repositorySlug);
			repositoryCloneSettingsDAO.remove(settings);
		}
	}
}
