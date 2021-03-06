package com.github.sailarize.link;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.github.sailarize.http.Header;
import com.github.sailarize.utils.ToStringBuilder;

/**
 * Models a Hypermedia link.
 * 
 * @author agusmunioz
 * 
 */
public class HypermediaLink {

    private String href;

    private String rel;

    private String title;

    private String type;

    private String fusion;

    private String residue;

    private Collection<Header> headers;

    private Map<String, String> data;

    /**
     * The link href property holding a valid URL. It is required.
     * 
     * @return the href value.
     */
    public String getHref() {

        return href;
    }

    /**
     * Sets the link href property holding a valid URL. It is required.
     * 
     * @param href
     *            the href value.
     */
    public void setHref(String href) {

        this.href = href;
    }

    /**
     * Gets the link rel property that gives some information of the link usage.
     * It is required.
     * 
     * @return the rel value.
     */
    public String getRel() {

        return rel;
    }

    /**
     * Sets the link rel property that gives some information of the link usage.
     * It is required.
     * 
     * @param rel
     *            the rel value.
     */
    public void setRel(String rel) {

        this.rel = rel;
    }

    /**
     * Gets the link title, a user friendly text explaining the link. It is not
     * required.
     * 
     * @return the title.
     */
    public String getTitle() {

        return title;
    }

    /**
     * Sets the link title, a user friendly text explaining the link. It is not
     * required.
     * 
     * @param title
     *            a text.
     */
    public void setTitle(String title) {

        this.title = title;
    }

    /**
     * Gets the link type property indicating the resource media type in the
     * other side of the link.
     * 
     * @return the media type.
     */
    public String getType() {

        return type;
    }

    /**
     * Sets the link type property indicating the resource media type in the
     * other side of the link.
     * 
     * @param type
     *            the media type.
     */
    public void setType(String type) {

        this.type = type;
    }

    /**
     * Name for fusion group.
     * 
     * @return the fusion group name or null if not fusionable.
     */
    public String getFusion() {
        return fusion;
    }

    /**
     * Sets the fusion group.
     * 
     * @param fusion
     *            the fusion group name.
     */
    public void setFusion(String fusion) {
        this.fusion = fusion;
    }

    /**
     * Residue for link fusion.
     * 
     * @return the residue or null if no reside is involved.
     */
    public String getResidue() {
        return residue;
    }

    /**
     * Sets the residue for link fusion.
     * 
     * @param residue
     *            the residue.
     */
    public void setResidue(String residue) {
        this.residue = residue;
    }

    /**
     * Gets the list of headers that must be used when navigating the link.
     * 
     * @return the headers or null if no header was added.
     */
    public Collection<Header> getHeaders() {

        return headers;
    }

    /**
     * Sets the list of headers that must be used when navigating the link.
     * 
     * @param headers
     *            the collection of headers.
     */
    public void setHeaders(Collection<Header> headers) {

        this.headers = headers;
    }

    /**
     * Adds a header to the link in order to be used when the link is navigated.
     * 
     * @param name
     *            the header's name.
     * 
     * @param value
     *            the header's value.
     */
    public void addHeader(String name, Object value) {

        if (this.headers == null) {
            this.headers = new LinkedList<Header>();
        }

        this.headers.add(new Header(name, value.toString()));
    }

    public void add(Header header) {

        if (this.headers == null) {
            this.headers = new LinkedList<Header>();
        }

        this.headers.add(header);
    }

    /**
     * Gets the link extra data. Not required, could be null.
     * 
     * @return a map with the extra data.
     */
    public Map<String, String> getData() {

        return data;
    }

    /**
     * Adds any extra data to the link.s
     * 
     * @param name
     *            the data name.
     * 
     * @param value
     *            the data value.
     */
    public void addData(String name, String value) {

        if (this.data == null) {
            this.data = new HashMap<String, String>();
        }

        this.data.put("data-" + name, value);
    }

    @Override
    public String toString() {

        return ToStringBuilder.toString(this);
    }
}
