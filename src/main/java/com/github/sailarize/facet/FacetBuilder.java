package com.github.sailarize.facet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.github.sailarize.http.Header;
import com.github.sailarize.http.ParameterHolder;
import com.github.sailarize.link.HypermediaLink;
import com.github.sailarize.link.LinkBuilder;
import com.github.sailarize.link.RelBuilder;
import com.github.sailarize.page.PageConstants;
import com.github.sailarize.properties.Titles;
import com.github.sailarize.resource.SailResource;
import com.github.sailarize.resource.SailTags;
import com.github.sailarize.servlet.RequestHolder;
import com.github.sailarize.url.Filter;
import com.github.sailarize.utils.ToStringBuilder;

/**
 * Builder that creates links for faceting a list of resources.
 * 
 * @author agusmunioz
 * 
 */
public class FacetBuilder {

    private static final String REFINES = "refines";

    /**
     * Link group name for facets.
     */
    public static final String GROUP = "facets";

    /**
     * Rel prefix for clean links.
     */
    public static final String CLEAN = "clean" + SailTags.KEY;

    private final static Collection<String> filterBlacklist = Arrays.asList(PageConstants.PAGE_PARAM);

    private String name;

    private String relPrefix;

    private String relPostfix;

    private Collection<FacetOption> options;

    private Map<String, Collection<Filter>> filtersByName;

    private Collection<String> excludedFilters;

    private String[] titles;

    private Map<String, Object[]> data;

    private boolean all = false;

    private String allTitle;

    private boolean grouped = true;

    private Collection<Header> headers;

    private FacetBuilder(String name) {

        this.name = name;
        this.relPrefix = name;
        this.relPostfix = "";
        this.options = new LinkedList<FacetOption>();
        this.filtersByName = new HashMap<String, Collection<Filter>>();
        this.excludedFilters = new LinkedList<String>();

        if (RequestHolder.get() != null) {
            this.filter(RequestHolder.get());
        }
    }

    /**
     * Creates an initialized {@link FacetBuilder}.
     * 
     * @param name
     *            the facet name.
     * 
     * @return the builder for further build.
     */
    public static FacetBuilder facet(String name) {

        return new FacetBuilder(name);
    }

    /**
     * Adds the list of options.
     * 
     * @param options
     *            the list of options for building the links.
     * 
     * @return this builder for further build.
     */
    public FacetBuilder options(Collection<FacetOption> options) {

        this.options.addAll(options);
        return this;
    }

    /**
     * Configures the facet to be inclusive, where values can be combined.
     * 
     * @param values
     *            the list of facet values.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder inclusive(Object... values) {

        for (Object value : values) {

            this.options.add(new InclusiveFacetOption(this.name, value.toString()));
        }

        return this;
    }

    /**
     * Configures the facet to be exclusive, where values must be used one at a
     * time.
     * 
     * @param values
     *            the list of facet values.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder exclusive(Object... values) {

        for (Object value : values) {

            this.options.add(new ExclusiveFacetOption(this.name, value.toString()));
        }

        return this;
    }

    /**
     * Configures the builder with a rel prefix.
     * 
     * @param prefix
     *            the prefix to be added in all facet links rel.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder relPrefix(String prefix) {

        this.relPrefix = prefix;
        return this;
    }

    /**
     * Configures the builder with a rel postfix.
     * 
     * @param postFix
     *            the postfix to be added in all facet links rel.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder relPostfix(String postFix) {

        this.relPostfix = postFix;
        return this;
    }

    /**
     * Configures the title of each facet value.
     * 
     * @param titles
     *            the list of titles for each facet value. The order must match
     *            the order of values.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder titles(String... titles) {

        this.titles = titles;
        return this;
    }

    /**
     * Adds a filter to all facet links.
     * 
     * @param name
     *            the filter name.
     * 
     * @param value
     *            the filter value.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder filter(String name, Object value) {
        if (name != null && !filterBlacklist.contains(name)) {
            Collection<Filter> filters = this.filtersByName.get(name);
            if (filters == null) {
                filters = new ArrayList<Filter>();
            }
            filters.add(new Filter(name, value.toString()));
            this.filtersByName.put(name, filters);
        }

        return this;
    }

    /**
     * Excludes a filter
     * 
     * @param filtersName
     *            the filters name to exclude
     * @return the builder for further build.
     */
    public FacetBuilder exclude(String... filtersName) {
        this.excludedFilters.addAll(Arrays.asList(filtersName));
        return this;
    }

    /**
     * Adds all the query parameters in the request as filters in all facet
     * links.
     * 
     * @param request
     *            the HTTP request with the parameters.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder filter(HttpServletRequest request) {

        for (Entry<String, String[]> filter : request.getParameterMap().entrySet()) {

            for (String value : filter.getValue()) {

                if (!ParameterHolder.get().contains(filter.getKey())) {
                    this.filter(filter.getKey(), value);
                }
            }

        }

        return this;
    }

    /**
     * Configures the extra data for each facet value.
     * 
     * @param name
     *            the extra data name.
     * 
     * @param data
     *            the list of extra data for each facet value. The order must
     *            match the order of values.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder data(String name, Object... data) {

        if (this.data == null) {
            this.data = new HashMap<String, Object[]>();
        }

        this.data.put(name, data);

        return this;
    }

    /**
     * Configures a header in the facet.
     * 
     * @param name
     *            the header name.
     * 
     * @param value
     *            the header vale.
     * 
     * @return the builder for further build.
     */
    public FacetBuilder header(String name, String value) {

        return this.header(new Header(name, value));
    }

