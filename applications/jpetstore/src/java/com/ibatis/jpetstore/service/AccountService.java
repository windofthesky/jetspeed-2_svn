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
package com.ibatis.jpetstore.service;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.jpetstore.domain.Account;
import com.ibatis.jpetstore.persistence.DaoConfig;
import com.ibatis.jpetstore.persistence.iface.AccountDao;

import java.util.List;

/**
 * <p/>
 * Date: Mar 6, 2004 11:22:43 PM
 *
 * @author Clinton Begin
 */
public class AccountService {

  /* Constants */

  private static final AccountService instance = new AccountService();

  /* Private Fields */

  private DaoManager daoManager = DaoConfig.getDaomanager();

  private AccountDao accountDao;

  /* Constructors */

  public AccountService() {
    accountDao = (AccountDao) daoManager.getDao(AccountDao.class);
  }

  /* Public Methods */

  public static AccountService getInstance() {
    return instance;
  }

  /* ACCOUNT */

  public Account getAccount(String username) {
    return accountDao.getAccount(username);
  }

  public Account getAccount(String username, String password) {
    return accountDao.getAccount(username, password);
  }

  public void insertAccount(Account account) {
    accountDao.insertAccount(account);
  }

  public void updateAccount(Account account) {
    accountDao.updateAccount(account);
  }

  public List getUsernameList() {
    return accountDao.getUsernameList();
  }

}
