package com.eric.util;

/**
 * Created by nalian on 2015/12/16.
 */
public interface Constants {

    String SECURITY_CODE_SESSION_KEY = "security_code_session_key";

    String SESSION_USER_KEY = "login_user";

    String AUTHORIZATIONINFO_PREFIX = "authorizationinfo_";

    String SESSION_PREFIX = "session_";

    String FILE_SERVER = ParsProperFile.getAppProp("file.server");

}
