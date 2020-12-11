package it.unisa.diem.se.team3.models;

import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the SiteDecorator class.
 */
public class Site implements Model {
    private final long siteId;
    private final String siteName;

    /**
     * @param siteId:   the id of the site
     * @param siteName: the name of the site
     */
    public Site(long siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Site site = (Site) o;
        return siteId == site.siteId &&
                Objects.equals(siteName, site.siteName);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"id\":\"" + siteId + "\","
                + "\"name\":\"" + siteName + "\"}";
    }
}
