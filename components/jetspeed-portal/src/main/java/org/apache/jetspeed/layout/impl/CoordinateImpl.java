/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.layout.impl;

import org.apache.jetspeed.layout.Coordinate;

/**
 * 
 * CoordinateImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>David Gurney</a>
 * @version $Id: $
 */
public class CoordinateImpl implements Coordinate
{

    private int m_iOldCol = -1;

    private int m_iOldRow = -1;

    private int m_iNewCol = -1;

    private int m_iNewRow = -1;

    public CoordinateImpl()
    {
    }

    public CoordinateImpl(int p_iOldCol, int p_iOldRow)
    {
        m_iOldCol = p_iOldCol;
        m_iOldRow = p_iOldRow;
    }

    public CoordinateImpl(int p_iOldCol, int p_iOldRow, int p_iNewCol,
            int p_iNewRow)
    {
        m_iOldCol = p_iOldCol;
        m_iOldRow = p_iOldRow;
        m_iNewCol = p_iNewCol;
        m_iNewRow = p_iNewRow;
    }

    public int getNewCol()
    {
        return m_iNewCol;
    }

    public int getNewRow()
    {
        return m_iNewRow;
    }

    public int getOldCol()
    {
        return m_iOldCol;
    }

    public int getOldRow()
    {
        return m_iOldRow;
    }

}
