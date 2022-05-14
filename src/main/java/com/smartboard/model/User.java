package com.smartboard.model;

public class User extends BoardItem<Project> {

    private final String password;
    private String firstName;
    private String lastName;
    private String imagePath;
    private final int MIN_NAME_LENGTH = 2;

    public User(String userName, String password, String firstName, String lastName) throws StringLengthException {
        super(userName);

        if (password.length() > 0 && firstName.length() > MIN_NAME_LENGTH && lastName.length() > MIN_NAME_LENGTH) {
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        } else {
            throw new StringLengthException("Invalid data length - User not created");
        }

        this.imagePath = "fry_avatar.jpg";
    }

    @Override
    public Project addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Project(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public boolean validateLogin(String username, String password) {
        return this.name.equalsIgnoreCase(username) && this.password.equals(password);
    }

    public Project getDefaultProject() {
        Project defaultProject = null;

        for (Project project : this.subItems) {
            if (project.isDefault()) {
                defaultProject = project;
                break;
            }
        }

        return defaultProject;
    }

    public void toggleDefaultProject(int index) throws IndexOutOfBoundsException {

        if (!this.subItems.get(index).isDefault()) {
            if (getDefaultProject() != null) {
                getDefaultProject().setDefault(false);
            }
            this.subItems.get(index).setDefault(true);

        } else {
            this.subItems.get(index).setDefault(false);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws StringLengthException {
        if (firstName.length() > MIN_NAME_LENGTH) {
            this.firstName = firstName;
        } else {
            throw new StringLengthException("Invalid name length - Name not changed");
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) throws StringLengthException {
        if (lastName.length() > MIN_NAME_LENGTH) {
            this.lastName = lastName;
        } else {
            throw new StringLengthException("Invalid name length - Name not changed");
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

}
