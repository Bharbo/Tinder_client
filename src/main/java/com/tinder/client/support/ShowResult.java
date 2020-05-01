package com.tinder.client.support;

import com.tinder.client.domain.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//@Component
public class ShowResult {
    public String createMatchesList(List<User> array) {
        StringBuilder builder = new StringBuilder("Список любимцев:\n");
        if (array.size() == 0)
            return builder.toString() + "Любимцев нетъ :'(";
        for (int i = 0; i < array.size(); i++) {
            builder.append(i + 1).append(") ").append(array.get(i).getUsername()).append("\n");
//            builder.append(i + 1).append(") ").append(array[i].getUsername()).append("\n");
        }
        return builder.toString();
    }

//    public static void main(String[] args) {
//        Collection<User> array = new HashSet<>();
//        array.add (new User(1L, "сударыня", "1", "1", "1"));
//        array.add (new User(2L, "сударь", "2", "2", "2"));
//        array.add (new User(3L, "сударь", "3", "3", "3"));
//        String res = createMatchesList(array);
//        System.out.println(res);
//    }
}
