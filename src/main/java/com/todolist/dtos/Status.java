package com.todolist.dtos;

public enum Status {
    DRAFT,
    IN_PROGRESS,
    IN_REVISION,
    DONE,
    CANCELLED;

    public static Status parse(String status) {
        String statusLowerCase = status.toLowerCase();
        switch (statusLowerCase) {
            case "draft":
                return DRAFT;
            case "in_progress":
            case "in progress":
                return IN_PROGRESS;
            case "in_revision":
            case "in revision":
                return IN_REVISION;
            case "done":
                return DONE;
            case "cancelled":
                return CANCELLED;
            default:
                return null;
        }
    }
}
