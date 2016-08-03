package com.plugin.util;

import android.database.Cursor;

import com.plugin.database.PaCursor;

/**
 * Created by LIUYONGKUI726 on 2015-12-02.
 */
public class PaCursorUtils {
    /**
     * Constructor
     */
    private PaCursorUtils() {

    }

    /**
     * 生成游标
     *
     * @param aCursor
     *            原始游标
     * @return BdCursor
     */
    public static PaCursor getCursor(Cursor aCursor) {
        if (aCursor != null) {
            return new PaCursor(aCursor);
        } else {
            return null;
        }
    }
}