    /**
     * Configures a header in the facet if the header is not null.
     * 
     * @param header
     *            the header.
     * 
     * @return the builder for further building.
     */
    public FacetBuilder header(Header header) {

        if (header == null) {
            return this;
        }

        if (this.headers == null) {
            this.headers = new LinkedList<Header>();
        }

        this.headers.add(header);

        return this;
    }

    /**
     * Configures a list of headers for all the options if the list is not null.
     * 
     * @param headers
     *            the list of headers.
     * 
     * @return the builder for further building.
     */
    public FacetBuilder headers(Collection<Header> headers) {

        if (headers == null) {
            return this;
        }

        for (Header header : headers) {
            this.header(header);
        }

        return this;
    }

    /**
     * Configures the all link to be built in the facet group link.
     * 
     * @param title
     *            the all link title.
     */
    public FacetBuilder all(String title) {
        this.all = true;
        this.allTitle = title;
        return this;
    }

    /**
     * Builds the facet links for each options and add them to the resource.
     * 
     * @param list
     *            the resource where to add all the facet links.
     */
    public void build(SailResource list, Object... values) {

        for (String excludedFilterName : this.excludedFilters) {
            this.filtersByName.remove(excludedFilterName);
        }

        Collection<Filter> filters = new ArrayList<Filter>();
        for (Collection<Filter> filterCollection : this.filtersByName.values()) {
            filters.addAll(filterCollection);
        }

        if (this.all) {

            Collection<Filter> allFilters = new LinkedList<Filter>();

            for (Filter filter : filters) {
                if (!filter.getName().equals(this.name)) {
                    allFilters.add(filter);
                }
            }

            HypermediaLink link = new LinkBuilder(list, "all", values).title(this.allTitle)
                    .filters(allFilters)
                    .data(REFINES, "false")
                    .headers(this.headers)
                    .build();

            if (grouped) {
                list.add(link, GROUP, this.name);
            } else {
                list.add(link);
            }
        }

        int index = 0;

        for (FacetOption option : this.options) {

            Collection<Filter> compatibleFilters = option.compatibles(filters);

            String rel = this.getRel(option);

            String residue = null;

            String refines = "true";

            if (option.isApplied(filters)) {
                rel = this.getCleanPrefix(option) + rel;
                residue = option.getFacet() + "=" + option.getValue();
                refines = "false";
            } else {
                option.apply(compatibleFilters);
            }

            LinkBuilder builder = new LinkBuilder(list, rel, values).title(this.getTitle(option, index))
                    .data(REFINES, refines)
                    .residue(residue)
                    .filters(compatibleFilters)
                    .headers(this.headers)
                    .headers(option.getHeaders());

            this.addData(builder, option, index);

            if (grouped) {
                list.add(builder.build(), GROUP, this.name);
            } else {
                list.add(builder.build());
            }

            index++;
        }

    }

    /**
     * Gets the facet option link title.
     * 
     * @param option
     *            the facet option.
     * 
     * @param position
     *            the position of the option in the list.
     * 
     * @return the title.
     */
    private String getTitle(FacetOption option, int position) {

        String title = option.getTitle();

        if (title == null && this.titles != null && position < this.titles.length) {

            title = this.titles[position];
        } else {
            title = Titles.get("facets." + option.getFacet() + "." + option.getValue());
        }

        return (title == null) ? option.getTitle() : title;
    }

    /**
     * Adds the facet option extra data to the link.
     * 
     * @param builder
     *            the builder for configuring the link data.
     * 
     * @param option
     *            the facet option a link is being created for.
     * 
     * @param position
     *            the position of the option in the list.
     */
    private void addData(LinkBuilder builder, FacetOption option, int position) {

        if (option.getData() != null) {

            for (Entry<String, Object> data : option.getData().entrySet()) {

                builder.data(data.getKey(), data.getValue().toString());
            }
        }

        if (this.data != null) {

            for (Entry<String, Object[]> optionData : this.data.entrySet()) {

                if (optionData.getValue() != null && position < optionData.getValue().length) {

                    builder.data(optionData.getKey(), optionData.getValue()[position].toString());
                }
            }

        }
    }

    /**
     * Builds the rel for the facet option link.
     * 
     * @param option
     *            a facet option.
     * 
     * @return the rel.
     */
    protected String getRel(FacetOption option) {

        return RelBuilder.rel(this.relPrefix, option.getName(), this.relPostfix);
    }

    /**
     * Gets the rel prefix for cleaning an applied facet option.
     * 
     * @param option
     *            the facet option.
     * 
     * @return the rel prefix.
     */
    protected String getCleanPrefix(FacetOption option) {

        return CLEAN;

    }

    /**
     * Configures the builder in order not to group facet links in a link group.
     */
    public FacetBuilder ungroup() {
        this.grouped = false;
        return this;
    }

    @Override
    public String toString() {

        return ToStringBuilder.toString(this);
    }
}
