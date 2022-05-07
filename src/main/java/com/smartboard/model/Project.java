package com.smartboard.model;

public class Project extends BoardItem<Column> {

    private boolean isDefault;
    private final String DEF_NAME_SUFFIX = " #";

    public Project(String name) throws StringLengthException {
        super(name);

        this.isDefault = false;
        this.subItems.add(new Column("To Do"));
        this.subItems.add(new Column("Doing"));
        this.subItems.add(new Column("Done"));
    }

    @Override
    public void addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Column(subItemName));
    }

    @Override
    public void setName(String name) throws StringLengthException {
        super.setName(name);

        if (this.isDefault) {
            this.name += DEF_NAME_SUFFIX;
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;

        if (isDefault) {
            this.name += DEF_NAME_SUFFIX;
        } else {
            int index = this.name.indexOf(DEF_NAME_SUFFIX);
            if (index > 1) {
                this.name = this.name.substring(0, index).strip();
            }
        }
    }

}
