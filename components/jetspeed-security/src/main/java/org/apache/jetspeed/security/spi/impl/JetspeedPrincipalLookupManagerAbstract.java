/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.JetspeedPrincipalResultList;
import org.apache.jetspeed.security.impl.PersistentJetspeedPrincipal;
import org.apache.jetspeed.security.spi.JetspeedPrincipalLookupManager;
import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.accesslayer.RowReader;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for the principal lookup manager. Defines possible
 * database specific abstract methods and provides more generic methods to all
 * database specific principal lookup managers.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public abstract class JetspeedPrincipalLookupManagerAbstract implements JetspeedPrincipalLookupManager {

	static final Logger log = LoggerFactory.getLogger(JetspeedPrincipalLookupManagerAbstract.class);

    private static final String PARAM_PLACEHOLDER_PREFIX = "@@paramPlaceHolder";
    private static final String PARAM_PLACEHOLDER_SUFFIX = "@@";
    private static final Pattern PARAM_PLACEHOLDER_PATTERN = Pattern
            .compile("(" + PARAM_PLACEHOLDER_PREFIX + "\\d+" + PARAM_PLACEHOLDER_SUFFIX + ")");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jetspeed.security.spi.JetspeedPrincipalPersistanceManager#
	 * getPrincipals(org.apache.jetspeed.security.JetspeedPrincipalQueryContext)
	 */
	public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext) {
		int numberOfRecords = 0;
		ArrayList<JetspeedPrincipal> results = new ArrayList<JetspeedPrincipal>();
		
        Connection conn = null;

        PreparedStatement pstmtForPaging = null;
        ResultSet rsForPaging = null;

        PreparedStatement pstmtForCount = null;
        ResultSet rsForCount = null;
		
		try {
			conn = PersistenceBrokerFactory.defaultPersistenceBroker().serviceConnectionManager().getConnection();
            PreparedStatement [] pstmts = createPagingPreparedStatementAndCountPreparedStatement(conn, queryContext);
            pstmtForPaging = pstmts[0];
            pstmtForCount = pstmts[1];

			rsForPaging = pstmtForPaging.executeQuery();
			boolean hasRecords = rsForPaging.next();

			if (hasRecords) {
				// scroll the result set to the offset
				scrollToOffset(conn, rsForPaging, queryContext.getOffset());
				for (int i = 0; i < queryContext.getLength(); i++) {
					// now materialize the ResultSet into a JetspeedPrincipal
					RowReader rr = PersistenceBrokerFactory.defaultPersistenceBroker().getClassDescriptor(
							PersistentJetspeedPrincipal.class).getRowReader();
					Map<Object, Object> row = new HashMap<Object, Object>();
					// TODO: optimize, just retrieve the id from the DB and setup
					// a JetspeedPrincipal template on that.
					rr.readObjectArrayFrom(rsForPaging, row);
					PersistentJetspeedPrincipal p = (PersistentJetspeedPrincipal) rr.readObjectFrom(row);
					QueryByCriteria query = new QueryByCriteria(p);
					p = (PersistentJetspeedPrincipal) PersistenceBrokerFactory.defaultPersistenceBroker()
							.getObjectByQuery(query);
					results.add(p);
					if (!rsForPaging.next()) {
						break;
					}
				}
				
				rsForPaging.close();
				rsForPaging = null;
                pstmtForPaging.close();
                pstmtForPaging = null;

				rsForCount = pstmtForCount.executeQuery();
				while (rsForCount.next()) {
					numberOfRecords += rsForCount.getInt(1);
				}

				rsForCount.close();
				rsForCount = null;
				pstmtForCount.close();
				pstmtForCount = null;
			}
		} catch (SQLException e) {
			log.error("Error reading principal.", e);
		} catch (PBFactoryException e) {
			log.error("Error reading principal.", e);
		} catch (LookupException e) {
			log.error("Error reading principal.", e);
		} finally {
            if(rsForPaging != null) 
            {
                try 
                {
                    rsForPaging.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(pstmtForPaging != null) 
            {
                try 
                {
                    pstmtForPaging.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(rsForCount != null) 
            {
                try 
                {
                    rsForCount.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(pstmtForCount != null) 
            {
                try 
                {
                    pstmtForCount.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(conn != null) 
            {
                try 
                {
                    conn.close();
                }
                catch (Exception e) 
                {
                    log.error("error releasing the connection",e);
                }
            }
		}
		return new JetspeedPrincipalResultList(results, numberOfRecords);
	}

    private String putParamPlaceHolder(Map<String, Object> paramPlaceHolders, Object value) {
        String paramPlaceHolderName = PARAM_PLACEHOLDER_PREFIX + paramPlaceHolders.size() + PARAM_PLACEHOLDER_SUFFIX;
        paramPlaceHolders.put(paramPlaceHolderName, value);
        return paramPlaceHolderName;
    }

    private PreparedStatement[] createPagingPreparedStatementAndCountPreparedStatement(Connection conn,
            JetspeedPrincipalQueryContext queryContext) throws SQLException {
        String _paramPlaceHolderName = null;
        Map<String, Object> _paramPlaceHolders = new HashMap<String, Object>();

        String attributeConstraint = null;
        String fromPart = "SECURITY_PRINCIPAL";

        if (queryContext.getSecurityAttributes() != null) {
            int cnt = 1;

            for (Map.Entry<String, String> attribute : queryContext.getSecurityAttributes().entrySet()) {
                if (attributeConstraint == null) {
                    _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, attribute.getKey());
                    attributeConstraint = " a" + cnt + ".PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID AND a" + cnt
                            + ".ATTR_NAME = " + _paramPlaceHolderName;

                    _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, convertWildcardsForLike(attribute.getValue()));
                    attributeConstraint += " AND a" + cnt + ".ATTR_VALUE LIKE " + _paramPlaceHolderName;
                } else {
                    _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, attribute.getKey());
                    attributeConstraint += " AND a" + cnt + ".PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID AND a" + cnt
                            + ".ATTR_NAME = " + _paramPlaceHolderName;

                    _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, convertWildcardsForLike(attribute.getValue()));
                    attributeConstraint += " AND a" + cnt + ".ATTR_VALUE LIKE " + _paramPlaceHolderName;
                }

                fromPart += ", SECURITY_ATTRIBUTE a" + cnt;
                cnt++;
            }
        }

        String constraint = null;

        if (queryContext.getNameFilter() != null && queryContext.getNameFilter().length() > 0) {
            _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders,
                    convertWildcardsForLike(queryContext.getNameFilter()));
            constraint = "SECURITY_PRINCIPAL.PRINCIPAL_NAME LIKE " + _paramPlaceHolderName;
        }

        // find principals that are member of one or many roles
        // the principal must be member in all supplied roles.
        String roleConstraints = null;

        if (queryContext.getAssociatedRoles() != null && queryContext.getAssociatedRoles().size() > 0
                && queryContext.getAssociatedRoles().get(0).length() > 0) {
            int cnt = 1;

            for (String roleName : queryContext.getAssociatedRoles()) {
                _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, convertWildcardsForLike(roleName));

                if (roleConstraints == null) {
                    roleConstraints = "r" + cnt + ".ASSOC_NAME = '" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' " + "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='role' AND r" + cnt
                            + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                } else {
                    roleConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='role' AND r" + cnt
                            + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                }
            }

            fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
            cnt++;
        }

        // find principals that are member of one or many groups
        // the principal must be member in all supplied groups.
        String groupConstraints = null;

        if (queryContext.getAssociatedGroups() != null && queryContext.getAssociatedGroups().size() > 0
                && queryContext.getAssociatedGroups().get(0).length() > 0) {
            int cnt = 1;

            for (String groupName : queryContext.getAssociatedGroups()) {
                _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, convertWildcardsForLike(groupName));

                if (groupConstraints == null) {
                    groupConstraints = "r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='group' AND r" + cnt
                            + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                } else {
                    groupConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='group' AND r" + cnt
                            + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                }
            }

            fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
            cnt++;
        }

        // find principals that contain one or many users
        // the principal must contain all supplied users.
        String userConstraints = null;

        if (queryContext.getAssociatedUsers() != null && queryContext.getAssociatedUsers().size() > 0) {
            int cnt = 1;

            for (String userName : queryContext.getAssociatedGroups()) {
                _paramPlaceHolderName = putParamPlaceHolder(_paramPlaceHolders, convertWildcardsForLike(userName));

                if (userConstraints == null) {
                    userConstraints = "r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' AND r" + cnt + ".FROM_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='user' AND r" + cnt + ".TO_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                } else {
                    userConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
                            + "' AND r" + cnt + ".FROM_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
                            + ".PRINCIPAL_NAME LIKE " + _paramPlaceHolderName + " AND rp" + cnt
                            + ".PRINCIPAL_TYPE='group' AND r" + cnt
                            + ".TO_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
                }
            }

            fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
            cnt++;
        }

        if (attributeConstraint != null) {
            if (constraint != null) {
                constraint += " AND " + attributeConstraint;
            } else {
                constraint = attributeConstraint;
            }
        }

        if (roleConstraints != null) {
            if (constraint != null) {
                constraint += " AND " + roleConstraints;
            } else {
                constraint = roleConstraints;
            }
        }

        if (groupConstraints != null) {
            if (constraint != null) {
                constraint += " AND " + groupConstraints;
            } else {
                constraint = groupConstraints;
            }
        }

        if (userConstraints != null) {
            if (constraint != null) {
                constraint += " AND " + userConstraints;
            } else {
                constraint = userConstraints;
            }
        }

        String baseSqlStr = "SELECT SECURITY_PRINCIPAL.* from " + fromPart
                + " WHERE SECURITY_PRINCIPAL.PRINCIPAL_TYPE='" + queryContext.getJetspeedPrincipalType()
                + "' AND SECURITY_PRINCIPAL.DOMAIN_ID=" + queryContext.getSecurityDomain();

        if (constraint != null) {
            baseSqlStr += " AND " + constraint;
        }

        if (queryContext.getOrder() != null && queryContext.getOrder().equalsIgnoreCase("desc")) {
            baseSqlStr += " ORDER BY SECURITY_PRINCIPAL.PRINCIPAL_NAME DESC";
        } else {
            baseSqlStr += " ORDER BY SECURITY_PRINCIPAL.PRINCIPAL_NAME";
        }

        StringBuilder preparedSqlBuilder = new StringBuilder(baseSqlStr.length());
        Matcher matcher;
        char [] sqlChars = baseSqlStr.toCharArray();
        List<Object> parameters = new ArrayList<Object>(_paramPlaceHolders.size());
        Segment segment = new Segment(sqlChars, 0, sqlChars.length);

        for (matcher = PARAM_PLACEHOLDER_PATTERN.matcher(segment); matcher.find(); ) {
            preparedSqlBuilder.append(segment.subSequence(0, matcher.start())).append('?');
            _paramPlaceHolderName = matcher.group(1);
            parameters.add(_paramPlaceHolders.get(_paramPlaceHolderName));
            segment = (Segment) segment.subSequence(matcher.end(), segment.length());
            matcher.reset(segment);
        }

        preparedSqlBuilder.append(segment);

        String preparedSqlStr = preparedSqlBuilder.toString();
        String sql = getPagingSql(preparedSqlStr, queryContext);

        PreparedStatement pstmtForPaging = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < parameters.size(); i++) {
            pstmtForPaging.setString(i + 1, (String) parameters.get(i));
        }
        pstmtForPaging.setFetchSize((int) (queryContext.getOffset() + queryContext.getLength()));

        String countSql = convertToCountQueryStatement(preparedSqlStr);

        PreparedStatement pstmtForCount = conn.prepareStatement(countSql);
        for (int i = 0; i < parameters.size(); i++) {
            pstmtForCount.setString(i + 1, (String) parameters.get(i));
        }

        return new PreparedStatement[] { pstmtForPaging, pstmtForCount };
    }

    private String convertToCountQueryStatement(String baseSqlStr) {
        // get the total number of results effected by the query
        int fromPos = baseSqlStr.toUpperCase().indexOf(" FROM ");
        String countSql = "SELECT count(SECURITY_PRINCIPAL.PRINCIPAL_ID)" + baseSqlStr.substring(fromPos);

        // strip ORDER BY clause
        int orderByPos = countSql.toUpperCase().indexOf(" ORDER BY ");

        if (orderByPos >= 0) {
            countSql = countSql.substring(0, orderByPos);
        }

        return countSql;
    }

    private String convertWildcardsForLike(String s) {
        String converted = s;

        if (s != null) {
            String textOnly = s;
            boolean foreWildcard = false;
            boolean rearWildcard = false;

            if (textOnly.length() > 0 && textOnly.charAt(0) == '*') {
                textOnly = textOnly.substring(1);
                foreWildcard = true;
            }

            if (textOnly.length() > 0 && textOnly.charAt(s.length() - 1) == '*') {
                textOnly = textOnly.substring(0, textOnly.length() - 1);
                rearWildcard = true;
            }

            if (textOnly.length() > 0) {
                StringBuilder sb = new StringBuilder(textOnly.length() + 2);

                if (foreWildcard) {
                    sb.append('%');
                }

                sb.append(StringUtils.remove(textOnly, '%'));

                if (rearWildcard) {
                    sb.append('%');
                }

                converted = sb.toString();
            }
        }

        return converted;
    }

    /**
	 * Generate the base SQL syntax for selecting principals. This must not
	 * contain any database specifics.
	 * 
	 * @param queryContext
	 * @return
	 * @deprecated Never use this method due to vulnerable SQL statement manipulation.
	 */
    @Deprecated
	protected String generateBaseSql(JetspeedPrincipalQueryContext queryContext) {
		String attributeConstraint = null;
		String fromPart = "SECURITY_PRINCIPAL";
		if (queryContext.getSecurityAttributes() != null) {
			int cnt = 1;
			for (Map.Entry<String, String> attribute : queryContext.getSecurityAttributes().entrySet()) {
				if (attributeConstraint == null) {
					attributeConstraint = "a" + cnt + ".PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID AND a" + cnt
							+ ".ATTR_NAME = '" + attribute.getKey() + "' AND a" + cnt + ".ATTR_VALUE LIKE '"
							+ attribute.getValue() + "'";
				} else {
					attributeConstraint += " AND a" + cnt + ".PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID AND a" + cnt
							+ ".ATTR_NAME = '" + attribute.getKey() + "' AND a" + cnt + ".ATTR_VALUE LIKE '"
							+ attribute.getValue() + "'";
				}
				fromPart += ", SECURITY_ATTRIBUTE a" + cnt;
				cnt++;
			}
		}

		String constraint = null;
		if (queryContext.getNameFilter() != null && queryContext.getNameFilter().length() > 0) {
			constraint = "SECURITY_PRINCIPAL.PRINCIPAL_NAME LIKE '" + queryContext.getNameFilter().replace('*', '%')
					+ "'";
		}

		// find principals that are member of one or many roles
		// the principal must be member in all supplied roles.
		String roleConstraints = null;
		if (queryContext.getAssociatedRoles() != null && queryContext.getAssociatedRoles().size() > 0
				&& queryContext.getAssociatedRoles().get(0).length() > 0) {
			int cnt = 1;
			for (String roleName : queryContext.getAssociatedRoles()) {
				if (roleConstraints == null) {
					roleConstraints = "r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + roleName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='role' AND r"
							+ cnt + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				} else {
					roleConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + roleName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='role' AND r"
							+ cnt + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				}
			}
			fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
			cnt++;
		}

		// find principals that are member of one or many groups
		// the principal must be member in all supplied groups.
		String groupConstraints = null;
		if (queryContext.getAssociatedGroups() != null && queryContext.getAssociatedGroups().size() > 0
				&& queryContext.getAssociatedGroups().get(0).length() > 0) {
			int cnt = 1;
			for (String groupName : queryContext.getAssociatedGroups()) {
				if (groupConstraints == null) {
					groupConstraints = "r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + groupName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='group' AND r"
							+ cnt + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				} else {
					groupConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".TO_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + groupName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='group' AND r"
							+ cnt + ".FROM_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				}
			}
			fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
			cnt++;
		}

		// find principals that contain one or many users
		// the principal must contain all supplied users.
		String userConstraints = null;
		if (queryContext.getAssociatedUsers() != null && queryContext.getAssociatedUsers().size() > 0) {
			int cnt = 1;
			for (String userName : queryContext.getAssociatedGroups()) {
				if (userConstraints == null) {
					userConstraints = "r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".FROM_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + userName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='user' AND r"
							+ cnt + ".TO_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				} else {
					userConstraints = " AND r" + cnt + ".ASSOC_NAME='" + JetspeedPrincipalAssociationType.IS_MEMBER_OF
							+ "' AND r" + cnt + ".FROM_PRINCIPAL_ID=rp" + cnt + ".PRINCIPAL_ID AND rp" + cnt
							+ ".PRINCIPAL_NAME LIKE '" + userName + "' AND rp" + cnt + ".PRINCIPAL_TYPE='group' AND r"
							+ cnt + ".TO_PRINCIPAL_ID=SECURITY_PRINCIPAL.PRINCIPAL_ID";
				}
			}
			fromPart += ", SECURITY_PRINCIPAL_ASSOC r" + cnt + ", SECURITY_PRINCIPAL rp" + cnt;
			cnt++;
		}

		if (attributeConstraint != null) {
			if (constraint != null) {
				constraint += " AND " + attributeConstraint;
			} else {
				constraint = attributeConstraint;
			}
		}

		if (roleConstraints != null) {
			if (constraint != null) {
				constraint += " AND " + roleConstraints;
			} else {
				constraint = roleConstraints;
			}
		}

		if (groupConstraints != null) {
			if (constraint != null) {
				constraint += " AND " + groupConstraints;
			} else {
				constraint = groupConstraints;
			}
		}

		if (userConstraints != null) {
			if (constraint != null) {
				constraint += " AND " + userConstraints;
			} else {
				constraint = userConstraints;
			}
		}

		String baseSqlStr = "SELECT SECURITY_PRINCIPAL.* from " + fromPart + " WHERE SECURITY_PRINCIPAL.PRINCIPAL_TYPE='"
				+ queryContext.getJetspeedPrincipalType() + "' AND SECURITY_PRINCIPAL.DOMAIN_ID="
				+ queryContext.getSecurityDomain();
		
		if (constraint != null) {
			baseSqlStr += " AND " + constraint;
		}
		
		if (queryContext.getOrder() != null && queryContext.getOrder().equalsIgnoreCase("desc")) {
			baseSqlStr += " ORDER BY SECURITY_PRINCIPAL.PRINCIPAL_NAME DESC";
		} else {
			baseSqlStr += " ORDER BY SECURITY_PRINCIPAL.PRINCIPAL_NAME";
		}
		return baseSqlStr;
	}

	/**
	 * Add database specific paging to the SQL.
	 * 
	 * @param sql
	 * @param queryContext
	 * @return
	 */
	protected abstract String getPagingSql(String sql, JetspeedPrincipalQueryContext queryContext);

	/**
	 * Add database specific code for scrolling the dataset to the specified
	 * offset.
	 * 
	 * @param con
	 * @param rs
	 * @param offset
	 * @throws SQLException
	 */
	protected abstract void scrollToOffset(Connection con, ResultSet rs, long offset) throws SQLException;

}
