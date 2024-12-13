package tools;

import com.obs.services.ObsClient;

/**
 * @author caiyongqing
 * @date 2024-12-13
 */
public class MyOBSClientRelease {

    private static ObsClient obsClient;

    private static final String endPoint = "https://183.134.72.90";
    private static final String ak = "QDKU0HPLR9X40EEA7DBR";
    private static final String sk = "eoPXA7z541ihdBSMHsxJNAkF5LTOFcePLZ3wLVQc";

    public static final String bucket = "docflex";

    public static ObsClient getObsClient() {
        if (obsClient == null) {
            obsClient = new ObsClient(ak, sk, endPoint);
        }
        return obsClient;
    }
}
