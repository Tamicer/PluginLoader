package com.plugin.database;


import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.plugin.util.PaLog;


/**
 * Cursor封装类
 * Created by LIUYONGKUI726 on 2015-12-02.
 */
@SuppressWarnings("ALL")
public class PaCursor implements Cursor {
    /**
     * 真实游标
     */
    private Cursor mCursor;

    /**
     * 游标
     *
     * @param aCursor
     *            真实游标
     */
    public PaCursor(Cursor aCursor) {
        mCursor = aCursor;
    }

    /**
     * Returns the numbers of rows in the cursor.
     *
     * @return the number of rows in the cursor.
     */
    @Override
    public int getCount() {
        try {
            if (mCursor != null) {
                return mCursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the current position of the cursor in the row set. The value is
     * zero-based. When the row set is first returned the cursor will be at
     * positon -1, which is before the first row. After the last row is returned
     * another call to next() will leave the cursor past the last entry, at a
     * position of count().
     *
     * @return the current cursor position.
     */
    @Override
    public int getPosition() {
        try {
            if (mCursor != null) {
                return mCursor.getPosition();
            } else {
                return -1;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return -1;
        }
    }

    /**
     * Move the cursor by a relative amount, forward or backward, from the
     * current position. Positive offsets move forwards, negative offsets move
     * backwards. If the final position is outside of the bounds of the result
     * set then the resultant position will be pinned to -1 or count() depending
     * on whether the value is off the front or end of the set, respectively.
     *
     * <p>
     * This method will return true if the requested destination was reachable,
     * otherwise, it returns false. For example, if the cursor is at currently
     * on the second entry in the result set and move(-5) is called, the
     * position will be pinned at -1, and false will be returned.
     *
     * @param offset
     *            the offset to be applied from the current position.
     * @return whether the requested move fully succeeded.
     */
    @Override
    public boolean move(int offset) {
        try {
            return (mCursor != null && mCursor.move(offset));
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Move the cursor to an absolute position. The valid range of values is -1
     * &lt;= position &lt;= count.
     *
     * <p>
     * This method will return true if the request destination was reachable,
     * otherwise, it returns false.
     *
     * @param position
     *            the zero-based position to move to.
     * @return whether the requested move fully succeeded.
     */
    @Override
    public boolean moveToPosition(int position) {
        try {
            return (mCursor != null && mCursor.moveToPosition(position));
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Move the cursor to the first row.
     *
     * <p>
     * This method will return false if the cursor is empty.
     *
     * @return whether the move succeeded.
     */
    @Override
    public boolean moveToFirst() {
        try {
            return (mCursor != null && mCursor.moveToFirst());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Move the cursor to the last row.
     *
     * <p>
     * This method will return false if the cursor is empty.
     *
     * @return whether the move succeeded.
     */
    @Override
    public boolean moveToLast() {
        try {
            return (mCursor != null && mCursor.moveToLast());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Move the cursor to the next row.
     *
     * <p>
     * This method will return false if the cursor is already past the last
     * entry in the result set.
     *
     * @return whether the move succeeded.
     */
    @Override
    public boolean moveToNext() {
        try {
            return (mCursor != null && mCursor.moveToNext());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Move the cursor to the previous row.
     *
     * <p>
     * This method will return false if the cursor is already before the first
     * entry in the result set.
     *
     * @return whether the move succeeded.
     */
    @Override
    public boolean moveToPrevious() {
        try {
            return (mCursor != null && mCursor.moveToPrevious());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Returns whether the cursor is pointing to the first row.
     *
     * @return whether the cursor is pointing at the first entry.
     */
    @Override
    public boolean isFirst() {
        try {
            return (mCursor != null && mCursor.isFirst());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Returns whether the cursor is pointing to the last row.
     *
     * @return whether the cursor is pointing at the last entry.
     */
    @Override
    public boolean isLast() {
        try {
            return (mCursor != null && mCursor.isLast());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Returns whether the cursor is pointing to the position before the first
     * row.
     *
     * @return whether the cursor is before the first result.
     */
    @Override
    public boolean isBeforeFirst() {
        try {
            return (mCursor != null && mCursor.isBeforeFirst());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }

    /**
     * Returns whether the cursor is pointing to the position after the last
     * row.
     *
     * @return whether the cursor is after the last result.
     */
    @Override
    public boolean isAfterLast() {
        try {
            return (mCursor != null && mCursor.isAfterLast());
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return false;
        }
    }
    /**
     * Returns the zero-based index for the given column name, or -1 if the
     * column doesn't exist. If you expect the column to exist use
     * {@link #getColumnIndexOrThrow(String)} instead, which will make the error
     * more clear.
     *
     * @param columnName
     *            the name of the target column.
     * @return the zero-based column index for the given column name, or -1 if
     *         the column name does not exist.
     * @see #getColumnIndexOrThrow(String)
     */
    @Override
    public int getColumnIndex(String columnName) {
        try {
            if (mCursor != null) {
                return mCursor.getColumnIndex(columnName);
            } else {
                return -1;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return -1;
        }
    }

    /**
     * Returns the zero-based index for the given column name, or throws
     * {@link IllegalArgumentException} if the column doesn't exist. If you're
     * not sure if a column will exist or not use
     * {@link #getColumnIndex(String)} and check for -1, which is more efficient
     * than catching the exceptions.
     *
     * @param columnName
     *            the name of the target column.
     * @return the zero-based column index for the given column name
     * @see #getColumnIndex(String)
     */
    @Override
    public int getColumnIndexOrThrow(String columnName) {
        try {
            if (mCursor != null) {
                return mCursor.getColumnIndexOrThrow(columnName);
            } else {
                return -1;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return -1;
        }
    }

    /**
     * Returns the column name at the given zero-based column index.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the column name for the given column index.
     */
    @Override
    public String getColumnName(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getColumnName(columnIndex);
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return null;
        }
    }

    /**
     * Returns a string array holding the names of all of the columns in the
     * result set in the order in which they were listed in the result.
     *
     * @return the names of the columns returned in this query.
     */
    @Override
    public String[] getColumnNames() {
        try {
            if (mCursor != null) {
                return mCursor.getColumnNames();
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return null;
        }
    }

    /**
     * Return total number of columns
     *
     * @return number of columns
     */
    @Override
    public int getColumnCount() {
        try {
            if (mCursor != null) {
                return mCursor.getColumnCount();
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the value of the requested column as a byte array.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null or the column type is not a blob type is
     * implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a byte array.
     */
    @Override
    public byte[] getBlob(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getBlob(columnIndex);
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return null;
        }
    }

    /**
     * Returns the value of the requested column as a String.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null or the column type is not a string type is
     * implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a String.
     */
    @Override
    public String getString(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getString(columnIndex);
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return null;
        }
    }

    /**
     * Retrieves the requested column text and stores it in the buffer provided.
     * If the buffer size is not sufficient, a new char buffer will be allocated
     * and assigned to CharArrayBuffer.data
     *
     * @param columnIndex
     *            the zero-based index of the target column. if the target
     *            column is null, return buffer
     * @param buffer
     *            the buffer to copy the text into.
     */
    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        try {
            if (mCursor != null) {
                mCursor.copyStringToBuffer(columnIndex, buffer);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
        }
    }

    /**
     * Returns the value of the requested column as a short.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null, the column type is not an integral type, or the integer
     * value is outside the range [<code>Short.MIN_VALUE</code>,
     * <code>Short.MAX_VALUE</code>] is implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a short.
     */
    @Override
    public short getShort(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getShort(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the value of the requested column as an int.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null, the column type is not an integral type, or the integer
     * value is outside the range [<code>Integer.MIN_VALUE</code>,
     * <code>Integer.MAX_VALUE</code>] is implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as an int.
     */
    @Override
    public int getInt(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getInt(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the value of the requested column as a long.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null, the column type is not an integral type, or the integer
     * value is outside the range [<code>Long.MIN_VALUE</code>,
     * <code>Long.MAX_VALUE</code>] is implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a long.
     */
    @Override
    public long getLong(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getLong(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the value of the requested column as a float.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null, the column type is not a floating-point type, or the
     * floating-point value is not representable as a <code>float</code> value
     * is implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a float.
     */
    @Override
    public float getFloat(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getFloat(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns the value of the requested column as a double.
     *
     * <p>
     * The result and whether this method throws an exception when the column
     * value is null, the column type is not a floating-point type, or the
     * floating-point value is not representable as a <code>double</code> value
     * is implementation-defined.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return the value of that column as a double.
     */
    @Override
    public double getDouble(int columnIndex) {
        try {
            if (mCursor != null) {
                return mCursor.getDouble(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns data type of the given column's value. The preferred type of the
     * column is returned but the data may be converted to other types as
     * documented in the get-type methods such as {@link #getInt(int)},
     * {@link #getFloat(int)} etc.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return column value type
     */

    @Override
    public int getType(int columnIndex) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0;
        }
        try {
            if (mCursor != null) {
                return mCursor.getType(columnIndex);
            } else {
                return 0;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;

            }
            return 0;
        }
    }

    /**
     * Returns <code>true</code> if the value in the indicated column is null.
     *
     * @param columnIndex
     *            the zero-based index of the target column.
     * @return whether the column value is null.
     */
    @Override
    public boolean isNull(int columnIndex) {
        try {
            return (mCursor == null || mCursor.isNull(columnIndex));
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return true;
        }
    }

    /**
     * Deactivates the Cursor, making all calls on it fail until
     * {@link #requery} is called. Inactive Cursors use fewer resources than
     * active Cursors. Calling {@link #requery} will make the cursor active
     * again.
     *
     * @deprecated Since {@link #requery()} is deprecated, so too is this.
     */
    @Override
    public void deactivate() {
        try {
            if (mCursor != null) {
                mCursor.deactivate();
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Performs the query that created the cursor again, refreshing its
     * contents. This may be done at any time, including after a call to
     * {@link #deactivate}.
     *
     * Since this method could execute a query on the database and potentially
     * take a while, it could cause ANR if it is called on Main (UI) thread. A
     * warning is printed if this method is being executed on Main thread.
     *
     * @return true if the requery succeeded, false if not, in which case the
     *         cursor becomes invalid.
     * @deprecated Don't use this. Just request a new cursor, so you can do this
     *             asynchronously and update your list view once the new cursor
     *             comes back.
     */
    @Deprecated
    public boolean requery() {
        try {
            return mCursor != null && mCursor.requery();
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return false;
        }
    }

    /**
     * Closes the Cursor, releasing all of its resources and making it
     * completely invalid. Unlike {@link #deactivate()} a call to
     * {@link #requery()} will not make the Cursor valid again.
     */
    @Override
    public void close() {
        try {
            if (mCursor != null) {
                mCursor.close();
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * return true if the cursor is closed
     *
     * @return true if the cursor is closed.
     */
    @Override
    public boolean isClosed() {
        try {
            return mCursor == null || mCursor.isClosed();
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return true;
        }
    }

    /**
     * Register an observer that is called when changes happen to the content
     * backing this cursor. Typically the data set won't change until
     * {@link #requery()} is called.
     *
     * @param observer
     *            the object that gets notified when the content backing the
     *            cursor changes.
     * @see #unregisterContentObserver(android.database.ContentObserver)
     */
    @Override
    public void registerContentObserver(ContentObserver observer) {
        try {
            if (mCursor != null) {
                mCursor.registerContentObserver(observer);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Unregister an observer that has previously been registered with this
     * cursor via {@link #registerContentObserver}.
     *
     * @param observer
     *            the object to unregister.
     * @see #registerContentObserver(ContentObserver)
     */
    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        try {
            if (mCursor != null) {
                mCursor.unregisterContentObserver(observer);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Register an observer that is called when changes happen to the contents
     * of the this cursors data set, for example, when the data set is changed
     * via {@link #requery()}, {@link #deactivate()}, or {@link #close()}.
     *
     * @param observer
     *            the object that gets notified when the cursors data set
     *            changes.
     * @see #unregisterDataSetObserver(android.database.DataSetObserver)
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        try {
            if (mCursor != null) {
                mCursor.registerDataSetObserver(observer);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Unregister an observer that has previously been registered with this
     * cursor via {@link #registerContentObserver}.
     *
     * @param observer
     *            the object to unregister.
     * @see #registerDataSetObserver(DataSetObserver)
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        try {
            if (mCursor != null) {
                mCursor.unregisterDataSetObserver(observer);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Register to watch a content URI for changes. This can be the URI of a
     * specific data row (for example, "content://my_provider_type/23"), or a a
     * generic URI for a content type.
     *
     * @param cr
     *            The content resolver from the caller's context. The listener
     *            attached to this resolver will be notified.
     * @param uri
     *            The content URI to watch.
     */
    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        try {
            if (mCursor != null) {
                mCursor.setNotificationUri(cr, uri);
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
        }
    }

    /**
     * Return the URI at which notifications of changes in this Cursor's data
     * will be delivered, as previously set by {@link #setNotificationUri}.
     *
     * @return Returns a URI that can be used with
     *         {@link ContentResolver#registerContentObserver(android.net.Uri, boolean, ContentObserver)
     *         ContentResolver.registerContentObserver} to find out about
     *         changes to this Cursor's data. May be null if no notification URI
     *         has been set.
     */
    @SuppressWarnings("NewApi")
    @Override
    public Uri getNotificationUri() {
        try {
            if (mCursor != null) {
                return mCursor.getNotificationUri();
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return null;
        }
    }

    /**
     * Returns a bundle of extra values. This is an optional way for cursors to
     * provide out-of-band metadata to their users. One use of this is for
     * reporting on the progress of network requests that are required to fetch
     * data for the cursor.
     *
     * <p>
     * These values may only change when requery is called.
     *
     * @return cursor-defined values, or {@link android.os.Bundle#EMPTY
     *         Bundle.EMPTY} if there are no values. Never <code>null</code>.
     */
    @Override
    public Bundle getExtras() {
        try {
            if (mCursor != null) {
                return mCursor.getExtras();
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return null;
        }
    }

    /**
     * This is an out-of-band way for the the user of a cursor to communicate
     * with the cursor. The structure of each bundle is entirely defined by the
     * cursor.
     *
     * <p>
     * One use of this is to tell a cursor that it should retry its network
     * request after it reported an error.
     *
     * @param extras
     *            extra values, or {@link android.os.Bundle#EMPTY Bundle.EMPTY}.
     *            Never <code>null</code>.
     * @return extra values, or {@link android.os.Bundle#EMPTY Bundle.EMPTY}.
     *         Never <code>null</code>.
     */
    @Override
    public Bundle respond(Bundle extras) {
        try {
            if (mCursor != null) {
                return mCursor.respond(extras);
            } else {
                return null;
            }
        } catch (Exception e) {
            PaLog.d("Cursor exception: " + e);
            if (mCursor != null) {
                try {
                    mCursor.close();
                } catch (Exception exception) {
                    PaLog.d("Cursor close exception: " + e);
                }
                mCursor = null;
            }
            return null;
        }
    }

    /**
     * onMove() will only be called across processes if this method returns
     * true.
     *
     * @return whether all cursor movement should result in a call to onMove().
     */
    @Override
    public boolean getWantsAllOnMoveCalls() {
        return mCursor.getWantsAllOnMoveCalls();
    }
}

