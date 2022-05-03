package com.smartboard.model;

import java.util.List;

public class User extends BoardItem<Project> {

	private String password;
	private String firstName;
	private String lastName;
	private String imagePath;

	public User(String userName, String password, String firstName, String lastName) throws StringLengthException {
		super(userName);

		if (password.length() > 0 && firstName.length() > 1 && lastName.length() > 1) {
			this.password = password;
			this.firstName = firstName;
			this.lastName = lastName;
		} else {
			throw new StringLengthException("Invalid data length - User not created");
		}

		this.imagePath = "fry_avatar.jpg";
		this.subItems.add(new Project("Project1"));
		this.subItems.get(0).setDefault(true);
	}

	@Override
	public void addSubItem(String subItemName) throws StringLengthException {
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) throws StringLengthException {
		if (firstName.length() > 2) {
			this.firstName = firstName;
		} else {
			throw new StringLengthException("Invalid name length - Name not changed");
		}
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) throws StringLengthException {
		if (lastName.length() > 2) {
			this.lastName = lastName;
		} else {
			throw new StringLengthException("Invalid name length - Name not changed");
		}
	}

	public String getImagePath() {
		return imagePath;
	}

	protected void setImagePath(String path) {
		this.imagePath = path;
	}

}
