/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:18:30 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.jpetstore.domain.Item;
import com.ibatis.jpetstore.domain.Order;

public interface ItemDao {

  public void updateQuantity(Order order);

  public boolean isItemInStock(String itemId);

  public PaginatedList getItemListByProduct(String productId);

  public Item getItem(String itemId);

}
