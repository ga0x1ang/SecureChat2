package common;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/18/13
 * Time: 3:58 PM
 */
public final class Dispatcher {

    private Gson gson;
    private SCLogger logger;

    public Dispatcher() throws IOException {
        this.gson = new Gson();
        this.logger = new SCLogger("test.log");
    }

    /**
     * dispatch the received message to specified object with specified content
     * invoke corresponding methods of the object
     *
     * @param obj : the destination (object)
     * @param cnt :
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public final void dispatch(Object obj, String cnt) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        logger.log(obj.getClass().getName(), cnt);

        Command msg = this.gson.fromJson(cnt, Command.class); // resolve the string format message to Command object
        Method method = obj.getClass().getMethod(msg.getInstruction(), msg.getParameters().getClass());
        method.invoke(obj, new Object[]{msg.getParameters()});
    }
}
