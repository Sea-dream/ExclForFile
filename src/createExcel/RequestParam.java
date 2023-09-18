package createExcel;

/**
 * @author caiyongqing
 * @date 2023-09-18
 */
public class RequestParam {
    private String clientId = "";
    private String clientSecret = "==";
    private String corNum = "";

    private Integer currPage;

    public String getClientId() {
        return clientId;
    }

    public RequestParam setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public RequestParam setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getCorNum() {
        return corNum;
    }

    public RequestParam setCorNum(String corNum) {
        this.corNum = corNum;
        return this;
    }

    public Integer getCurrPage() {
        return currPage;
    }

    public RequestParam setCurrPage(Integer currPage) {
        this.currPage = currPage;
        return this;
    }
}
