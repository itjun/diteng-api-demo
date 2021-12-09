package site.diteng.api.demo;

import java.io.IOException;

import cn.cerc.core.DataRow;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.mis.core.ServiceState;
import lombok.extern.slf4j.Slf4j;

/**
 * 地藤远程服务调用工具
 */
@Slf4j
public class DitengService {

    /**
     * 地藤对外提供的服务访问地址
     */
    private static final String API_URL = "http://127.0.0.1/services/";
    // private static final String API_URL = "https://www.diteng.site/services/";

    /**
     * 测试帐套使用的token，正式使用请根据实际帐套分配的token
     */
    private static final String API_TOKEN = "0098d9af12a94b37a8112629c9d580cb";

    private String service;
    private DataSet dataIn;
    private DataSet dataOut;

    public DitengService(String service) {
        this.service = service;
    }

    public boolean exec(Object... args) {
        if (args.length > 0) {
            DataRow headIn = dataIn().head();
            if (args.length % 2 != 0) {
                throw new RuntimeException("传入的参数数量必须为偶数！");
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setValue(args[i].toString(), args[i + 1]);
            }
        }

        log.debug(this.service);
        if (Utils.isEmpty(this.service)) {
            this.setMessage("服务代码不允许为空");
            return false;
        }

        String requestUrl = this.getRequestUrl(this.service());
        try {
            Curl client = new Curl();
            client.put(ISession.TOKEN, API_TOKEN);
            client.put("dataIn", dataIn().json());

            String response = null;
            try {
                response = client.doPost(requestUrl);
                log.debug("response: {}", response);
            } catch (IOException e) {
                dataOut().setState(ServiceState.CALL_TIMEOUT).setMessage("远程服务异常");
                return false;
            }
            this.dataOut().setJson(response);

            return dataOut().state() > ServiceState.ERROR;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null) {
                setMessage(e.getCause().getMessage());
            } else {
                setMessage(e.getMessage());
            }
            return false;
        }
    }

    public final String service() {
        return service;
    }

    public final DitengService setService(String service) {
        this.service = service;
        return this;
    }

    public final String message() {
        return dataOut().message();
    }

    public final void setMessage(String message) {
        dataOut().setMessage(message);
    }

    public final DataSet dataOut() {
        if (dataOut == null)
            dataOut = new DataSet();
        return dataOut;
    }

    protected void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    public final DataSet dataIn() {
        if (dataIn == null)
            dataIn = new DataSet();
        return dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    public String getRequestUrl(String service) {
        return String.format("%s/%s", API_URL, service);
    }

}
