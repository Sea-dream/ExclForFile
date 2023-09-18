package createExcel;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author caiyongqing
 * @date 2023-09-18
 */
public class MainFile {
    public static void main(String[] args) {
        Workbook workbook = new Workbook();
        workbook.loadFromFile("F:\\test\\在所员工商务卡卡号.xlsx");
        Worksheet sheet = workbook.getWorksheets().get(0);
        requestCreate(1, sheet, 0);
        workbook.save();
    }

    private static void requestCreate(Integer pageNumber, Worksheet sheet, Integer dtt) {
        String requestUrl = "";
        String requestString = JSONUtil.toJsonPrettyStr(new BaseParam().setParaMap(new RequestParam().setCurrPage(pageNumber)));
        String resultString = HttpUtil.post(requestUrl, requestString);
        BaseResult responseBase = JSONUtil.toBean(resultString,new TypeReference<BaseResult>() {}, true);
        for (int row=2+dtt; row<2+responseBase.getObject().size()+dtt; row++) {
            int index = row - 2 - dtt;
            CardInfo currentCardInfo = responseBase.getObject().get(index);
            for (int column=1; column<7; column++)
            if (column == 1) {
                CellRange cell = sheet.getCellRange(row, column);
                cell.setValue(String.valueOf(row-1));
            } else if (column == 2) {
                CellRange cell = sheet.getCellRange(row, column);
                cell.setValue(currentCardInfo.getHolderChiName());
            } else if (column == 3) {
                CellRange cell = sheet.getCellRange(row, column);
                cell.setValue(currentCardInfo.getCardNumber().substring(3)+"c");
            } else if (column == 4) {
                CellRange cell = sheet.getCellRange(row, column);
                cell.setValue(currentCardInfo.getActivationDate());
            } else if (column == 5) {
                CellRange cell = sheet.getCellRange(row, column);
                cell.setValue(currentCardInfo.getCirculationFlag());
            } else if (column == 6) {
                CellRange cell = sheet.getCellRange(row, column);
                if (currentCardInfo.getCirculationFlag().equals("N")) {
                    cell.setValue(currentCardInfo.getCancelDate());
                } else {
                    cell.setValue("");
                }
            }
        }
        System.out.println("当前页码：" + pageNumber);
        if (pageNumber < Integer.parseInt(responseBase.getSumPage())) {
            requestCreate(pageNumber+1, sheet, responseBase.getObject().size() + dtt);
        }
    }
}
