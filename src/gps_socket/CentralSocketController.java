package gps_socket;

import java.net.ServerSocket;
import java.util.HashMap;

/**
 * Created by mi on 8/31/15.
 */
public class CentralSocketController {
    public static HashMap<Integer, CabGuardServerSocket>  clientSocketList = new HashMap<Integer, CabGuardServerSocket>();
    public static ServerSocket serverSocket = null;
}
