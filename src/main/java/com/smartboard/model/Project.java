package com.smartboard.model;

public class Project extends BoardItem<Column> {

    private int projectID;
    private boolean isDefault;
    private final String DEF_NAME_SUFFIX = " #";

    public Project(String name) throws StringLengthException {
        super(name);

        this.isDefault = false;
    }

    @Override
    public Column addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Column(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    @Override
    public void setName(String name) throws StringLengthException {
        super.setName(name);

        if (this.isDefault) {
            this.name += DEF_NAME_SUFFIX;
        }
    }

    public void setProjectID(int projectID) { this.projectID = projectID; }

    public int getProjectID() { return projectID; }

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
