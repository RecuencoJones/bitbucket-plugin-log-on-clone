package com.recuencojones.bitbucket.log;

import com.recuencojones.bitbucket.log.dao.*;

import com.atlassian.bitbucket.event.repository.RepositoryCloneEvent;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.atlassian.sal.api.net.*;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OnRepositoryClone {
	private static final Logger log = LoggerFactory.getLogger(OnRepositoryClone.class);

	@ComponentImport
	private final RequestFactory requestFactory;

	private final RepositoryCloneSettingsDAO repositoryCloneSettingsDAO;

	@Inject
	public OnRepositoryClone(
		final RequestFactory requestFactory,
		final RepositoryCloneSettingsDAO repositoryCloneSettingsDAO
	) {
		this.requestFactory = requestFactory;
		this.repositoryCloneSettingsDAO = repositoryCloneSettingsDAO;
	}

	@EventListener
	public void onCloneEvent(final RepositoryCloneEvent event) {
		final Repository repository = event.getRepository();
		final int repositoryID = repository.getId();
		final String repositorySlug = repository.getSlug();
		final String projectKey = repository.getProject().getKey();

		final RepositoryCloneSettings settings = repositoryCloneSettingsDAO.get(repositoryID);

		if (settings != null && settings.isEnabled()) {
			log.debug("Repository {}/{} has log-on-clone settings", projectKey, repositorySlug);

			final Request request = requestFactory.createRequest(Request.MethodType.POST, settings.getURL());

			request.setRequestBody(new Gson().toJson(repository));

			try {
				request.execute();
			} catch (final ResponseException e) {
				log.error("Could not log clone of {}/{}. Skipping.", projectKey, repositorySlug);
			}
		}
	}
}
