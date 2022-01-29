package com.legionmodding.energetictools.util;

import java.util.HashMap;
import java.util.Map;

public enum AxeModes
{
    MODE_STRIP("s", "strip"),
    MODE_HEAL("h", "heal");

    private static final Map<String,AxeModes> CODE_TO_MODE = new HashMap<>();

    private final String code;
    private final String name;

    static
    {
        for (AxeModes mode : values())
        {
            CODE_TO_MODE.put(mode.getCode(), mode);
        }
    }

    AxeModes(String code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public static AxeModes getMode(String code)
    {
        return CODE_TO_MODE.get(code);
    }
}
