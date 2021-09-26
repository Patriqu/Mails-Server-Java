package com.patriqu.mail.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ethenq
 */
public class DataTools {
    
    private static List<String> usersOnline = new ArrayList<>();
    private static Map<String, ClientHandler> clients = new HashMap<>();
    
    public DataTools() {}
    
    public void addClient(String user, ClientHandler client)
    {
        clients.put(user, client);
    }
    
    public int addUser(String us)
    {
        usersOnline.add(us);
        
        return 1;
    }
            
    public int deleteUser(String us)
    {
        usersOnline.remove(us);
        
        return 1;
    }
    
    public ClientHandler getClient(String name)
    {
        return clients.get(name);
    }
    
    public boolean isOnline(String us)
    {
        return usersOnline.contains(us);
    }
}
