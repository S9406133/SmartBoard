package com.smartboard.model;

import java.util.List;

public class User extends BoardItem<Project> {

	private String password;
	private String firstName;
	private String lastName;
	private String picturePath;

	public User(String userName, String password, String firstName, String lastName) throws StringLengthException {
		super(userName);

		if (password.length() > 0 && firstName.length() > 2 && lastName.length() > 2) {
			this.password = password;
			this.firstName = firstName;
			this.lastName = lastName;
		} else {
			throw new StringLengthException("Invalid data length - User not created");
		}

		this.picturePath = "fry_avatar.jpg";
		this.subItems.add(new Project("Project1"));
		this.subItems.get(0).setDefault(true);
	}

	@Override
	protected void addSubItem(String subItemName) throws StringLengthException {
		this.subItems.add(new Project(subItemName));

		if ((getDefaultProject() == null)) {
			this.subItems.get(0).setDefault(true);
		}
	}

	protected boolean validateLogin(String username, String password) {
		return this.name.equalsIgnoreCase(username) && this.password.equals(password);
	}

	protected Project getDefaultProject() {
		Project defaultProject = null;

		for (Project project : this.subItems) {
			if (project.isDefault()) {
				defaultProject = project;
			}
		}

		return defaultProject;
	}

	protected void setDefaultProject(int index) throws IndexOutOfBoundsException {
		List<Project> projectList = this.subItems;

		if (!projectList.get(index).isDefault()) {
			getDefaultProject().setDefault(false);
			projectList.get(index).setDefault(true);
		}

	}

	protected String getFirstName() {
		return firstName;
	}

	protected void setFirstName(String firstName) throws StringLengthException {
		if (firstName.length() > 2) {
			this.firstName = firstName;
		} else {
			throw new StringLengthException("Invalid name length - Name not changed");
		}
	}

	protected String getLastName() {
		return lastName;
	}

	protected void setLastName(String lastName) throws StringLengthException {
		if (lastName.length() > 2) {
			this.lastName = lastName;
		} else {
			throw new StringLengthException("Invalid name length - Name not changed");
		}
	}

	protected String getPicturePath() {
		return picturePath;
	}

	protected void setPicturePath(String picture) {
		this.picturePath = picture;
	}

}
