package com.recuencojones.bitbucket.log.rest;

import com.recuencojones.bitbucket.log.rest.*;
import com.recuencojones.bitbucket.log.dao.*;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.rest.util.ResourcePatterns;
import com.atlassian.bitbucket.rest.util.ResponseFactory;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.sun.jersey.spi.resource.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ResourcePatterns.REPOSITORY_URI + "/settings")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Singleton
public class RepositoryCloneSettingsResource {
	private static final Logger log = LoggerFactory.getLogger(RepositoryCloneSettingsResource.class);

	@ComponentImport
	private final PermissionValidationService permissionValidationService;

	private final RepositoryCloneSettingsDAO repositoryCloneSettingsDAO;

	@Inject
	public RepositoryCloneSettingsResource(
		PermissionValidationService permissionValidationService,
		RepositoryCloneSettingsDAO repositoryCloneSettingsDAO
	) {
		this.permissionValidationService = permissionValidationService;
		this.repositoryCloneSettingsDAO = repositoryCloneSettingsDAO;
	}

	@GET
	public Response getSettings(@Context Repository repository) {
		log.info("Retrieve logging url for repository {}/{}", repository.getProject().getKey(), repository.getSlug());

		RepositoryCloneSettings settings = repositoryCloneSettingsDAO.get(repository.getId());

		if (settings != null) {
			RepositoryCloneSettingsDTO settingsDTO = new RepositoryCloneSettingsDTO();

			settingsDTO.setURL(settings.getURL());

			return ResponseFactory.ok(settingsDTO).build();
		}

		return ResponseFactory.ok().build();
	}

	@POST
	public Response saveSettings(@Context Repository repository, RepositoryCloneSettingsDTO settings) {
		permissionValidationService.validateForRepository(repository, Permission.REPO_ADMIN);

		log.info("Store logging url for repository {}/{}", repository.getProject().getKey(), repository.getSlug());
		repositoryCloneSettingsDAO.save(repository.getId(), settings.getURL());

		return ResponseFactory.ok().build();
	}
}
