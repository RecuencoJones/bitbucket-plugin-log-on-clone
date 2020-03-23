package com.recuencojones.bitbucket.log;

import com.atlassian.bitbucket.event.repository.RepositoryDeletedEvent;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettings;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettingsDAO;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OnRepositoryDeletedTest {
	final String projectKey = "proj_1";
	final String repositorySlug = "repo_1";
	final int repositoryID = 1337;

	private RepositoryCloneSettings mockRepositoryCloneSettings;
	private RepositoryCloneSettingsDAO mockRepositoryCloneSettingsDAO;
	private RepositoryDeletedEvent mockEvent;
	private Repository mockRepository;
	private Project mockProject;

	@Before
	public void setup() {
		mockRepositoryCloneSettings = mock(RepositoryCloneSettings.class);
		mockRepositoryCloneSettingsDAO = mock(RepositoryCloneSettingsDAO.class);
		mockEvent = mock(RepositoryDeletedEvent.class);
		mockRepository = mock(Repository.class);
		mockProject = mock(Project.class);

		when(mockProject.getKey()).thenReturn(projectKey);
		when(mockRepository.getId()).thenReturn(repositoryID);
		when(mockRepository.getSlug()).thenReturn(repositorySlug);
		when(mockRepository.getProject()).thenReturn(mockProject);
		when(mockEvent.getRepository()).thenReturn(mockRepository);
	}

	@Test
	public void testDeleteRepositoryWithSettingsShouldRemoveSettings() {
		final OnRepositoryDeleted component = new OnRepositoryDeleted(mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(mockRepositoryCloneSettings);

		component.onRepositoryDeleted(mockEvent);

		verify(mockRepositoryCloneSettingsDAO).remove(mockRepositoryCloneSettings);
	}

	@Test
	public void testDeleteRepositoryWithoutSettingsShouldNotRemoveSettings() {
		final OnRepositoryDeleted component = new OnRepositoryDeleted(mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(null);

		component.onRepositoryDeleted(mockEvent);

		verify(mockRepositoryCloneSettingsDAO, never()).remove(mockRepositoryCloneSettings);
	}
}
