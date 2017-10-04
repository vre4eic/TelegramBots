/*******************************************************************************
 * Copyright (c) 2017 VRE4EIC Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package eu.vre4eic.evre;

import java.util.ArrayList;
import java.util.List;



public class BuildVars {
    public static final Boolean debug = false;
    public static final Boolean useWebHook = false;
    public static final int PORT = 8443;
    public static final String EXTERNALWEBHOOKURL = "https://example.changeme.com:" + PORT; // https://(xyz.)externaldomain.tld
    public static final String INTERNALWEBHOOKURL = "https://localhost.changeme.com:" + PORT; // https://(xyz.)localip/domain(.tld)
    public static final String pathToCertificatePublicKey = "./YOURPEM.pem"; //only for self-signed webhooks
    public static final String pathToCertificateStore = "./YOURSTORE.jks"; //self-signed and non-self-signed.
    public static final String certificateStorePassword = "yourpass"; //password for your certificate-store

    public static final String OPENWEATHERAPIKEY = "<your-api-key>";

    public static final String DirectionsApiKey = "<your-api-key>";

    public static final String TRANSIFEXUSER = "<transifex-user>";
    public static final String TRANSIFEXPASSWORD = "<transifex-password>";
    public static final List<Integer> ADMINS = new ArrayList<>();

    public static final String pathToLogs = "./";

    public static final String linkDB = "jdbc:mysql://localhost:3306/YOURDATABSENAME?useUnicode=true&characterEncoding=UTF-8";
    public static final String controllerDB = "com.mysql.jdbc.Driver";
    public static final String userDB = "<your-database-user>";
    public static final String password = "<your-databas-user-password>";

    static {
        // Add elements to ADMIN array here
    }
}
