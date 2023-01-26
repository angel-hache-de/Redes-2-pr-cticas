/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author angel
 */
public class MessageService {
    private volatile HashMap <String, List<Message>> messages;

    public MessageService() {
        messages = new HashMap<>();
    }

    public synchronized void addMessage(String user, Message message) {
        if(!isUserPresent(user)) 
            messages.put(user, new ArrayList<>());
        
        messages.get(user).add(message);
    }
    
    public synchronized void removeUser(String user) {
        if(isUserPresent(user)) 
            messages.remove(user);
    }

    public List<Message> getMessages(String user) {
        if(!isUserPresent(user)) return new ArrayList();
        return messages.get(user).stream().collect(Collectors.toList());
    }

    private boolean isUserPresent(String user) {
        return messages.get(user) != null;
    }
}
