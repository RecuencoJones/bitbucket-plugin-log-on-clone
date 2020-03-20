package com.recuencojones.bitbucket.log.dao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

import static net.java.ao.schema.StringLength.UNLIMITED;

@Table("RC_SETTINGS")
public interface RepositoryCloneSettings extends Entity {

	String REPO_ID_COLUMN = "REPO_ID";
	String URL_COLUMN = "URL";

	@Indexed
	@Accessor(REPO_ID_COLUMN)
	@NotNull
	int getId();

	@Accessor(URL_COLUMN)
	@StringLength(UNLIMITED)
	String getURL();

	@Mutator(URL_COLUMN)
	void setURL(String url);
}
