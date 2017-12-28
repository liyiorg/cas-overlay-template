package org.jasig.services.persondir.support.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.CaseInsensitiveAttributeNamedPersonImpl;
import org.jasig.services.persondir.support.CaseInsensitiveNamedPersonImpl;
import org.jasig.services.persondir.support.MultivaluedPersonAttributeUtils;
import org.jasig.services.persondir.support.jdbc.AbstractJdbcPersonAttributeDao;
import org.jasig.services.persondir.support.jdbc.ColumnMapParameterizedRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An {@link org.jasig.services.persondir.IPersonAttributeDao}
 * implementation that maps from column names in the result of a SQL query
 * to attribute names. <br>
 * You must set a Map from column names to attribute names and only column names
 * appearing as keys in that map will be used.
 * 
 * <br>
 * <br>
 * Configuration:
 * <table border="1">
 *     <tr>
 *         <th align="left">Property</th>
 *         <th align="left">Description</th>
 *         <th align="left">Required</th>
 *         <th align="left">Default</th>
 *     </tr>
 *     <tr>
 *         <td align="right" valign="top">columnsToAttributes</td>
 *         <td>
 *             The {@link Map} of {@link String} columns names to {@link String} or {@link Set}s
 *             of {@link String}s to use as attribute names in the returned Map. If a column name
 *             is not in the map the column name will be used in as the returned attribute name.
 *         </td>
 *         <td valign="top">No</td>
 *         <td valign="top">{@link java.util.Collections#EMPTY_MAP}</td>
 *     </tr>
 * </table>
 * 
 * @author andrew.petro@yale.edu
 * @author Eric Dalquist
 * @version $Revision$ $Date$
 * @since uPortal 2.5
 */
public class SingleRowJdbcPersonAttributeDaoX extends AbstractJdbcPersonAttributeDao<Map<String, Object>> {
    private static final ParameterizedRowMapper<Map<String, Object>> MAPPER = new ColumnMapParameterizedRowMapper(true);

    public SingleRowJdbcPersonAttributeDaoX() {
        super();
    }

    /**
     * Creates a new MultiRowJdbcPersonAttributeDao specifying the DataSource and SQL to use.
     * 
     * @param ds The DataSource to get connections from for executing queries, may not be null.
     * @param attrList Sets the query attribute list
     * @param sql The SQL to execute for user attributes, may not be null.
     */
    public SingleRowJdbcPersonAttributeDaoX(final DataSource ds, final String sql) {
    	super(ds, sql);
    	logger.debug("sql:" + sql);
    	System.out.println("sql:" + sql);
    }

    /* (non-Javadoc)
     * @see org.jasig.services.persondir.support.jdbc.AbstractJdbcPersonAttributeDao#getRowMapper()
     */
    @Override
    protected ParameterizedRowMapper<Map<String, Object>> getRowMapper() {
        return MAPPER;
    }
    

    
    /* (non-Javadoc)
     * @see org.jasig.services.persondir.support.jdbc.AbstractJdbcPersonAttributeDao#parseAttributeMapFromResults(java.util.List, java.lang.String)
     */
    @Override
    protected List<IPersonAttributes> parseAttributeMapFromResults(final List<Map<String, Object>> queryResults, final String queryUserName) {
        final List<IPersonAttributes> peopleAttributes = new ArrayList<>(queryResults.size());
        ObjectMapper objectMapper = new ObjectMapper();
       System.out.println("queryUserName : " + queryUserName);
       logger.debug("queryUserName : " + queryUserName);
        try {
        	logger.debug("queryResults : " + objectMapper.writeValueAsString(queryResults));
        	 System.out.println("queryResults : " + objectMapper.writeValueAsString(queryResults));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for (final Map<String, Object> queryResult : queryResults) {
            final Map<String, List<Object>> multivaluedQueryResult = MultivaluedPersonAttributeUtils.toMultivaluedMap(queryResult);
            
            final IPersonAttributes person;
            final String userNameAttribute = this.getConfiguredUserNameAttribute();
            if (this.isUserNameAttributeConfigured() && queryResult.containsKey(userNameAttribute)) {
                // Option #1:  An attribute is named explicitly in the config, 
                // and that attribute is present in the results from LDAP;  use it
                person = new CaseInsensitiveAttributeNamedPersonImpl(userNameAttribute, multivaluedQueryResult);
            } else if (queryUserName != null) {
                // Option #2:  Use the userName attribute provided in the query 
                // parameters.  (NB:  I'm not entirely sure this choice is 
                // preferable to Option #3.  Keeping it because it most closely 
                // matches the legacy behavior there the new option -- Option #1 
                // -- doesn't apply.  ~drewwills)
                person = new CaseInsensitiveNamedPersonImpl(queryUserName, multivaluedQueryResult);
            } else {
                // Option #3:  Create the IPersonAttributes doing a best-guess 
                // at a userName attribute
                person = new CaseInsensitiveAttributeNamedPersonImpl(userNameAttribute, multivaluedQueryResult);
            }
            
            peopleAttributes.add(person);
        }
        
        return peopleAttributes;
    }
}

