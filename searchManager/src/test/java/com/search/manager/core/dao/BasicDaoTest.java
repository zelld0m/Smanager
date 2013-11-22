package com.search.manager.core.dao;

import com.search.manager.core.exception.CoreDaoException;

public interface BasicDaoTest<T> {

	void daoWiringTest() throws CoreDaoException;;

	void addTest() throws CoreDaoException;

	void updateTest() throws CoreDaoException;

	void deleteTest() throws CoreDaoException;

	void searchTest() throws CoreDaoException;

	void searchModelTest() throws CoreDaoException;

}
