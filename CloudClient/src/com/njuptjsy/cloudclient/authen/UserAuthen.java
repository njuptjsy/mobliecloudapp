package com.njuptjsy.cloudclient.authen;

import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

public interface UserAuthen extends Runnable{

	public void login();
	public void sendLoginResult(MESSAGE_TYPE msgType);
}
