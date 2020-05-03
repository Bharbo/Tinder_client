package com.tinder.client.support;

import com.tinder.client.domain.User;

import java.util.*;

public class ShowResult {
    public String createMatchesList(List<User> array) {
        StringBuilder builder = new StringBuilder("Список любимцев:\n");
        if (array.size() == 0)
            return builder.toString() + "Любимцев нетъ :'(";
        for (int i = 0; i < array.size(); i++) {
            builder.append("|| ").append(i + 1).append(") ").append(array.get(i).getUsername())
                    .append(array.get(i).getUsername().length() % 2 == 0 ? "  ||" : " ||").append("\n");
        }
        return builder.toString();
    }

    public String separationOnLine(String str) {
        int maxLength = 40;
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder res = new StringBuilder();
        String[] splitStr = str.split(" ");
        for (String s : splitStr) {
            if (stringBuilder.length() <= maxLength) {
                stringBuilder.append(s).append(" ");
                continue;
            }
            res.append(stringBuilder).append(s).append("\n");
            stringBuilder = new StringBuilder();
        }
        res.append(stringBuilder).append("\n");
        return res.toString();
    }

    public String createView(String str) {
        str = separationOnLine(str);
        String[] strSplit = str.split("\n");
        StringBuilder res = new StringBuilder();
        int maxLen = 0;
        for (String s : strSplit) {
            if (maxLen < s.length())
                maxLen = s.length();
        }
        for (String s : strSplit) {
            int indentation = s.length() % 2 == 0 ? (maxLen - s.length()) / 2 + 1 : (maxLen - s.length()) / 2;
            StringBuilder spaces = new StringBuilder();
            for (int i = 0; i < indentation; i++) {
                spaces.append(" ");
            }
            res.append("||  ").append(spaces).append(s).append(spaces)
                    .append(s.length() % 2 == 0 ? "  ||" : "   ||").append("\n");
        }
        StringBuilder upDownScope = new StringBuilder();
        for (int i = 0; i < maxLen + 9; i++) {
            upDownScope.append("-");
        }
        return upDownScope + "\n" + res + upDownScope + "\n";
    }
}
