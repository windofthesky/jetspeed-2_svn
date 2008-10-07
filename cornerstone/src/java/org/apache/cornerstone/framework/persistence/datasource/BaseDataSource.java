package org.apache.cornerstone.framework.persistence.datasource;

import org.apache.cornerstone.framework.api.persistence.datasource.IDataSource;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;

public class BaseDataSource extends BaseObject implements IDataSource
{
    public static final String REVISION = "$Revision$";

    public static final String DRIVER_CLASS_NAME = Constant.DRIVER + Constant.DOT + Constant.CLASS_NAME;
    public static final String CONNECTION_URL = Constant.CONNECTION + Constant.DOT + Constant.URL;
    public static final String CONNECTION_USER_NAME = Constant.CONNECTION + Constant.DOT + "userName";
    public static final String CONNECTION_PASSWORD = Constant.CONNECTION + Constant.DOT + "password";

    public static final String CONFIG_PARAMS =
        DRIVER_CLASS_NAME
        + Constant.COMMA + CONNECTION_URL
        + Constant.COMMA + CONNECTION_USER_NAME
        + Constant.COMMA + CONNECTION_PASSWORD;

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.persistence.datasource.IDataSource#getDriverClassName()
	 */
	public String getDriverClassName()
	{
		return _config.getProperty(DRIVER_CLASS_NAME);
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.persistence.datasource.IDataSource#getConnectionUrl()
	 */
	public String getConnectionUrl()
	{
		return _config.getProperty(CONNECTION_URL);
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.persistence.datasource.IDataSource#getUserName()
	 */
	public String getConnectionUserName()
	{
		return _config.getProperty(CONNECTION_USER_NAME);
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.persistence.datasource.IDataSource#getPassword()
	 */
	public String getConnectionPassword()
	{
        return _config.getProperty(CONNECTION_PASSWORD);
	}
}