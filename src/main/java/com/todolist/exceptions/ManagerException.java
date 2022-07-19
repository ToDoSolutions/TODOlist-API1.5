package com.todolist.exceptions;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class ManagerException {

    private LocalDate timestamp;
    private String msg;
    private String path;
    private String status;

    /*
    private ManagerException(HttpStatusCodeException exception) {
        Map<String, String> info;
        try {
            info = new HashMap<>(Splitter.on(",")
                    .withKeyValueSeparator(":")
                    .split(exception.getResponseBodyAsString().replace("{", "").replace("}", "").replace("\"", "")));
            this.timestamp = LocalDate.parse(info.get("timestamp"));
            this.msg = info.get("msg").trim();
            this.path = info.get("path").trim();
            this.status = info.get("status").trim();
        } catch (Exception e) {
            info = new HashMap<>(Splitter.on(",")
                    .withKeyValueSeparator(":")
                    .split(
                            exception.getResponseBodyAsString()
                                    .replace("{", "")
                                    .replace("}", "")
                                    .replace("\"", "")
                                    .substring(40)
                    ));
            this.timestamp = LocalDate.now();
            this.msg = "No information";
            this.path = info.get("path");
            this.status = info.get("error");
        }
    }

    public static ManagerException of(HttpStatusCodeException exception) {
        return new ManagerException(exception);
    }
     */

    public ManagerException assertStatus(String expected) {
        if (!Objects.equals(status, expected))
            throw new AssertionError("Error Status: " + status + " != " + expected);
        return this;
    }

    public ManagerException assertPath(String expected) {
        if (!Objects.equals(path, expected))
            throw new AssertionError("Error Path: " + path + " != " + expected);
        return this;
    }

    public ManagerException assertMsg(String expected) {
        if (!Objects.equals(this.msg, expected))
            throw new AssertionError("Error Message: " + this.msg + " != " + expected);
        return this;
    }
}
