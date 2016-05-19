package com.didekin.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: pedro@didekin
 * Date: 13/05/16
 * Time: 11:46
 */
public interface JksInClient {

    InputStream getInputStream() throws IOException;

    String getJksPswd();
}
