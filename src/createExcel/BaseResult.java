package createExcel;

import java.util.List;

/**
 * @author caiyongqing
 * @date 2023-09-18
 */
public class BaseResult {
    private String currPage;
    private String sumPage;
    private String success;
    private String message;
    private String msgCode;
    private List<CardInfo> object;
    private String caseNo;
    private String count;

    public String getCurrPage() {
        return currPage;
    }

    public BaseResult setCurrPage(String currPage) {
        this.currPage = currPage;
        return this;
    }

    public String getSumPage() {
        return sumPage;
    }

    public BaseResult setSumPage(String sumPage) {
        this.sumPage = sumPage;
        return this;
    }

    public String getSuccess() {
        return success;
    }

    public BaseResult setSuccess(String success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public BaseResult setMsgCode(String msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    public List<CardInfo> getObject() {
        return object;
    }

    public BaseResult setObject(List<CardInfo> object) {
        this.object = object;
        return this;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public BaseResult setCaseNo(String caseNo) {
        this.caseNo = caseNo;
        return this;
    }

    public String getCount() {
        return count;
    }

    public BaseResult setCount(String count) {
        this.count = count;
        return this;
    }
}
