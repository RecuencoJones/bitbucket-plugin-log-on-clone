package com.recuencojones.bitbucket.log.rest;

import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettings;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettingsDAO;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoryCloneSettingsResourceTest {
	final boolean enabled = true;
	final String url = "https://url";
	final String projectKey = "proj_1";
	final String repositorySlug = "repo_1";
	final int repositoryID = 1337;

	private PermissionValidationService mockPermissionValidationService;
	private RepositoryCloneSettings mockRepositoryCloneSettings;
	private RepositoryCloneSettingsDAO mockRepositoryCloneSettingsDAO;
	private Repository mockRepository;
	private Project mockProject;

	@Before
	public void setup() {
		mockPermissionValidationService = mock(PermissionValidationService.class);
		mockRepositoryCloneSettings = mock(RepositoryCloneSettings.class);
		mockRepositoryCloneSettingsDAO = mock(RepositoryCloneSettingsDAO.class);
		mockRepository = mock(Repository.class);
		mockProject = mock(Project.class);

		when(mockProject.getKey()).thenReturn(projectKey);
		when(mockRepository.getId()).thenReturn(repositoryID);
		when(mockRepository.getSlug()).thenReturn(repositorySlug);
		when(mockRepository.getProject()).thenReturn(mockProject);
	}

	@Test
	public void testGetSettingsForRepositoryWithConfigShouldReturnSettingsObject() {
		RepositoryCloneSettingsResource component = new RepositoryCloneSettingsResource(mockPermissionValidationService, mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(mockRepositoryCloneSettings);

		Response res = component.getSettings(mockRepository);

		assertThat(res.getEntity(), instanceOf(RepositoryCloneSettingsDTO.class));
	}

	@Test
	public void testGetSettingsForRepositoryWithoutConfigShouldReturnNull() {
		RepositoryCloneSettingsResource component = new RepositoryCloneSettingsResource(mockPermissionValidationService, mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(null);

		Response res = component.getSettings(mockRepository);

		assertNull(res.getEntity());
	}

	@Test
	public void testSaveSettingsForRepositoryAdminShouldSucceed() {
		RepositoryCloneSettingsResource component = new RepositoryCloneSettingsResource(mockPermissionValidationService, mockRepositoryCloneSettingsDAO);
		RepositoryCloneSettingsDTO settings = new RepositoryCloneSettingsDTO();

		settings.setEnabled(enabled);
		settings.setURL(url);

		component.saveSettings(mockRepository, settings);

		verify(mockRepositoryCloneSettingsDAO).save(repositoryID, url, enabled);
	}

	@Test(expected = AuthorisationException.class)
	public void testSaveSettingsForNonRepositoryAdminShouldFail() {
		RepositoryCloneSettingsResource component = new RepositoryCloneSettingsResource(mockPermissionValidationService, mockRepositoryCloneSettingsDAO);
		RepositoryCloneSettingsDTO settings = new RepositoryCloneSettingsDTO();

		doThrow(AuthorisationException.class)
			.when(mockPermissionValidationService)
			.validateForRepository(mockRepository, Permission.REPO_ADMIN);

		component.saveSettings(mockRepository, settings);
	}
}
