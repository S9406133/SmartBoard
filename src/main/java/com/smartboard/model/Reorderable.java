/**
 * Interface used by a model class which is reorderable within a containing list
 */

package com.smartboard.model;

public interface Reorderable {

    void setOrderIndex(int orderIndex);

    int getOrderIndex();
}
