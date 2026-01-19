package Network;

import java.io.Serializable;

public enum CommandType implements Serializable {
    HELP,
    INFO,
    SHOW,
    ADD,
    UPDATE,
    REMOVE_BY_ID,
    CLEAR,
    ADD_IF_MAX,
    ADD_IF_MIN,
    REMOVE_LOWER,
    REMOVE_ANY_BY_DISTANCE,
    FILTER_GREATER_THAN_DISTANCE,
    PRINT_FIELD_DESCENDING_DISTANCE,
    EXIT
}
