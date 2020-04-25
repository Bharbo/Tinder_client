package com.tinder.client.support;

import org.springframework.stereotype.Component;

@Component
public class ShowResult {
    public String createMatchesList(Object[] array) {
        StringBuilder builder = new StringBuilder("Список любимцев:\n");
        if (array.length == 0)
            return builder.toString() + "Любимцевъ нетъ :'(";
        for (int i = 0; i < array.length; i++) {
            builder.append(i + 1).append(") ").append(array[i]).append("\n");
        }
        return builder.toString();
    }
}
