package com.recuencojones.bitbucket.log;

import com.atlassian.bitbucket.event.repository.RepositoryCloneEvent;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.atlassian.sal.api.net.*;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Inject;

@Named("onRepositoryClone")
public class OnRepositoryClone {
	private static final Logger log = LoggerFactory.getLogger(OnRepositoryClone.class);

	@ComponentImport
	private final RequestFactory requestFactory;

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public OnRepositoryClone(
		final RequestFactory requestFactory,
		final PluginSettingsFactory pluginSettingsFactory
	) {
		this.requestFactory = requestFactory;
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@EventListener
	public void onCloneEvent(final RepositoryCloneEvent cloneEvent) {
		final Repository repository = cloneEvent.getRepository();
		final String projectKey = repository.getProject().getKey();
		final String repositorySlug = repository.getSlug();

		log.info("Repository {}/{} cloned", projectKey, repositorySlug);

		final Request request = requestFactory.createRequest(Request.MethodType.POST, "https://en6qhxx7a3ksl.x.pipedream.net");

		request.setRequestBody(new Gson().toJson(repository));

		try {
			request.execute();
		} catch (final ResponseException e) {
			log.error("Could not log clone of {}/{}. Skipping.", projectKey, repositorySlug);
		}
	}
}
