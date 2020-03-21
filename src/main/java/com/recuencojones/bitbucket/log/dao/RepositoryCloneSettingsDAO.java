package com.recuencojones.bitbucket.log.dao;

import com.atlassian.activeobjects.external.ActiveObjects;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.ao.DBParam;

import javax.inject.Inject;

import static com.recuencojones.bitbucket.log.dao.RepositoryCloneSettings.REPO_ID_COLUMN;

@Component
public class RepositoryCloneSettingsDAO {
	private static final String ID_QUERY = String.format("%s = ?", REPO_ID_COLUMN);

	@ComponentImport
	private final ActiveObjects ao;

	@Inject
	public RepositoryCloneSettingsDAO(
		ActiveObjects ao
	) {
		this.ao = ao;
	}

	public RepositoryCloneSettings save(int repositoryID, String url, boolean enabled) {
		RepositoryCloneSettings settings = find(repositoryID);

		if (settings == null) {
			settings = ao.create(
				RepositoryCloneSettings.class,
				new DBParam(REPO_ID_COLUMN, repositoryID)
			);
		}

		settings.setURL(url);
		settings.setEnabled(enabled);
		settings.save();

		return settings;
	}

	public RepositoryCloneSettings get(int repositoryID) {
		return find(repositoryID);
	}

	public void remove(int repositoryID) {
		RepositoryCloneSettings settings = find(repositoryID);

		remove(settings);
	}

	public void remove(RepositoryCloneSettings settings) {
		ao.delete(settings);
	}

	private RepositoryCloneSettings find(int repositoryID) {
		RepositoryCloneSettings[] results = ao.find(RepositoryCloneSettings.class, ID_QUERY, repositoryID);

		if (results.length == 0) {
			return null;
		}

		return results[0];
	}
}
