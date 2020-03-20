package com.recuencojones.bitbucket.log.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryCloneSettingsDTO {

    @JsonProperty("url")
    private String url;

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }
}
