package com.smartboard.model;

public class Project extends BoardItem<Column> {
	
	private boolean isDefault;

	public Project(String name) throws StringLengthException {
		super(name);
		
		this.isDefault = false;
		this.subItems.add(new Column("To Do"));
		this.subItems.add(new Column("Doing"));
		this.subItems.add(new Column("Done"));
	}

	@Override
	protected void addSubItem(String subItemName) throws StringLengthException {
		this.subItems.add(new Column(subItemName));
	}

	protected boolean isDefault() {
		return isDefault;
	}

	protected void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
		
		if (isDefault) {
			this.name += " #";
		}else {
			int index = this.name.indexOf(" #");
			if (index > 1) {
				this.name = this.name.substring(0, index).strip();
			}
		}
	}

}
