package co.jp.xeex.chat.base;

import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.util.ConvertUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class ServiceBaseImpl<Request extends RequestBase, Response>
        implements ServiceBase<Request, Response> {

    /**
     * This method is called before processing the request.<br>
     * You can override this method to initialize the process.
     * 
     * @param request
     */
    protected void innitProcess(Request req) {
        // do nothing
        log.debug("Initialize process");
    }

    /**
     * This method is called after processing the request.<br>
     * You can override this method to do something after processing.
     * 
     * @param req
     * @param rep
     */
    protected void afterProcess(Request req, Response rep) {
        log.debug("Finished processing");
    }

    /**
     * This method is the main processes in executing workflow.<br>
     * Execute the business logic to make the business OutputData as a response data
     * object.
     * 
     * @param req The request object.
     * @return OutputData object
     * @throws Exception
     */
    protected abstract Response processRequest(Request req) throws BusinessException;

    @Override
    public Response execute(Request req) throws BusinessException {
        try {
            // innit process
            this.innitProcess(req);

            // Execute process logic
            log.debug("Start processing request: " + ConvertUtil.toSimpleName(req) + "=" + ConvertUtil.toJson(req));
            Response out = this.processRequest(req);

            // after process
            this.afterProcess(req, out);

            return out;
        } catch (BusinessException e) {
            throw e;
        }
    }

}
