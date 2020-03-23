package com.recuencojones.bitbucket.log;

import com.atlassian.bitbucket.event.repository.RepositoryCloneEvent;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettings;
import com.recuencojones.bitbucket.log.dao.RepositoryCloneSettingsDAO;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OnRepositoryCloneTest {
	final String url = "https://url";
	final String projectKey = "proj_1";
	final String repositorySlug = "repo_1";
	final int repositoryID = 1337;

	private Request mockRequest;
	private RequestFactory mockRequestFactory;
	private RepositoryCloneSettings mockRepositoryCloneSettings;
	private RepositoryCloneSettingsDAO mockRepositoryCloneSettingsDAO;
	private RepositoryCloneEvent mockEvent;
	private Repository mockRepository;
	private Project mockProject;

	@Before
	public void setup() {
		mockRequest = mock(Request.class);
		mockRequestFactory = mock(RequestFactory.class);
		mockRepositoryCloneSettings = mock(RepositoryCloneSettings.class);
		mockRepositoryCloneSettingsDAO = mock(RepositoryCloneSettingsDAO.class);
		mockEvent = mock(RepositoryCloneEvent.class);
		mockRepository = mock(Repository.class);
		mockProject = mock(Project.class);

		when(mockProject.getKey()).thenReturn(projectKey);
		when(mockRepository.getId()).thenReturn(repositoryID);
		when(mockRepository.getSlug()).thenReturn(repositorySlug);
		when(mockRepository.getProject()).thenReturn(mockProject);
		when(mockEvent.getRepository()).thenReturn(mockRepository);
		when(mockRequestFactory.createRequest(Request.MethodType.POST, url)).thenReturn(mockRequest);
	}

	@Test
	public void testCloneRepositoryWithSettingsShouldExecutePostRequestWhenEnabled() throws ResponseException {
		final OnRepositoryClone component = new OnRepositoryClone(mockRequestFactory, mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(mockRepositoryCloneSettings);
		when(mockRepositoryCloneSettings.getURL()).thenReturn(url);
		when(mockRepositoryCloneSettings.isEnabled()).thenReturn(true);

		component.onCloneEvent(mockEvent);

		verify(mockRequest).execute();
	}

	@Test
	public void testCloneRepositoryWithSettingsShouldNotExecutePostRequestWhenNotEnabled() throws ResponseException {
		final OnRepositoryClone component = new OnRepositoryClone(mockRequestFactory, mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(mockRepositoryCloneSettings);
		when(mockRepositoryCloneSettings.getURL()).thenReturn(url);
		when(mockRepositoryCloneSettings.isEnabled()).thenReturn(false);

		component.onCloneEvent(mockEvent);

		verify(mockRequest, never()).execute();
	}

	@Test
	public void testCloneRepositoryWithoutSettingsShouldNotExecutePostRequest() throws ResponseException {
		final OnRepositoryClone component = new OnRepositoryClone(mockRequestFactory, mockRepositoryCloneSettingsDAO);

		when(mockRepositoryCloneSettingsDAO.get(repositoryID)).thenReturn(null);

		component.onCloneEvent(mockEvent);

		verify(mockRequest, never()).execute();
	}
}
