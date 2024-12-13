package tools;

import com.obs.services.ObsClient;

/**
 * @author caiyongqing
 * @date 2024-12-13
 */
public class MyOBSClientTest {

    private static ObsClient obsClient;

    private static final String endPoint = "https://183.134.72.90";
    private static final String ak = "CNHIA3KC0EF5XAHD30AC";
    private static final String sk = "JZXcEGLOfiOsCX0cSxcudLeeMpnYU1Y1nAfcuG3Y";

    public static final String bucket = "testflex";

    public static ObsClient getObsClient() {
        if (obsClient == null) {
            obsClient = new ObsClient(ak, sk, endPoint);
        }
        return obsClient;
    }
}
