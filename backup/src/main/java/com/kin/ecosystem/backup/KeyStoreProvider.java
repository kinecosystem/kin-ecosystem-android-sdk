package com.kin.ecosystem.backup;

public interface KeyStoreProvider {

	//TODO we need to define exception
	void exportAccount(String password) throws Exception;

	//TODO we need to define exception
	void importAccount(String password) throws Exception;

	//TODO we need to define exceptions
	void validatePassword(String password) throws Exception;
}
