package site.diteng.api.demo;

import org.junit.Test;

import cn.cerc.core.DataRow;
import cn.cerc.core.DataSet;

public class DitengServiceTest {

    @Test
    public void test() {
        String json = "{\"head\":{\"CusCode_\":\"C00397\",\"ManageNo_\":\"23422342342\",\"TBDate_\":\"2021/12/07\"},\"body\":[[\"PartCode_\",\"Desc_\",\"Spec_\",\"Num_\",\"SpareNum_\",\"OriUP_\"],[\"0212DYRDR40350\",\"\",\"\",5,0,3],[\"0212DYRDR40350\",\"\",\"\",3,3,3],[\"11L20DLX-1\",\"\",\"\",13,0,1.5]]}";
        DataSet dataSet = new DataSet().setJson(json);
        DitengService svr = new DitengService("tranod.append");
        DataRow headIn = svr.dataIn().head();
        headIn.copyValues(dataSet.head());
//        headIn.setValue("CusCode_", "C00397");// 手动赋值到头部

        // 此处可以循环订单对象，然后追加生成商品列表数据集，这里直接从json数据生成dataSet了
        DataSet dataIn = svr.dataIn();
        while (dataSet.fetch()) {
            dataIn.append();
            dataIn.setValue("PartCode_", dataSet.getString("PartCode_"));
            dataIn.setValue("Desc_", dataSet.getString("Desc_"));
            dataIn.setValue("Spec_", dataSet.getString("Spec_"));
            dataIn.setValue("Num_", dataSet.getString("Num_"));
            dataIn.setValue("SpareNum_", dataSet.getString("SpareNum_"));
            dataIn.setValue("OriUP_", dataSet.getString("OriUP_"));
        }

        if (svr.exec())
            System.out.println(svr.dataOut());
        else
            System.out.println(svr.dataOut());
    }

}
