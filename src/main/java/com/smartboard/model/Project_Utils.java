package com.smartboard.model;

public class Project_Utils {

    public static Project addNewProject(String name) throws StringLengthException {
        Project newProject = User_Utils.currentUser.addSubItem(name);
        DB_Utils.InsertNewProject(newProject);
        return newProject;
    }

    public static void updateProjectName(Project project, String name) throws StringLengthException {
        project.setName(name);
        DB_Utils.UpdateProject(project);
    }

    public static void updateProjectDefault(Project project, boolean isDefault) {
        project.setDefault(isDefault);
        DB_Utils.UpdateProject(project);
    }
}
