package com.recuencojones.bitbucket.log;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

class RepositoryCloneSettingsServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(RepositoryCloneSettingsServlet.class);

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@ComponentImport
	private final RepositoryService repositoryService;

	@ComponentImport
	private final SoyTemplateRenderer soyTemplateRenderer;

	@ComponentImport
	private final PageBuilderService pageBuilderService;

	@Inject
	public RepositoryCloneSettingsServlet(
		final PluginSettingsFactory pluginSettingsFactory,
		final RepositoryService repositoryService,
		final SoyTemplateRenderer soyTemplateRenderer,
		final PageBuilderService pageBuilderService
	) {
		this.pluginSettingsFactory = pluginSettingsFactory;
		this.repositoryService = repositoryService;
		this.soyTemplateRenderer = soyTemplateRenderer;
		this.pageBuilderService = pageBuilderService;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (Strings.isNullOrEmpty(pathInfo) || pathInfo.equals("/")) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String[] pathParts = pathInfo.substring(1).split("/");
		if (pathParts.length != 2) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String projectKey = pathParts[0];
		String repoSlug = pathParts[1];
		Repository repository = repositoryService.getBySlug(projectKey, repoSlug);
		if (repository == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		doView(repository, req, resp);
	}

	private void doView(Repository repository, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		render(
			resp,
			"com.recuencojones.bitbucket.log.page.repoSettingsPanel",
			ImmutableMap.<String, Object>builder()
				.put("repository", repository)
				.build()
		);
	}

	private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws ServletException, IOException {
		pageBuilderService.assembler()
			.resources()
			.requireContext("bitbucket.page.repository.settings.log-on-clone");

		resp.setContentType("text/html;charset=UTF-8");
		try {
			soyTemplateRenderer.render(
				resp.getWriter(),
				"com.recuencojones.bitbucket.log-on-clone:log-on-clone-serverside-resources",
				templateName,
				data
			);
		} catch (SoyException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException) {
				throw (IOException) cause;
			}
			throw new ServletException(e);
		}
	}
}
